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
public class FixedWidthWriteCallback implements IExcelWriteCallback {

    private final int width;

    @Override
    public void onFinishSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        sheet.setDefaultColumnWidth(width);
    }
}
