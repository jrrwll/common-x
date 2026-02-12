package org.dreamcat.common.excel.build;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.ExcelSheet;
import org.dreamcat.common.excel.annotation.ExcelColumn;
import org.dreamcat.common.excel.annotation.ExcelFontStyle;
import org.dreamcat.common.excel.annotation.ExcelHeaderStyle;
import org.dreamcat.common.excel.annotation.ExcelType;
import org.dreamcat.common.util.ArrayUtil;
import org.dreamcat.common.util.RandomUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Create by tuke on 2020/7/26
 */
public class BeanSheetTest extends BaseTest {

    @Test
    void testSmall() {
        BeanSheet<Pojo> sheet = new BeanSheet<>(Pojo.class);
        sheet.setBody(ArrayUtil.mapRangeToList(1, 4, Pojo::create));
        writeXlsx("testSmall", sheet);
    }

    @Test
    void testSmallSimple() {
        BeanSheet<SimplePojo> sheet = new BeanSheet<>(SimplePojo.class);
        sheet.setBody(ArrayUtil.mapRangeToList(1, 4, SimplePojo::create));
        writeXlsx("testSmallSimple", sheet);
    }

    @Test
    void testHuge() {
        // header
        ExcelSheet headerSheet = MixedSheetTest.createHeaderSheet();

        // body
        BeanSheet<SimplePojo> bodySheet = new BeanSheet<>(SimplePojo.class);
        ArrayList<SimplePojo> pojoList;
        pojoList = new ArrayList<>();
        for (int i = 0; i < 20_0000; i++) {
            pojoList.add(SimplePojo.create(i));
        }
        bodySheet.setBody(pojoList);

        CompositeSheet sheet = new CompositeSheet();
        sheet.setName("Sheet One");
        sheet.setSheets(Arrays.asList(headerSheet, bodySheet));
        writeXlsxWithBigGrid("testHuge", sheet);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimplePojo {

        int a;
        double b;
        Long c;
        String s;

        static SimplePojo create(int seq) {
            return new SimplePojo(seq, rand(), null, choose26(6));
        }
    }

    @Data
    @ExcelType(name = "Pojo")
    public static class Pojo {

        @ExcelHeaderStyle(horizontalAlignment = HorizontalAlignment.CENTER,
                fillColorIndex = IndexedColors.RED)
        @ExcelFontStyle(name = "宋体", height = 24)
        String name;

        @ExcelHeaderStyle(fillColorIndex = IndexedColors.LEMON_CHIFFON,
                fillBaseColorIndex = IndexedColors.GREEN,
                fillPattern = FillPatternType.ALT_BARS)
        Double num;

        @ExcelHeaderStyle(verticalAlignment = VerticalAlignment.CENTER)
        @ExcelFontStyle(name = "黑体", height = 21, italic = true, indexedColor = IndexedColors.AQUA)
        @ExcelColumn(expanded = true, header = "expanded item")
        Item item;

        @ExcelHeaderStyle(
                fillColorIndex = IndexedColors.ROSE,
                borderBottom = BorderStyle.DASH_DOT_DOT,
                borderLeft = BorderStyle.THICK)
        @ExcelFontStyle(name = "微软雅黑", height = 16, bold = true, italic = true)
        Date date;

        @ExcelHeaderStyle(
                fillColorIndex = IndexedColors.SKY_BLUE,
                borderBottom = BorderStyle.DASHED,
                borderLeft = BorderStyle.THIN)
        @ExcelFontStyle(height = 15, italic = true)
        LocalDateTime localDateTime;

        int seq;

        public static Pojo create(int seq) {
            Pojo pojo = new Pojo();
            pojo.name = choose26(randi(4, 7)); // 4-6
            pojo.num = rand();
            pojo.item = Item.create(seq);
            pojo.date = new Date(System.currentTimeMillis() - randi(90 * 24 * 3600_000L));
            pojo.localDateTime = LocalDateTime.now().plusDays(randi(180));
            pojo.seq = seq;
            return pojo;
        }
    }

    @Data
    @ExcelType(onlyAnnotated = true)
    public static class Item {

        @ExcelColumn(fieldIndex = 2)
        Long r1;
        @ExcelColumn(fieldIndex = 1)
        String r2;
        @ExcelColumn(fieldIndex = 3)
        LocalDate r3;

        String r0 = RandomUtil.uuid();

        public static Item create(int seq) {
            Item item = new Item();
            item.r1 = randi(1000L);
            item.r2 = String.format("seq-%07d", seq);
            item.r3 = LocalDate.now().minusDays(randi(360));
            return item;
        }

        @Override
        public String toString() {
            return r2;
        }
    }
}
