package org.dreamcat.common.csv;

import java.lang.reflect.Field;
import java.util.List;
import org.dreamcat.common.excel.annotation.CsvFormat;
import org.dreamcat.common.util.ReflectUtil;

/**
 * Create by tuke on 2020/8/10
 */
public final class CsvBuilder {

    private CsvBuilder() {
    }

    public static CsvMeta parse(Class<?> clazz) {
        CsvMeta meta = new CsvMeta();

        CsvFormat csvFormat = clazz.getDeclaredAnnotation(CsvFormat.class);
        if (csvFormat != null) {
            meta.deserializer = ReflectUtil.newInstance(csvFormat.typeDeserializer());
            meta.serializer = ReflectUtil.newInstance(csvFormat.typeSerializer());
        }

        List<Field> fields = ReflectUtil.retrieveFields(clazz);
        int index = 0;
        for (Field field : fields) {
            csvFormat = field.getDeclaredAnnotation(CsvFormat.class);
            int i = index;
            int ind;
            if (csvFormat != null && (ind = csvFormat.index()) != -1) {
                i = ind;
            }
            CsvMeta.Cell cell = meta.computeCell(i);
            cell.index = i;
            cell.field = field;

            if (csvFormat == null) {
                continue;
            }

            if (csvFormat.ignored()) {
                meta.getCells().put(index, CsvMeta.IGNORED_CELL);
                continue;
            }

            cell.serializer = ReflectUtil.newInstance(csvFormat.serializer());
            cell.deserializer = ReflectUtil.newInstance(csvFormat.deserializer());
            index++;
        }

        return meta;
    }
}
