package org.dreamcat.common.excel.style;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.common.excel.BaseTest;
import org.junit.jupiter.api.Test;

/**
 * Create by tuke on 2021/2/14
 */
class StyleTest extends BaseTest {

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
