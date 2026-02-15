package org.dreamcat.common.excel.callback;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.excel.IExcelWriteCallback;

/**
 * @author Jerry Will
 * @version 2026-02-15
 */
public class FixedWidthWriteCallback implements IExcelWriteCallback {

    private final int width;

    public FixedWidthWriteCallback(int width) {
        this.width = width / 12;
    }

    @Override
    public void onCreateSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        sheet.setDefaultColumnWidth(width);
    }
}
