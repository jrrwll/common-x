package org.dreamcat.common.excel.callback;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.excel.IExcelWriteCallback;
import org.dreamcat.common.excel.content.IExcelContent;

/**
 * Create by tuke on 2020/7/26
 */
public class FitWidthWriteCallback implements IExcelWriteCallback {

    @Override
    public void onFinishCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell,
            IExcelContent content, CellStyle style) {
        double px = 1;
        if (style != null) {
            int fontIndex = style.getFontIndex();
            int fontNum = workbook.getNumberOfFonts();
            if (fontIndex >= 0 && fontIndex < fontNum) {
                Font font = workbook.getFontAt(fontIndex);
                px = font.getFontHeightInPoints() / 12.;
            }
        }

        int charNum = content.toString().length();
        int width = (int) ((charNum + 1) * 256 * px);

        // maximum column width
        if (width > 255 * 256) width = 255 * 256;

        int columnIndex = cell.getColumnIndex();
        int columnWith = sheet.getColumnWidth(columnIndex);
        if (width > columnWith) {
            sheet.setColumnWidth(columnIndex, width);
        }
    }
}
