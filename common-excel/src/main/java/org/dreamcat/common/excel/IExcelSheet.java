package org.dreamcat.common.excel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.Pair;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelComment;
import org.dreamcat.common.excel.style.ExcelHyperLink;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelSheet extends Iterable<IExcelCell> {

    String getName();

    default List<IExcelWriteCallback> getWriteCallbacks() {
        return new ArrayList<>();
    }

    default void addWriteCallback(IExcelWriteCallback writeCallback) {
        getWriteCallbacks().add(writeCallback);
    }

    default void fill(Sheet sheet, int sheetIndex, IExcelWorkbook<?> excelWorkbook) {
        Workbook workbook = sheet.getWorkbook();
        for (IExcelWriteCallback writeCallback : getWriteCallbacks()) {
            writeCallback.onCreateSheet(workbook, sheet, sheetIndex);
        }
        for (IExcelCell excelCell : this) {
            Pair<Row, Cell> rowCell = ExcelUtil.makeRowCell(excelCell, sheet);
            Row row = rowCell.first();
            Cell cell = rowCell.second();

            for (IExcelWriteCallback writeCallback : getWriteCallbacks()) {
                writeCallback.onCreateCell(workbook, sheet, sheetIndex, row, cell);
            }

            // content
            IExcelContent cellContent = excelCell.getContent();
            cellContent.fill(cell);

            // font and style
            CellStyle style = excelWorkbook.makeCellStyle(excelCell, workbook);
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

            for (IExcelWriteCallback writeCallback : getWriteCallbacks()) {
                writeCallback.onFinishCell(workbook, sheet, sheetIndex,
                        row, cell, cellContent, style);
            }
        }
        for (IExcelWriteCallback writeCallback : getWriteCallbacks()) {
            writeCallback.onFinishSheet(workbook, sheet, sheetIndex);
        }
    }
}
