package org.dreamcat.common.excel.content;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @author Jerry Will
 * @version 2023-06-30
 */
public enum ExcelBlankContent implements IExcelContent {
    INSTANCE;

    @Override
    public void fill(Cell cell) {
        cell.setBlank();
    }

    @Override
    public String toString() {
        return "";
    }
}
