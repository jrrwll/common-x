package org.dreamcat.common.excel.callback;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.MutableInt;
import org.dreamcat.common.excel.IExcelWriteCallback;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.util.ObjectUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Create by tuke on 2020/7/26
 */
@Accessors(chain = true)
public class FitWidthWriteCallback implements IExcelWriteCallback {

    @Setter
    private int estimateLines = 100;

    private final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    @Override
    public void onFinishCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell,
            IExcelContent content, CellStyle style) {
        if (row.getRowNum() >= estimateLines) return;

        double px = 1;
        MutableInt charNum = new MutableInt();
        if (style != null) {
            int fontIndex = style.getFontIndex();
            int fontNum = workbook.getNumberOfFonts();
            if (fontIndex >= 0 && fontIndex < fontNum) {
                Font font = workbook.getFontAt(fontIndex);
                px = font.getFontHeightInPoints() / 12.;
            }

            String dataFormat = style.getDataFormatString();
            if (ObjectUtil.isNotEmpty(dataFormat)) {
                charNum.set(dataFormat.length());
            }
        }
        if (charNum.get() == 0) {
            content.toString().chars().forEach(c -> {
                if (c >= 0x4E00 && c <= 0x9FFF) {
                    charNum.addAndGet(2);
                } else {
                    charNum.getAndIncr();
                }
            });
        }

        int width = (int) ((charNum.get() + 1) * 256 * px * 1.6); // 1.6 ~ 2.0

        // maximum column width
        if (width > 255 * 256) width = 255 * 256;

        int columnIndex = cell.getColumnIndex();
        int columnWith = columnWidthMap.getOrDefault(columnIndex, 0);
        if (width > columnWith) {
            columnWidthMap.put(columnIndex, width);
        }
    }

    @Override
    public void onFinishSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        for (Entry<Integer, Integer> entry : columnWidthMap.entrySet()) {
            sheet.setColumnWidth(entry.getKey(), entry.getValue());
        }
    }
}
