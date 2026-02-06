package org.dreamcat.common.excel.build;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.rand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.ExcelSheet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Create by tuke on 2020/7/26
 */
public class AnnotatedSheetTest extends BaseTest {
    @Test
    void testHuge() {
        // header
        ExcelSheet headerSheet = MixedSheetTest.createHeaderSheet();

        // body
        AnnotatedSheet<Pojo> bodySheet = new AnnotatedSheet<>();
        ArrayList<Pojo> pojoList;
        pojoList = new ArrayList<>();
        for (int i = 0; i < 20_0000; i++) {
            pojoList.add(Pojo.create(i));
        }
        bodySheet.setData(pojoList);

        MixedSheet sheet = new MixedSheet();
        sheet.setName("Sheet One");
        sheet.setSheets(Arrays.asList(headerSheet, bodySheet));
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

        static Pojo create(int seq) {
            return new Pojo(seq, rand(), null, choose26(6));
        }
    }
}
