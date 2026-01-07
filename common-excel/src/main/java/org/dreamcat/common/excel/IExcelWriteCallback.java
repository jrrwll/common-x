package org.dreamcat.common.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.excel.content.IExcelContent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Create by tuke on 2020/7/26
 */
public interface IExcelWriteCallback {

    default void onCreateSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        // nop
    }

    default void onFinishSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        // nop
    }

    default void onCreateCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell) {
        // nop
    }

    default void onFinishCell(
            Workbook workbook, Sheet sheet, int sheetIndex,
            Row row, Cell cell, IExcelContent content, CellStyle style) {
        // nop
    }

    static IExcelSheet bind(IExcelSheet sheet, IExcelWriteCallback... writeCallbacks) {
        return bind(sheet, Arrays.asList(writeCallbacks));
    }

    static IExcelSheet bind(IExcelSheet sheet, List<IExcelWriteCallback> writeCallbacks) {

        return new IExcelSheet() {

            @Override
            public String getName() {
                return sheet.getName();
            }

            @Override
            public Iterator<IExcelCell> iterator() {
                return sheet.iterator();
            }

            @Override
            public List<IExcelWriteCallback> getWriteCallbacks() {
                return writeCallbacks;
            }
        };
    }
}
