package org.dreamcat.common.excel.demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.dreamcat.common.excel.demo.SimpleListDemo.Pojo;
import org.dreamcat.common.excel.map.SimpleSheet;

/**
 * Create by tuke on 2021/5/29
 */
public class SimpleListWithUtilDemo {

    public static void main(String[] args) throws IOException {
        String excelFile = System.getenv("HOME") + "/Downloads/SimpleListDemo.xlsx";

        // build a empty sheet called "Sheet One"
        SimpleSheet sheet1 = new SimpleSheet(Pojo.class);
        // add many rows to the sheet
        List<Pojo> pojoList = Arrays.asList(new Pojo(), new Pojo());
        sheet1.addAll(pojoList);
        // add one row to the sheet
        sheet1.addRow(new Pojo());

        // custom bean2list, only output c & a
        sheet1.setSchemeConverter(bean -> {
            Pojo pojo = (Pojo) bean;
            return Arrays.asList(pojo.c, pojo.a);
        });
    }
}
