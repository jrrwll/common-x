package org.dreamcat.common.excel.style;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.ExcelSheet;
import org.dreamcat.common.excel.ExcelWorkbook;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelWriteCallback;
import org.dreamcat.common.excel.mapping.SimpleSheet;
import org.dreamcat.common.util.BeanUtil;
import org.dreamcat.common.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Jerry Will
 * @version 2026-01-21
 */
public class ExcelStyleTest {

    @Test
    @SneakyThrows
    public void testDataFormat() {

        List<List<Object>> sheetData1 = new ArrayList<>();
        sheetData1.add(Arrays.asList("abc", "314", "2020/02/02", 1));
        sheetData1.add(Arrays.asList("xyz", "3.14", "1999/12/31", 2.71828));
        sheetData1.add(Arrays.asList("abc", "314", "2020/02/02", BigDecimal.ONE));
        sheetData1.add(Arrays.asList("xyz", "3.14", "1999/12/31", BigDecimal.valueOf(2.71828)));
        sheetData1.add(Arrays.asList("rst", "1.414", new Date(), BigDecimal.valueOf(3.14158265357989)));
        sheetData1.add(Arrays.asList("uvw", "65537", DateUtil.parse("2015-12-12 00:00"), BigDecimal.valueOf(2.71828)));

        SimpleSheet sheet1 = new SimpleSheet(headerSheet(false));
        sheet1.addAll(sheetData1);
        sheet1.addWriteCallback(new IExcelWriteCallback() {

            @Override
            public void onFinishSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
                sheet.setDefaultRowHeightInPoints((float) 14);
                for (int i = 0; i < 4; i++) {
                    // unit is 1/256
                    sheet.setColumnWidth(i, 10 * 256);
                }
            }
        });

        List<List<Object>> sheetData2 = new ArrayList<>();
        for (int i = 0; i < 30_0000; i++) {
            long t = ThreadLocalRandom.current().nextLong(1000 * 24 * 3600_000L);
            Date date = new Date(System.currentTimeMillis() - t);
            LocalDate localDate = DateUtil.ofDate(date).toLocalDate();
            Object value = i % 10 == 0 ? localDate : "";
            sheetData2.add(Arrays.asList(UUID.randomUUID().toString(), String.valueOf(Math.random()), value,
                    Math.random() * 1000));
            if (i > 10) break;
        }
        SimpleSheet sheet2 = new SimpleSheet(headerSheet(true));
        sheet2.setName("Sheet 2");
        sheet2.addAll(sheetData2);

        File file = new File(System.getenv("HOME"), "Downloads/common_excel_test.xlsx");
        ExcelWorkbook<SimpleSheet> workbook = new ExcelWorkbook<>();
        workbook.addSheet(sheet1).addSheet(sheet2).writeTo(file);
    }

    private static ExcelSheet headerSheet(boolean dataFormat) {
        ExcelStyle style = new ExcelStyle();
        style.setHorizontalAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        ExcelFont font = new ExcelFont();
        font.setName("SimSun"); // 宋体
        font.setHeight((short) 11);
        style.setFont(font);

        ExcelSheet header = new ExcelSheet("Sheet1", headerCells(Arrays.asList(
                "text", "number-like text", "date-like text", "number"), style));
        if (dataFormat) {
            ExcelCell dateCell = (ExcelCell) header.getCells().get(2);
            ExcelStyle dateStyle = BeanUtil.copy(style);
            dateStyle.setDataFormat("yyyy/mm/dd");
            dateCell.setStyle(dateStyle);
        }
        return header;
    }

    public static List<IExcelCell> headerCells(List<String> headers) {
        return headerCells(headers, null);
    }

    public static List<IExcelCell> headerCells(List<String> headers, ExcelStyle style) {
        List<IExcelCell> cells = new ArrayList<>();
        for (int i = 0, n = headers.size(); i < n; i++) {
            String header = headers.get(i);
            ExcelCell cell = new ExcelCell(header, 0, i);
            cell.setStyle(style);
            cells.add(cell);
        }
        return cells;
    }
}
