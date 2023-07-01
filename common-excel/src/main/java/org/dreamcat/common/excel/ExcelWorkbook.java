package org.dreamcat.common.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.dreamcat.common.excel.content.ExcelPicture;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ListUtil;

/**
 * Create by tuke on 2020/7/21
 */
public class ExcelWorkbook<T extends IExcelSheet> implements IExcelWorkbook<T> {

    @Getter
    final List<T> sheets;
    final Map<ExcelFont, Font> fonts;
    @Getter
    final List<ExcelStyle> styles;
    final List<Font> reservedFonts;
    final List<CellStyle> reservedStyles;

    @Getter
    final List<ExcelPicture> pictures;
    boolean date1904;

    public ExcelWorkbook() {
        this.sheets = new ArrayList<>();
        this.fonts = new HashMap<>();
        this.reservedFonts = new ArrayList<>();
        this.styles = new ArrayList<>();
        this.reservedStyles = new ArrayList<>();
        this.pictures = new ArrayList<>();
    }

    public static ExcelWorkbook<ExcelSheet> from(File file)
            throws IOException, InvalidFormatException {
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
        ExcelWorkbook<ExcelSheet> self = new ExcelWorkbook<>();
        // font
        int fontNum = workbook.getNumberOfFonts();
        for (int i = 0; i < fontNum; i++) {
            Font font = workbook.getFontAt(i);
            ExcelFont excelFont = ExcelFont.from(font);
            self.fonts.put(excelFont, font);
            self.reservedFonts.add(font);
        }
        // cell style
        int cellStyleNum = workbook.getNumCellStyles();
        for (int i = 0; i < cellStyleNum; i++) {
            CellStyle cellStyle = workbook.getCellStyleAt(i);
            ExcelStyle excelStyle = ExcelStyle.from(cellStyle);
            self.styles.add(excelStyle);
            self.reservedStyles.add(cellStyle);
        }
        workbook.createDataFormat();
        // sheet
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            self.sheets.add(ExcelSheet.from(sheet, self));
        }
        // picture
        List<? extends PictureData> pictures = workbook.getAllPictures();
        for (PictureData picture : pictures) {
            self.pictures.add(ExcelPicture.from(picture));
        }
        // extra
        if (workbook instanceof XSSFWorkbook) {
            XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
            self.date1904 = xssfWorkbook.isDate1904();
        } else if (workbook instanceof HSSFWorkbook) {
            HSSFWorkbook hssfWorkbook = (HSSFWorkbook) workbook;
            self.date1904 = hssfWorkbook.getWorkbook().isUsing1904DateWindowing();
        } else if (workbook instanceof SXSSFWorkbook) {
            SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbook;
            self.date1904 = sxssfWorkbook.getXSSFWorkbook().isDate1904();
        }
        return self;
    }

    @Override
    public CellStyle makeCellStyle(IExcelCell excelCell, Workbook workbook) {
        ExcelStyle excelStyle = excelCell.getStyle();
        if (excelStyle == null) return null;

        // style
        CellStyle style = ListUtil.getOrNull(reservedStyles, excelStyle.getIndex());
        if (style != null) return null;
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
}
