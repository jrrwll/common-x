package org.dreamcat.common.excel.demo;

import static org.dreamcat.common.util.DateUtil.addDay;
import static org.dreamcat.common.util.DateUtil.ofDate;
import static org.dreamcat.common.util.RandomUtil.choose36;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;
import static org.dreamcat.common.util.RandomUtil.uuid32;

import lombok.Data;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.dreamcat.common.Triple;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.ExcelSheet;
import org.dreamcat.common.excel.ExcelWorkbook;
import org.dreamcat.common.excel.annotation.ExcelColumnStyle;
import org.dreamcat.common.excel.annotation.XlsHeader;
import org.dreamcat.common.excel.annotation.XlsSheet;
import org.dreamcat.common.excel.build.MixedSheet2;
import org.dreamcat.common.excel.callback.FitWidthWriteCallback;
import org.dreamcat.common.excel.callback.HeaderCellStyleWriteCallback;
import org.dreamcat.common.excel.style.ExcelStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

/**
 * Create by tuke on 2021/2/16
 */
public class StyledSimpleListDemo {

    @XlsSheet(name = "Sheet via @XlsSheet")
    @Data
    static class Pojo {

        @XlsHeader(header = "Cell int", style = @ExcelColumnStyle(fillColorIndex = IndexedColors.RED1))
        int a = randi(128);
        @XlsHeader(header = "Cell Double", style = @ExcelColumnStyle(fillColorIndex = IndexedColors.BRIGHT_GREEN1))
        Double b = rand();
        @XlsHeader(header = "Cell String", style = @ExcelColumnStyle(fillColorIndex = IndexedColors.BLUE1))
        String c = choose36(randi(3, 7));
        @XlsHeader(header = "Cell boolean", style = @ExcelColumnStyle(fillColorIndex = IndexedColors.YELLOW1))
        boolean d = rand() > 0.5;
        @XlsHeader(header = "Cell Date", style = @ExcelColumnStyle(
                fillColorIndex = IndexedColors.PINK1, dataFormat = "yyyy-MM-dd hh:mm:ss"))
        Date e = new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L));
        @XlsHeader(header = "Cell LocalDate", style = @ExcelColumnStyle(
                fillColorIndex = IndexedColors.TURQUOISE1, dataFormat = "yyyy-MM-dd"))
        LocalDate f = ofDate(
                new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L))).toLocalDate();
        @XlsHeader(header = "Cell LocalDateTime", style = @ExcelColumnStyle(
                fillColorIndex = IndexedColors.RED, dataFormat = "yyyy-MM-dd hh:mm:ss"))
        LocalDateTime g = ofDate(new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L)));
        @XlsHeader(header = "null", style = @ExcelColumnStyle(fillColorIndex = IndexedColors.ROYAL_BLUE))
        String _null; // null
    }

    public static void main(String[] args) throws IOException {
        // build a sheet with a styled header row
        MixedSheet2 sheet1 = new MixedSheet2(Pojo.class);
        for (int i = 0; i < randi(2, 17); i++) {
            // add one row to the sheet
            sheet1.addRow(new Pojo());
        }
        // add many rows to the sheet
        sheet1.addAll(Arrays.asList(new Pojo(), new Pojo()));
        sheet1.addWriteCallback(new HeaderCellStyleWriteCallback().overwrite(true));
        sheet1.addWriteCallback(new FitWidthWriteCallback());

        // build the second sheet with a specified header

        ExcelSheet headerSheet = new ExcelSheet("Sheet Two");
        headerSheet.addCell(new ExcelCell("cell_a", 0, 0)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.RED1)));
        headerSheet.addCell(new ExcelCell("cell_b", 0, 1));
        headerSheet.addCell(new ExcelCell("cell_c", 0, 2)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.BRIGHT_GREEN1)));

        MixedSheet2 sheet2 = new MixedSheet2(headerSheet);
        for (int i = 0; i < randi(1, 17); i++) {
            sheet2.addRow(Triple.of(uuid32(), rand(10), addDay(new Date(), -i - 1)));
        }
        // custom bean_to_list, only output c & a
        sheet2.setSchemeConverter(row -> {
            Triple<?, ?, ?> triple = (Triple<?, ?, ?>) row;
            return Arrays.asList(triple.third(), triple.first());
        });

        // write data to a local Excel file
        String excelFile = System.getenv("HOME") + "/Downloads/StyledSimpleListDemo.xlsx";
        new ExcelWorkbook<>().addSheet(sheet1).addSheet(sheet2).writeTo(excelFile);
    }
}
