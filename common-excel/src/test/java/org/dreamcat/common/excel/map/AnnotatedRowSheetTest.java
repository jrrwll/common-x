package org.dreamcat.common.excel.map;

import static org.dreamcat.common.util.RandomUtil.randi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.annotation.XlsCell;
import org.dreamcat.common.excel.annotation.XlsSheet;
import org.junit.jupiter.api.Test;

/**
 * Create by tuke on 2020/7/26
 */
class AnnotatedRowSheetTest extends BaseTest {

    @Test
    void test() {
        Pojo pojo = newPojo();
        AnnotatedRowSheet sheet = new AnnotatedRowSheet(pojo);
        printSheetVerbose(sheet);

        AnnotatedSheet listSheet = new AnnotatedSheet("Sheet");
        listSheet.addSheet(sheet);
        listSheet.addSheet(new AnnotatedRowSheet(newPojo()));
        listSheet.addSheet(new AnnotatedRowSheet(newPojo()));

        writeXlsx("test", listSheet);
    }

    public static Pojo newPojo() {
        Pojo pojo = new Pojo();
        pojo.setS("S");
        pojo.setSA(Stream.of(1, 2, 3, 4)
                .map(it -> "SA" + it)
                .collect(Collectors.toList()));
        pojo.setV(new Item("V1", "V2"));
        pojo.setVA(Stream.of(1, 2, 3)
                .map(it -> new Item("VA1-" + it, "VA2-" + it))
                .collect(Collectors.toList()));
        // D
        int width = randi(2, 5);
        Map<String, String> d = new HashMap<>();
        for (int i = 1; i <= width; i++) {
            d.put("$" + i, "D" + i);
        }
        pojo.setD(d);
        // DA
        int width2 = randi(2, 5);
        pojo.setDA(Stream.of(1, 2, 3).map(it -> {
            Map<String, String> m = new TreeMap<>();
            for (int i = 1; i <= width2; i++) {
                m.put("$" + i, "DA" + it + "-" + i);
            }
            return m;
        }).collect(Collectors.toList()));
        return pojo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XlsSheet(name = "Pojo")
    public static class Pojo {

        List<String> SA;
        String S;
        @XlsCell(expanded = true)
        Item V;
        List<Map<String, String>> DA;
        Map<String, String> D;
        @XlsCell(expanded = true)
        List<Item> VA;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XlsSheet(name = "item")
    public static class Item {

        String r1;
        String r2;

        @Override
        public String toString() {
            return r2;
        }
    }

}
