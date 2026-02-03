package org.dreamcat.common.excel.build;

import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.annotation.XlsCell;
import org.dreamcat.common.excel.annotation.XlsSheet;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create by tuke on 2020/7/26
 */
class SVRowSheetTest extends BaseTest {

    @Test
    void test() {
        SVRowSheet sheet = new SVRowSheet(newPojo());
        printSheetVerbose(sheet);

        SVSheet listSheet = new SVSheet("Sheet");
        listSheet.add(sheet);
        listSheet.add(new SVRowSheet(newPojo()));
        listSheet.add(new SVRowSheet(newPojo()));

        writeXlsx("test", listSheet);
    }

    public static Pojo newPojo() {
        Pojo pojo = new Pojo();
        pojo.setS("S");
        pojo.setSA(Stream.of(1, 2, 3, 4)
                .map(it -> "SA" + it)
                .collect(Collectors.toList()));
        pojo.setV(new Item("V1", "V2", rand(100)));
        pojo.setVA(Stream.of(1, 2, 3)
                .map(it -> new Item("VA1-" + it, "VA2-" + it, rand(100)))
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
        Double r3;

        @Override
        public String toString() {
            return r2;
        }
    }

}
