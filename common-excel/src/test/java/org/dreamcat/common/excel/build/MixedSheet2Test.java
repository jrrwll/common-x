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
import org.dreamcat.common.excel.style.ExcelStyle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by tuke on 2020/7/22
 */
class MixedSheet2Test extends BaseTest {

    @Test
    void testSmall() {
        MixedSheet2 sheet = new MixedSheet2("Sheet One");

        List<ExcelCell> cells1 = new ArrayList<>();
        cells1.add(new ExcelCell("A1:C2", 0, 0, 2, 3)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.GREY_50_PERCENT)));
        cells1.add(new ExcelCell("D1:D3", 0, 3, 3, 1)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.RED)));
        cells1.add(new ExcelCell("B3:C3", 2, 1, 1, 2)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.GREEN)));
        cells1.add(new ExcelCell("A3", 2, 0)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.LIGHT_BLUE)));
        sheet.addRow(cells1);

        sheet.addRow(new Pojo(1, rand(), null, choose72(6)));
        sheet.addRow(new Pojo(2, rand() * (1 << 16), null, choose72(2)));

        List<ExcelCell> cells2 = new ArrayList<>();
        cells2.add(new ExcelCell("A6:B6", 0, 0, 1, 2)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.PINK)));
        cells2.add(new ExcelCell("A7", 1, 0, 1, 1)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.LIGHT_YELLOW)));
        cells2.add(new ExcelCell("B7:C7", 1, 1, 1, 2)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.ORANGE)));
        cells2.add(new ExcelCell("C6", 0, 2, 1, 1)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.YELLOW1)));
        cells2.add(new ExcelCell("D6:D7", 0, 3, 2, 1)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.TURQUOISE)));
        sheet.addRow(cells2);

        sheet.addRow(new Pojo(3, rand(), null, choose72(6)));
        sheet.addRow(new Pojo(4, rand() * (1 << 16), null, choose72(2)));

        printSheetVerbose(sheet);
        writeXlsx("testSmall", sheet);
    }

    @Test
    void test() {
        MixedSheet2 sheet = new MixedSheet2("Sheet One");
        // list1
        List<ExcelCell> cells1 = new ArrayList<>();
        cells1.add(new ExcelCell("A1:C2", 0, 0, 2, 3)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.GREY_50_PERCENT)));
        cells1.add(new ExcelCell("D1:D3", 0, 3, 3, 1)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.RED)));
        cells1.add(new ExcelCell("B3:C3", 2, 1, 1, 2)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.GREEN)));
        cells1.add(new ExcelCell("A3", 2, 0)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.LIGHT_BLUE)));
        sheet.addRow(cells1);

        // list2
        ArrayList<Pojo> pojoList;
        pojoList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            pojoList.add(new Pojo(i, rand(), null, choose26(6)));
            System.out.println(pojoList.get(pojoList.size() - 1));
        }
        sheet.addAll(pojoList);

        // list3
        pojoList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            pojoList.add(new Pojo(i, rand() * (1 << 16), null, choose26(2)));
            System.out.println(pojoList.get(pojoList.size() - 1));
        }
        sheet.addAll(pojoList);

        // // list4
        List<ExcelCell> cells2 = new ArrayList<>();
        cells2.add(new ExcelCell("A6:B6", 0, 0, 1, 2)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.PINK)));
        cells2.add(new ExcelCell("A7", 1, 0, 1, 1)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.BLUE_GREY)));
        cells2.add(new ExcelCell("B7:C7", 1, 1, 1, 2)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.GREY_25_PERCENT)));
        cells2.add(new ExcelCell("C6", 0, 2, 1, 1)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.DARK_YELLOW)));
        cells2.add(new ExcelCell("D6:D7", 0, 3, 2, 1)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.DARK_BLUE)));
        sheet.addRow(cells2);

        // list5
        pojoList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            pojoList.add(new Pojo(i, rand() * 8, (long) randi(1 << 16), choose72(10)));
            System.out.println(pojoList.get(pojoList.size() - 1));
        }
        sheet.addAll(pojoList);

        writeXlsx("test", sheet);
    }

    @Test
    void testHuge() {
        //Thread.sleep(30_000);

        MixedSheet2 sheet = new MixedSheet2("Sheet One");
        // list1
        List<ExcelCell> cells1 = new ArrayList<>();
        cells1.add(new ExcelCell("A1:C2", 0, 0, 2, 3));
        cells1.add(new ExcelCell("D1:D3", 0, 3, 3, 1));
        cells1.add(new ExcelCell("B3:C3", 2, 1, 1, 2));
        cells1.add(new ExcelCell("A3", 2, 0));
        sheet.addRow(cells1);

        // list2
        ArrayList<Pojo> pojoList;
        pojoList = new ArrayList<>();
        for (int i = 0; i < 20_0000; i++) {
            pojoList.add(new Pojo(i, rand(), null, choose26(6)));
        }
        sheet.addAll(pojoList);

        writeXlsxWithBigGrid("testHuge", sheet);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Pojo {

        int a;
        double b;
        Long c;
        String s;
    }

}
