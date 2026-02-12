package org.dreamcat.common.csv;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Create by tuke on 2020/8/10
 */
@Getter
@Setter
@SuppressWarnings("rawtypes")
class CsvTypeValue {

    boolean headerless;
    char delimiter = ',';
    boolean skipEmptyLines = true;
    Function serializer;
    Function deserializer;

    List<ColumnValue> columns;

    @Getter
    @Setter
    static class ColumnValue {

        Field field;
        Function serializer;
        Function deserializer;

        String header;
    }

    static CsvTypeValue parse(Class<?> clazz) {
        CsvTypeValue typeValue = new CsvTypeValue();

        CsvType csvType = ReflectUtil.retrieveAnnotation(clazz, CsvType.class);
        boolean onlyAnnotated = false;
        if (csvType != null) {
            onlyAnnotated = csvType.onlyAnnotated();

            typeValue.headerless = csvType.headerless();
            typeValue.delimiter = csvType.delimiter();
            typeValue.skipEmptyLines = csvType.skipEmptyLines();
            if (csvType.serializer() != CsvType.NoneSerializer.class) {
                typeValue.serializer = ReflectUtil.newInstance(csvType.serializer());
            }
            if (csvType.deserializer() != CsvType.NoneDeserializer.class) {
                typeValue.deserializer = ReflectUtil.newInstance(csvType.deserializer());
            }
        }

        List<ColumnValue> unsortedColumns = new ArrayList<>();
        Map<Integer, ColumnValue> annotatedSortedColumns = new TreeMap<>();

        List<Field> fields = ReflectUtil.retrieveBeanFields(clazz);
        for (Field field : fields) {
            CsvColumn csvColumn = field.getAnnotation(CsvColumn.class);
            if (csvColumn == null) {
                if (onlyAnnotated) continue;
            } else if (csvColumn.ignore()) {
                continue;
            }

            ColumnValue columnValue = new ColumnValue();
            columnValue.field = field;
            columnValue.header = field.getName();

            if (csvColumn == null) {
                unsortedColumns.add(columnValue);
                continue;
            }
            String header = csvColumn.header();
            if (!header.isEmpty()) {
                columnValue.header = header;
            }

            if (csvColumn.serializer() != CsvColumn.NoneSerializer.class) {
                columnValue.serializer = ReflectUtil.newInstance(csvColumn.serializer());
            }
            if (csvColumn.deserializer() != CsvColumn.NoneDeserializer.class) {
                columnValue.deserializer = ReflectUtil.newInstance(csvColumn.deserializer());
            }

            int fieldIndex = csvColumn.fieldIndex();
            if (fieldIndex == -1) {
                unsortedColumns.add(columnValue);
            } else {
                annotatedSortedColumns.put(fieldIndex, columnValue);
            }
        }

        typeValue.columns = new ArrayList<>(unsortedColumns.size() + annotatedSortedColumns.size());
        typeValue.columns.addAll(unsortedColumns);
        typeValue.columns.addAll(annotatedSortedColumns.values());
        return typeValue;
    }
}
