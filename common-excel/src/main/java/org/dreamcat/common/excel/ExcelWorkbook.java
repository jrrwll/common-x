package org.dreamcat.common.excel;

import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.common.excel.content.ExcelPictureData;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.common.util.ListUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by tuke on 2020/7/21
 */
public class ExcelWorkbook<T extends IExcelSheet> implements IExcelWorkbook<T> {

    @Getter
    final List<T> sheets = new ArrayList<>();
    final Map<ExcelFont, Font> fonts = new HashMap<>();
    @Getter
    final List<ExcelStyle> styles = new ArrayList<>();
    final List<Font> reservedFonts = new ArrayList<>();
    final List<CellStyle> reservedStyles = new ArrayList<>();
    final Map<ExcelPictureData, Integer> pictureDatas = new LinkedHashMap<>();

    boolean date1904;

    public static ExcelWorkbook<ExcelSheet> from(File file)
            throws IOException, InvalidFormatException {
        if (FileUtil.suffix(file.getName()).equalsIgnoreCase("xls")) {
            return from2003(file);
        }
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> fromBigGrid(File file)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new SXSSFWorkbook(new XSSFWorkbook(file))) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> from2003(File file) throws IOException {
        try (Workbook workbook = new HSSFWorkbook(new POIFSFileSystem(file, true))) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> from(Workbook workbook) {
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();
        // font
        int fontNum = workbook.getNumberOfFonts();
        for (int i = 0; i < fontNum; i++) {
            Font font = workbook.getFontAt(i);
            ExcelFont excelFont = ExcelFont.from(font);
            book.fonts.put(excelFont, font);
            book.reservedFonts.add(font);
        }
        // cell style
        int cellStyleNum = workbook.getNumCellStyles();
        for (int i = 0; i < cellStyleNum; i++) {
            CellStyle cellStyle = workbook.getCellStyleAt(i);
            ExcelStyle excelStyle = ExcelStyle.from(cellStyle);
            book.styles.add(excelStyle);
            book.reservedStyles.add(cellStyle);
        }
        // picture data
        List<? extends PictureData> pictures = workbook.getAllPictures();
        int pictureIndex = (workbook instanceof HSSFWorkbook) ? 1 : 0; // 0 for xls, 1 for xlsx
        for (PictureData picture : pictures) {
            ExcelPictureData data = ExcelPictureData.from(picture);
            book.pictureDatas.put(data, pictureIndex++);
        }

        // sheet
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            book.sheets.add(ExcelSheet.from(sheet, book));
        }

        // extra
        if (workbook instanceof XSSFWorkbook) {
            XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
            book.date1904 = xssfWorkbook.isDate1904();
        } else if (workbook instanceof HSSFWorkbook) {
            HSSFWorkbook hssfWorkbook = (HSSFWorkbook) workbook;
            book.date1904 = hssfWorkbook.getWorkbook().isUsing1904DateWindowing();
        } else if (workbook instanceof SXSSFWorkbook) {
            SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbook;
            book.date1904 = sxssfWorkbook.getXSSFWorkbook().isDate1904();
        }
        return book;
    }

    @Override
    public CellStyle makeCellStyle(IExcelCell excelCell, Workbook workbook) {
        ExcelStyle excelStyle = excelCell.getStyle();
        if (excelStyle == null) return null;

        // style
        CellStyle style = ListUtil.getOrNull(reservedStyles, excelStyle.getIndex());
        if (style != null) return style;

        style = workbook.createCellStyle();
        styles.add(excelStyle);
        reservedStyles.add(style);

        // font
        ExcelFont excelFont = excelCell.getFont();
        if (excelFont != null) {
            Font font = fonts.get(excelFont);
            if (font == null) {
                font = workbook.createFont();
                excelFont.fill(font);
                fonts.put(excelFont, font);
                reservedFonts.add(font);
                style.setFont(font);
            }
        }
        DataFormat dataFormat = workbook.createDataFormat();
        excelStyle.fill(style, dataFormat);
        return style;
    }

    @Override
    public Collection<ExcelPictureData> getPictureDatas() {
        return pictureDatas.keySet();
    }

    @Override
    public int makePictureData(ExcelPictureData pictureData, Workbook workbook) {
        Integer index = pictureDatas.get(pictureData);
        if (index != null) return index;

        index = workbook.addPicture(pictureData.getData(), pictureData.getPictureType());
        pictureDatas.put(pictureData, index);
        return index;
    }
}
