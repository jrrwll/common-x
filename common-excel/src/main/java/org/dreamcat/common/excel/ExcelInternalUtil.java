package org.dreamcat.common.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dreamcat.common.Pair;
import org.dreamcat.common.excel.style.ExcelFont;

/**
 * @author Jerry Will
 * @version 2023-07-04
 */
public class ExcelInternalUtil {

    static Pair<Row, Cell> makeRowCell(IExcelCell excelCell, Sheet sheet) {
        int ri = excelCell.getRowIndex();
        int ci = excelCell.getColumnIndex();

        if (excelCell.hasMergedRegion()) {
            int rs = excelCell.getRowSpan();
            int cs = excelCell.getColumnSpan();
            sheet.addMergedRegion(new CellRangeAddress(
                    ri, ri + rs - 1, ci, ci + cs - 1));
        }

        Row row = sheet.getRow(ri);
        if (row == null) {
            row = sheet.createRow(ri);
        }
        Cell cell = row.createCell(ci);
        return Pair.of(row, cell);
    }

    public static ExcelFont getOrCreateFont(ExcelWorkbook<?> excelWorkbook, Font font) {
        int fontId = font.hashCode();
        ExcelFont excelFont = excelWorkbook.fonts.get(fontId);
        if (excelFont == null) {
            excelFont = ExcelFont.from(font);
            excelWorkbook.fonts.put(fontId, excelFont);
            excelWorkbook.reservedFonts.put(excelFont, font);
        }
        return excelFont;
    }

    public static Font getOrCreateFont(Workbook workbook, ExcelWorkbook<?> excelWorkbook, ExcelFont excelFont) {
        Font font = excelWorkbook.reservedFonts.get(excelFont);
        if (font != null) {
            return font;
        }

        font = workbook.createFont();
        excelFont.fill(font);

        excelWorkbook.fonts.put(font.hashCode(), excelFont);
        excelWorkbook.reservedFonts.put(excelFont, font);
        return font;
    }
}
