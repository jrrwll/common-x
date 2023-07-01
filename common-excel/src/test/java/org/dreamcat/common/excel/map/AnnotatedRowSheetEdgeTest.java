package org.dreamcat.common.excel.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.dreamcat.common.asm.MakeClass;
import org.dreamcat.common.asm.MakeField;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.DelegateSheet;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.annotation.XlsSheet;
import org.dreamcat.common.excel.callback.FitWidthWriteCallback;
import org.dreamcat.common.util.ReflectUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Create by tuke on 2021/2/16
 */
@Disabled
class AnnotatedRowSheetEdgeTest extends BaseTest {

    @Test
    void test() {
        List<IExcelSheet> sheets = new ArrayList<>();
        List<Object> list = newPojoList();
        for (Object pojo : list) {
            AnnotatedRowSheet sheet = new AnnotatedRowSheet(pojo);
            sheet.setName(pojo.getClass().getSimpleName());

            DelegateSheet delegateSheet = new DelegateSheet(sheet);
            delegateSheet.setWriteCallback(new FitWidthWriteCallback());
            sheets.add(delegateSheet);
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
        String className = "Pojo_" + keyword;
        MakeClass makeClass = MakeClass.make(className)
                .addAnnotation(XlsSheet.class.getCanonicalName(), "name", className);

        try {
            List<MakeField> fields = new ArrayList<>();
            for (int index : indexes) {
                MakeField field = MakeField.make(FIELD_SOURCES.get(index), makeClass)
                        .toMakeProperty().makeGetter().makeSetter();
                fields.add(field);
            }
            makeClass.addFields(fields);

            Object object = ReflectUtil.newInstance(makeClass.toClass());
            list.add(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final List<String> FIELD_SOURCES = Arrays.asList(
            "java.lang.String scalar = \"S\";",
            "java.util.List scalarArray = org.dreamcat.jwrap.excel.map.XlsMetaTest.newStrings();",
            "org.dreamcat.jwrap.excel.map.XlsMetaTest.Item vector = org.dreamcat.jwrap.excel.map.XlsMetaTest.newItem();",
            "java.util.List vectorArray = org.dreamcat.jwrap.excel.map.XlsMetaTest.newItems();",
            "java.util.Map dynamic = org.dreamcat.jwrap.excel.map.XlsMetaTest.newMap();",
            "java.util.List dynamicArray = org.dreamcat.jwrap.excel.map.XlsMetaTest.newMaps();"
    );

    private static final List<String> FIELDS = Arrays.asList(
            "S",
            "SA",
            "V",
            "VA",
            "D",
            "DA"
    );
}
