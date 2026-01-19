package org.dreamcat.common.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.common.Pair;
import org.dreamcat.common.excel.content.ExcelPicture;
import org.dreamcat.common.excel.content.ExcelPictureData;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelComment;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelHyperLink;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.common.util.ListUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by tuke on 2020/7/21
 */
public class ExcelWorkbook<T extends IExcelSheet> {

    final List<T> sheets = new ArrayList<>();
    final Map<ExcelFont, Font> fonts = new LinkedHashMap<>();
    final List<ExcelStyle> styles = new ArrayList<>();
    final List<CellStyle> reservedStyles = new ArrayList<>();
    final Map<ExcelPictureData, Integer> pictureDatas = new LinkedHashMap<>();

    boolean date1904;

    public ExcelWorkbook<T> addSheet(T sheet) {
        sheets.add(sheet);
        return this;
    }

    public ExcelWorkbook<T> addSheets(Iterable<T> sheets) {
        for (T sheet : sheets) {
            addSheet(sheet);
        }
        return this;
    }

    public List<T> getSheets() {
        return Collections.unmodifiableList(sheets);
    }

    public Collection<ExcelFont> getFonts() {
        return Collections.unmodifiableCollection(fonts.keySet());
    }

    public Collection<ExcelStyle> getStyles() {
        return Collections.unmodifiableCollection(styles);
    }

    public Collection<ExcelPictureData> getPictureDatas() {
        return Collections.unmodifiableCollection(pictureDatas.keySet());
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

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

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public XSSFWorkbook toWorkbook() {
        return toWorkbook(new XSSFWorkbook());
    }

    public SXSSFWorkbook toWorkbookWithBigGrid() {
        return toWorkbook(new SXSSFWorkbook());
    }

    public HSSFWorkbook toWorkbook2003() {
        return toWorkbook(new HSSFWorkbook());
    }

    public <W extends Workbook> W toWorkbook(W workbook) {
        // picture data
        for (ExcelPictureData pictureData : getPictureDatas()) {
            workbook.addPicture(pictureData.getData(), pictureData.getPictureType());
        }

        // sheet
        int sheetIndex = 0;
        for (T excelSheet : sheets) {
            Sheet sheet = workbook.createSheet(excelSheet.getName());
            fillSheet(excelSheet, sheet, sheetIndex++);
        }
        return workbook;
    }

    private void fillSheet(IExcelSheet excelSheet, Sheet sheet, int sheetIndex) {
        Workbook workbook = sheet.getWorkbook();
        for (IExcelWriteCallback writeCallback : excelSheet.getWriteCallbacks()) {
            writeCallback.onCreateSheet(workbook, sheet, sheetIndex);
        }

        for (IExcelCell excelCell : excelSheet) {
            Pair<Row, Cell> rowCell = ExcelInternalUtil.makeRowCell(excelCell, sheet);
            Row row = rowCell.first();
            Cell cell = rowCell.second();

            for (IExcelWriteCallback writeCallback : excelSheet.getWriteCallbacks()) {
                writeCallback.onCreateCell(workbook, sheet, sheetIndex, row, cell);
            }

            // content
            IExcelContent cellContent = excelCell.getContent();
            cellContent.fill(cell);

            // font and style
            CellStyle style = makeCellStyle(excelCell, workbook);
            if (style != null) cell.setCellStyle(style);

            // hyperlink
            ExcelHyperLink cellLink = excelCell.getHyperLink();
            if (cellLink != null) {
                cellLink.fill(cell, workbook, excelCell);
            }

            // comment
            ExcelComment excelComment = excelCell.getComment();
            if (excelComment != null) {
                excelComment.fill(cell, sheet);
            }

            for (IExcelWriteCallback writeCallback : excelSheet.getWriteCallbacks()) {
                writeCallback.onFinishCell(workbook, sheet, sheetIndex,
                        row, cell, cellContent, style);
            }
        }

        for (IExcelWriteCallback writeCallback : excelSheet.getWriteCallbacks()) {
            writeCallback.onFinishSheet(workbook, sheet, sheetIndex);
        }

        for (ExcelPicture excelPicture : excelSheet.getPictures()) {
            int pictureDataIndex = makePictureData(excelPicture.getPictureData(), workbook);
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor clientAnchor = excelPicture.getAnchor().createClientAnchor(drawing);

            Picture picture = drawing.createPicture(clientAnchor, pictureDataIndex);
            if (excelPicture.getScaleX() > 0 || excelPicture.getScaleY() > 0) {
                picture.resize(excelPicture.getScaleX(), excelPicture.getScaleY());
            }
        }
    }

    private CellStyle makeCellStyle(IExcelCell excelCell, Workbook workbook) {
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
                style.setFont(font);
            }
        }
        DataFormat dataFormat = workbook.createDataFormat();
        excelStyle.fill(style, dataFormat);
        return style;
    }

    private int makePictureData(ExcelPictureData pictureData, Workbook workbook) {
        Integer index = pictureDatas.get(pictureData);
        if (index != null) return index;

        index = workbook.addPicture(pictureData.getData(), pictureData.getPictureType());
        pictureDatas.put(pictureData, index);
        return index;
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public void writeTo(String newFile) throws IOException {
        writeTo(new File(newFile));
    }

    public void writeTo(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeTo(ostream);
        }
    }

    public void writeTo(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbook()) {
            workbook.write(output);
        }
    }

    public void writeToWithBigGrid(String newFile) throws IOException {
        writeToWithBigGrid(new File(newFile));
    }

    public void writeToWithBigGrid(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeToWithBigGrid(ostream);
        }
    }

    public void writeToWithBigGrid(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbookWithBigGrid()) {
            workbook.write(output);
        }
    }

    public void writeTo2003(String newFile) throws IOException {
        writeTo2003(new File(newFile));
    }

    public void writeTo2003(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeTo2003(ostream);
        }
    }

    public void writeTo2003(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbook2003()) {
            workbook.write(output);
        }
    }

    public byte[] toByteArray() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbook()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }

    public byte[] toByteArrayWithBigGrid() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbookWithBigGrid()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }

    public byte[] toByteArray2003() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbook2003()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }
}
