package org.dreamcat.common.excel.style;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;

/**
 * Create by tuke on 2021/2/14
 */
class StyleTest extends BaseTest {

    @SneakyThrows
    @Test
    void color() {
        String filename = basePath + "/color.xlsx";
        try (FileOutputStream w = new FileOutputStream(filename);
                XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFSheet sheet = workbook.createSheet("color");
            XSSFRow row = sheet.createRow(0);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(new Date());
            cell.setCellStyle(cellStyle);
            workbook.write(w);
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(filename)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFCell cell = sheet.getRow(0).getCell(0);
            System.out.println(JsonUtil.toJson(ExcelStyle.from(cell.getCellStyle())));
        }
    }

    @Test
    void rich() throws IOException {
        String file = basePath + "/test.xlsx";
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFCell cell = sheet.getRow(0).getCell(0);
            XSSFComment comment = cell.getCellComment();
            XSSFRichTextString string = comment.getString();
            string.getString();
        }
    }

    @Test
    void printIndexedColors() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("IndexedColors");
        int ri = 0;
        for (IndexedColors indexedColor : IndexedColors.values()) {
            Row row = sheet.createRow(ri++);
            Cell cell1 = row.createCell(0);
            Cell cell2 = row.createCell(1);

            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(indexedColor.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell1.setCellStyle(style);

            cell2.setCellValue(indexedColor.name());
        }
        sheet.setColumnWidth(0, 64 * 255);
        sheet.setColumnWidth(1, 64 * 255);
        workbook.write(new FileOutputStream(basePath +
                "/book_StyleTest_printIndexedColors.xlsx"));
    }
}
