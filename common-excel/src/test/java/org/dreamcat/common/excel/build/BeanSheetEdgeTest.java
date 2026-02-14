package org.dreamcat.common.excel.build;

import org.dreamcat.common.asm.JavassistMaker;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.annotation.ExcelType;
import org.dreamcat.common.excel.callback.FitWidthWriteCallback;
import org.dreamcat.common.util.ReflectUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2021/2/16
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class BeanSheetEdgeTest extends BaseTest {

    @Test
    void test() {
        List<IExcelSheet> sheets = new ArrayList<>();
        List<Object> list = newPojoList();
        for (Object pojo : list) {
            BeanSheet<?> sheet = new BeanSheet<>(pojo.getClass());
            sheet.setName(pojo.getClass().getSimpleName());
            sheet.setBody((List) Arrays.asList(pojo, pojo, pojo));
            sheet.getWriteCallbacks().add(new FitWidthWriteCallback());
            sheets.add(sheet);
        }
        writeXlsx("test", sheets.toArray(new IExcelSheet[0]));
    }

    private List<Object> newPojoList() {
        List<Object> list = new ArrayList<>();
        addPojo(list, 0, 1, 2, 3, 4, 5);
        addPojo(list, 0, 1, 2, 3, 5, 4);
        addPojo(list, 0, 1, 2, 4, 3, 5);
        addPojo(list, 0, 1, 2, 4, 5, 3);
        addPojo(list, 0, 1, 2, 5, 3, 4);
        addPojo(list, 0, 1, 2, 5, 4, 3);

        addPojo(list, 0, 1, 3, 2, 4, 5);
        addPojo(list, 0, 1, 3, 2, 5, 4);
        addPojo(list, 0, 1, 3, 4, 2, 5);
        addPojo(list, 0, 1, 3, 4, 5, 2);
        addPojo(list, 0, 1, 3, 5, 2, 4);
        addPojo(list, 0, 1, 3, 5, 4, 2);

        addPojo(list, 0, 1, 4, 2, 3, 5);
        addPojo(list, 0, 1, 4, 2, 5, 3);
        addPojo(list, 0, 1, 4, 3, 2, 5);
        addPojo(list, 0, 1, 4, 3, 5, 2);
        addPojo(list, 0, 1, 4, 5, 2, 3);
        addPojo(list, 0, 1, 4, 5, 3, 2);
        return list;
    }

    private void addPojo(List<Object> list, int... indexes) {
        String keyword = Arrays.stream(indexes).mapToObj(FIELDS::get)
                .collect(Collectors.joining("_"));
        String className = "org.dreamcat.common.asm.Pojo_" + keyword;

        JavassistMaker maker = new JavassistMaker(className);
        maker.addAnnotation(ExcelType.class.getCanonicalName())
                .setMethodValue("name", className).end();
        try {
            for (int index : indexes) {
                maker.addProperty(FIELD_SOURCES.get(index)).addGetter().addSetter();
            }

            Object object = ReflectUtil.newInstance(maker.toClass());
            list.add(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final List<String> FIELD_SOURCES = Arrays.asList(
            "java.lang.String name = \"S\";",
            "long ts = java.lang.System.currentTimeMillis();",
            "java.util.Date date = new java.util.Date();",
            "java.time.LocalDate ld = java.time.LocalDate.now();",
            "double num = java.lang.Math.random();",
            "boolean flag = true;"
    );

    private static final List<String> FIELDS = Arrays.asList(
            "name",
            "ts",
            "date",
            "ld",
            "num",
            "flag"
    );
}
