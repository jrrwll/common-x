package org.dreamcat.common.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dreamcat.common.Pair;

/**
 * @author Jerry Will
 * @version 2023-07-04
 */
class ExcelInternalUtil {

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
}
