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
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.annotation.ExcelColumn;
import org.dreamcat.common.excel.annotation.ExcelColumnStyle;
import org.dreamcat.common.excel.annotation.ExcelType;
import org.dreamcat.common.excel.build.BeanSheet;
import org.dreamcat.common.excel.build.ExcelBuilder;
import org.dreamcat.common.excel.callback.FitWidthWriteCallback;
import org.dreamcat.common.excel.callback.FixedWidthWriteCallback;
import org.dreamcat.common.excel.style.ExcelStyle;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Create by tuke on 2021/2/16
 */
@SuppressWarnings({"rawtypes"})
public class SimpleListDemo {

    public static void main(String[] args) throws IOException {
        // pojo list
        List<Pojo> pojoList = new ArrayList<>();
        for (int i = 0; i < randi(2, 17); i++) {
            pojoList.add(new Pojo());
        }
        // 9X9
        List<List<Object>> multiplyList = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            List<Object> line = new ArrayList<>();
            multiplyList.add(line);
            for (int j = 1; j <= i; j++) {
                line.add(String.format("%s x %s = %s", i, j, i * j));
            }
        }

        // build a sheet by cells
        IExcelSheet headerSheet = headerSheet();
        // build a sheet by pojo list
        IExcelSheet tripleListSheet = tripleListSheet();

        String excelFile = System.getenv("HOME") + "/Downloads/SimpleListDemo.xlsx";
        System.out.println("writing to " + excelFile);
        ExcelBuilder.build()
                .addSheet(Pojo.class, sheet -> {
                    sheet.name("Pojo")
                            .body(pojoList)
                            .addWriteCallback(new FitWidthWriteCallback());
                })
                .addSheet(sheet -> {
                    sheet.name("9X9")
                            .body(multiplyList)
                            .addWriteCallback(new FixedWidthWriteCallback(140));
                })
                .addCompositeSheet("Triple", headerSheet, tripleListSheet)
                .writeTo(new File(excelFile));
    }


    @ExcelType(name = "Sheet via @ExcelType")
    @Data
    public static class Pojo {

        @ExcelColumn(header = "Cell int", headerStyle = @ExcelColumnStyle(fillColorIndex = IndexedColors.RED1))
        int a = randi(128);
        @ExcelColumn(header = "Cell Double", headerStyle = @ExcelColumnStyle(fillColorIndex = IndexedColors.BRIGHT_GREEN1))
        Double b = rand();
        @ExcelColumn(header = "Cell String", headerStyle = @ExcelColumnStyle(fillColorIndex = IndexedColors.BLUE1))
        String c = choose36(randi(3, 7));
        @ExcelColumn(header = "Cell boolean", headerStyle = @ExcelColumnStyle(fillColorIndex = IndexedColors.YELLOW1))
        boolean d = rand() > 0.5;
        @ExcelColumn(header = "Cell Date", headerStyle = @ExcelColumnStyle(
                fillColorIndex = IndexedColors.PINK1, dataFormat = "yyyy-MM-dd hh:mm:ss"))
        Date e = new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L));
        @ExcelColumn(header = "Cell LocalDate", headerStyle = @ExcelColumnStyle(
                fillColorIndex = IndexedColors.TURQUOISE1, dataFormat = "yyyy-MM-dd"))
        LocalDate f = ofDate(
                new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L))).toLocalDate();
        @ExcelColumn(header = "Cell LocalDateTime", headerStyle = @ExcelColumnStyle(
                fillColorIndex = IndexedColors.RED, dataFormat = "yyyy-MM-dd hh:mm:ss"))
        LocalDateTime g = ofDate(new Date(System.currentTimeMillis() + randi(-3 * 24 * 3600L, 3 * 24 * 3600L)));
        @ExcelColumn(header = "null", headerStyle = @ExcelColumnStyle(fillColorIndex = IndexedColors.ROYAL_BLUE))
        String _null; // null
    }

    private static IExcelSheet headerSheet() {
        ExcelSheet sheet = new ExcelSheet();
        sheet.addCell(new ExcelCell("cell_a", 0, 0)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.RED1)));
        sheet.addCell(new ExcelCell("cell_b", 0, 1));
        sheet.addCell(new ExcelCell("cell_c", 0, 2)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.BRIGHT_GREEN1)));
        return sheet;
    }

    private static IExcelSheet tripleListSheet() {
        List<Triple> tripleList = new ArrayList<>();
        for (int i = 0; i < randi(1, 17); i++) {
            tripleList.add(Triple.of(uuid32(), rand(10), addDay(new Date(), -i - 1)));
        }
        BeanSheet<Triple> sheet = new BeanSheet<>(Triple.class);
        sheet.setHeaderless(true);// no header
        sheet.setBody(tripleList);
        return sheet;
    }
}
