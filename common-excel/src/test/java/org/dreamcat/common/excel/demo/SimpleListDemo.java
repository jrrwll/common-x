package org.dreamcat.common.excel.demo;

import static org.dreamcat.common.excel.ExcelBuilder.sheet;
import static org.dreamcat.common.excel.ExcelBuilder.style;
import static org.dreamcat.common.util.DateUtil.addDay;
import static org.dreamcat.common.util.DateUtil.ofDate;
import static org.dreamcat.common.util.RandomUtil.choose36;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;
import static org.dreamcat.common.util.RandomUtil.uuid32;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import lombok.Data;
import org.dreamcat.common.Triple;
import org.dreamcat.common.excel.ExcelWorkbook;
import org.dreamcat.common.excel.annotation.XlsHeader;
import org.dreamcat.common.excel.annotation.XlsSheet;
import org.dreamcat.common.excel.annotation.XlsStyle;
import org.dreamcat.common.excel.callback.FitWidthWriteCallback;
import org.dreamcat.common.excel.callback.HeaderCellStyleWriteCallback;
import org.dreamcat.common.excel.map.SimpleSheet;

/**
 * Create by tuke on 2021/2/16
 */
public class SimpleListDemo {

    @XlsSheet(name = "Sheet via @XlsSheet")
    @Data
    static class Pojo {

        @XlsHeader(header = "Cell int", style = @XlsStyle(fgColor = 2))
        int a = randi(128);
        @XlsHeader(header = "Cell Double", style = @XlsStyle(fgColor = 3))
        Double b = rand();
        @XlsHeader(header = "Cell String", style = @XlsStyle(fgColor = 4))
        String c = choose36(randi(3, 7));
        @XlsHeader(header = "Cell boolean", style = @XlsStyle(fgColor = 5))
        boolean d = rand() > 0.5;
        @XlsHeader(header = "Cell Date", style = @XlsStyle(
                fgColor = 6, dataFormat = "yyyy-MM-dd hh:mm:ss"))
        Date e = new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L));
        @XlsHeader(header = "Cell LocalDate", style = @XlsStyle(
                fgColor = 7, dataFormat = "yyyy-MM-dd"))
        LocalDate f = ofDate(
                new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L))).toLocalDate();
        @XlsHeader(header = "Cell LocalDateTime", style = @XlsStyle(
                fgColor = 10, dataFormat = "yyyy-MM-dd hh:mm:ss"))
        LocalDateTime g = ofDate(new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L)));
        @XlsHeader(header = "null", style = @XlsStyle(fgColor = 30))
        String _null; // null
    }

    public static void main(String[] args) throws IOException {
        // build a sheet with a styled header row
        SimpleSheet sheet1 = new SimpleSheet(Pojo.class);
        for (int i = 0; i < randi(2, 17); i++) {
            // add one row to the sheet
            sheet1.addRow(new Pojo());
        }
        // add many rows to the sheet
        sheet1.addAll(Arrays.asList(new Pojo(), new Pojo()));
        sheet1.addWriteCallback(new HeaderCellStyleWriteCallback().overwrite(true));
        sheet1.addWriteCallback(new FitWidthWriteCallback());

        // build the second sheet with a specified header
        SimpleSheet sheet2 = new SimpleSheet(sheet("Sheet Two")
                .richCell("cell_a", 0, 0)
                .style(style().bgColor((short) 2).finish()).finishCell()
                .cell("cell_b", 0, 1)
                .richCell("cell_c", 0, 2)
                .style(style().bgColor((short) 3).finish()).finishCell()
                .finish());
        for (int i = 0; i < randi(1, 17); i++) {
            sheet2.addRow(Triple.of(uuid32(), rand(10), addDay(new Date(), -i - 1)));
        }
        // custom bean_to_list, only output c & a
        sheet2.setSchemeConverter(row -> {
            Triple<?, ?, ?> triple = (Triple<?, ?, ?>) row;
            return Arrays.asList(triple.third(), triple.first());
        });

        // write data to a local excel file
        String excelFile = System.getenv("HOME") + "/Downloads/SimpleListDemo.xlsx";
        new ExcelWorkbook<>().addSheet(sheet1).addSheet(sheet2).writeTo(excelFile);
    }
}
