package org.dreamcat.common.excel.callback;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.excel.IExcelWriteCallback;

/**
 * @author Jerry Will
 * @version 2026-02-15
 */
@RequiredArgsConstructor
public class WidthHeightWriteCallback implements IExcelWriteCallback {

    private final int columnWidth; // // visible char count
    private final float rowHeight; // px

    @Override
    public void onCreateSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        if (columnWidth != 0) {
            sheet.setDefaultColumnWidth(columnWidth);
        }
        if (rowHeight != 0) {
            sheet.setDefaultRowHeightInPoints(rowHeight);
        }
    }
}
