package org.dreamcat.common.excel.demo;

import static org.dreamcat.common.util.RandomUtil.randi;

import org.dreamcat.common.Pair;
import org.dreamcat.common.excel.ExcelUtil;
import org.dreamcat.common.excel.demo.StyledSimpleListDemo.Pojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry Will
 * @version 2025-09-14
 */
public class SimpleListDemo {

    public static void main(String[] args) throws IOException {
        List<Pojo> pojoList = new ArrayList<>();
        for (int i = 0; i < randi(2, 17); i++) {
            pojoList.add(new Pojo());
        }

        // 9X9
        List<List<String>> multiply = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            List<String> line = new ArrayList<>();
            multiply.add(line);
            for (int j = 1; j <= i; j++) {
                line.add(String.format("%s x %s = %s", i, j, i * j));
            }
        }

        String excelFile = System.getenv("HOME") + "/Downloads/SimpleListDemo.xlsx";
        ExcelUtil.writeTo(new File(excelFile),
                Pair.of("pojo", pojoList),
                Pair.of("9x9", multiply));

        String excelFileWithStyle = System.getenv("HOME") + "/Downloads/SimpleListDemoWithStyle.xlsx";
        ExcelUtil.writeTo(new File(excelFileWithStyle), Pojo.class, "pojo", pojoList);
    }

}
