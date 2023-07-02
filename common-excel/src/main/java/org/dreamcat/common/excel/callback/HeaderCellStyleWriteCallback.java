package org.dreamcat.common.excel.callback;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.excel.IExcelWriteCallback;
import org.dreamcat.common.excel.content.IExcelContent;

/**
 * @author Jerry Will
 * @version 2023-07-02
 */
@Setter
@Builder
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class HeaderCellStyleWriteCallback implements IExcelWriteCallback {

    @Builder.Default
    private int headerRows = 1;
    private int headerIndex; // 0-based
    private boolean overwrite;// overwrite existing cell styles

    // dataFormat only
    @Builder.Default
    private boolean onlyDataFormat = true;
    private final Map<Integer, CellStyle> dataFormatCellStyles = new HashMap<>();

    @Override
    public void onFinishCell(Workbook workbook, Sheet sheet, int sheetIndex,
            Row row, Cell cell, IExcelContent content, CellStyle style) {
        if (style != null && !overwrite) return;
        int ri = cell.getRowIndex(), ci = cell.getColumnIndex();
        if (ri < headerRows) return;
        // find the styled cell at (headerIndex, ci)
        Row headerRow = sheet.getRow(headerIndex);
        if (headerRow == null) return;
        Cell headerCell = headerRow.getCell(ci);
        if (headerCell == null) return;

        CellStyle headerCellStyle = headerCell.getCellStyle();
        if (headerCellStyle == null) return;
        if (!onlyDataFormat) {
            cell.setCellStyle(headerCellStyle);
            return;
        }

        CellStyle dataFormatCellStyle = dataFormatCellStyles.computeIfAbsent(ci, k -> {
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(headerCellStyle.getDataFormat());
            return cellStyle;
        });
        cell.setCellStyle(dataFormatCellStyle);
    }
}
