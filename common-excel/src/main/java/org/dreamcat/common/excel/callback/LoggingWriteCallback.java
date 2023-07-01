package org.dreamcat.common.excel.callback;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.excel.IExcelWriteCallback;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/27
 */
@Slf4j
public class LoggingWriteCallback implements IExcelWriteCallback {

    @Override
    public void onCreateSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        if (log.isDebugEnabled()) {
            log.debug("IExcelWriteCallback#onCreateSheet:\t{}", sheet.getSheetName());
        }
    }

    @Override
    public void onFinishSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        if (log.isDebugEnabled()) {
            log.debug("IExcelWriteCallback#onFinishSheet:\t{}", sheet.getSheetName());
        }
    }

    @Override
    public void onCreateCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell) {
        if (log.isDebugEnabled()) {
            log.debug("IExcelWriteCallback#onCreateCell:\t{}\t{}",
                    sheet.getSheetName(), IExcelContent.from(cell));
        }
    }

    @Override
    public void onFinishCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell,
            IExcelContent content, CellStyle style) {
        if (log.isDebugEnabled()) {
            ExcelStyle excelStyle = null;
            ExcelFont excelFont = null;
            if (style != null) {
                Font font = ExcelFont.getFont(style.getFontIndex(), workbook);
                excelStyle = ExcelStyle.from(style);
                if (font != null) excelFont = ExcelFont.from(font);
            }
            log.debug("IExcelWriteCallback#onFinishCell:\t{}\t{}\t{}\t{}",
                    sheet.getSheetName(), IExcelContent.from(cell),
                    excelFont, excelStyle);
        }
    }
}
