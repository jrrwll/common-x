package org.dreamcat.common.excel.build;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.choose72;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.ExcelSheet;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by tuke on 2020/7/22
 */
class CompositeSheetTest extends BaseTest {

    @Test
    void testSmall() {
        List<Pojo> body1 = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            body1.add(new Pojo(i, rand(), null, choose26(6)));
        }
        BeanSheet<Pojo> sheet1 = new BeanSheet<>(Pojo.class);
        sheet1.setBody(body1);

        List<Pojo> body2 = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            body2.add(new Pojo(i, rand() * (1 << 16), null, choose26(2)));
        }
        BeanSheet<Pojo> sheet2 = new BeanSheet<>(Pojo.class);
        sheet2.setBody(body2);

        List<Pojo> body3 = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            body3.add(new Pojo(i, rand() * 8, (long) randi(1 << 16), choose72(10)));
        }
        BeanSheet<Pojo> sheet3 = new BeanSheet<>(Pojo.class);
        sheet3.setBody(body3);

        CompositeSheet sheet = new CompositeSheet();
        sheet.setName("Sheet One");
        sheet.setSheets(Arrays.asList(excelSheet1(), sheet1, excelSheet2(), sheet2, excelSheet1(), sheet3));
        writeXlsx("testSmall", sheet);
    }

    @Test
    void testSmallParse() {
        readXlsx("testSmall", sheet -> {
            System.out.println("sheetName: " + sheet.getName());
            printSheetVerbose(sheet);
        });
    }

    @Test
    void testHuge() {
        // list2
        List<Pojo> body = new ArrayList<>();
        for (int i = 0; i < 20_0000; i++) {
            body.add(new Pojo(i, rand(), null, choose26(6)));
        }
        BeanSheet<Pojo> sheet1 = new BeanSheet<>(Pojo.class);
        sheet1.setBody(body);

        CompositeSheet sheet = new CompositeSheet();
        sheet.setName("Sheet One");
        sheet.setSheets(Arrays.asList(excelSheet1(), sheet1, excelSheet2()));
        writeXlsxWithBigGrid("testHuge", sheet);
    }

    @Test
    void test() {
        CompositeSheet sheet1 = new CompositeSheet();
        sheet1.setName("Sheet One");
        List<IExcelSheet> sheets1 = new ArrayList<>();
        sheet1.setSheets(sheets1);
        for (int i = 0; i < 5; i++) {
            TableSheet tableSheet = new TableSheet();
            tableSheet.setDefaultStyle(TableSheetTest.defaultStyle);
            tableSheet.setHeader(TableSheetTest.buildHeader());
            tableSheet.setBody(TableSheetTest.buildBody());
            sheets1.add(tableSheet);
        }

        CompositeSheet sheet2 = new CompositeSheet();
        sheet2.setName("Sheet2");
        List<IExcelSheet> sheets2 = new ArrayList<>();
        sheet2.setSheets(sheets2);
        for (int i = 0; i < 4; i++) {
            TableSheet tableSheet = new TableSheet();
            tableSheet.setDefaultStyle(TableSheetTest.defaultStyle);
            tableSheet.setHeaderStyle(TableSheetTest.defaultHeaderStyle);
            tableSheet.setHeader(TableSheetTest.buildHeader());
            tableSheet.setBody(TableSheetTest.buildBody());
            sheets2.add(tableSheet);
        }
        writeXlsx("test", sheet1, sheet2);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pojo {

        int a;
        double b;
        Long c;
        String s;
    }

    static ExcelSheet excelSheet1() {
        ExcelSheet sheet = new ExcelSheet();
        sheet.addCell(new ExcelCell("A1:C2", 0, 0, 2, 3)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.GREY_50_PERCENT)));
        sheet.addCell(new ExcelCell("D1:D3", 0, 3, 3, 1)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.RED)));
        sheet.addCell(new ExcelCell("B3:C3", 2, 1, 1, 2)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.GREEN)));
        sheet.addCell(new ExcelCell("A3", 2, 0)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.LIGHT_BLUE)));
        return sheet;
    }

    static ExcelSheet excelSheet2() {
        ExcelSheet sheet = new ExcelSheet();
        sheet.addCell(new ExcelCell("A6:B6", 0, 0, 1, 2)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.PINK)));
        sheet.addCell(new ExcelCell("A7", 1, 0, 1, 1)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.LIGHT_YELLOW)));
        sheet.addCell(new ExcelCell("B7:C7", 1, 1, 1, 2)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.ORANGE)));
        sheet.addCell(new ExcelCell("C6", 0, 2, 1, 1)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.YELLOW1)));
        sheet.addCell(new ExcelCell("D6:D7", 0, 3, 2, 1)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.TURQUOISE)));
        return sheet;
    }

}
