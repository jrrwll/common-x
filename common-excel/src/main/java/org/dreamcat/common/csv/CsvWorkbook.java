package org.dreamcat.common.csv;

import org.dreamcat.common.CloseableIterator;
import org.dreamcat.common.csv.CsvTypeValue.ColumnValue;
import org.dreamcat.common.io.CsvUtil;
import org.dreamcat.common.util.BeanUtil;
import org.dreamcat.common.util.ListUtil;
import org.dreamcat.common.util.MapUtil;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.common.util.StringUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2020/7/28
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CsvWorkbook<T> implements Iterable<Iterable<String>> {

    private static final String csvEscapeChars = "\\\",";
    private static final String tsvEscapeChars = "\\\"\t";

    private final List<T> values;

    public CsvWorkbook(List<T> values) {
        this.values = values;
    }

    public CsvWorkbook() {
        this(new ArrayList<>());
    }

    public static CsvWorkbook<List<String>> from(String filename) throws IOException {
        return from(filename, null);
    }

    public static <T> CsvWorkbook<T> from(String filename, Class<T> clazz) throws IOException {
        return from(new File(filename), clazz);
    }

    public static CsvWorkbook<List<String>> from(File file) throws IOException {
        return from(file, null);
    }

    public static <T> CsvWorkbook<T> from(File file, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return from(reader, clazz);
        }
    }

    public static CsvWorkbook<List<String>> from(Reader reader) throws IOException {
        return from(reader, null);
    }

    public static <T> CsvWorkbook from(Reader reader, Class<T> clazz) throws IOException {
        if (clazz == null) {
            // nop
            List<List<String>> values = CsvUtil.read(reader);
            return new CsvWorkbook<>(values);
        }

        List<T> values = new ArrayList<>();
        CsvWorkbook<T> workbook = new CsvWorkbook<>(values);
        CsvTypeValue typeValue = CsvTypeValue.parse(clazz);
        List<ColumnValue> columns = typeValue.getColumns();

        Map<Integer, ColumnValue> headerMappedColumns = null;
        try (CloseableIterator<List<String>> iter = CsvUtil.readAsIter(reader, typeValue.delimiter, '"')) {
            while (iter.hasNext()) {
                List<String> row = iter.next();
                if (row.isEmpty() && typeValue.skipEmptyLines) continue;

                if (headerMappedColumns == null && !typeValue.headerless) {
                    if (!iter.hasNext()) {
                        return workbook;
                    }

                    headerMappedColumns = getHeaderMappedColumns(row, columns);
                    continue;
                }

                T object = ReflectUtil.newInstance(clazz);
                values.add(object);

                for (int i = 0, size = row.size(); i < size; i++) {
                    ColumnValue column;
                    if (headerMappedColumns != null) {
                        column = headerMappedColumns.get(i);
                    } else {
                        column = ListUtil.getOrNull(columns, i);
                    }
                    if (column == null) continue;

                    String word = row.get(i);

                    Field field = column.field;
                    Object fieldValue = null;
                    if (column.deserializer != null) {
                        fieldValue = column.deserializer.apply(word);
                    } else {
                        fieldValue = ReflectUtil.cast(word, field.getType());
                    }

                    if (fieldValue != null) {
                        ReflectUtil.setFieldValue(object, field, fieldValue);
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new RuntimeException(e);
        }
        return workbook;
    }

    private static Map<Integer, ColumnValue> getHeaderMappedColumns(
            List<String> row, List<ColumnValue> columns) {
        Map<Integer, ColumnValue> map = new HashMap<>();
        Map<String, ColumnValue> columnMap = MapUtil.toMap(columns, ColumnValue::getHeader);
        for (int i = 0, n = row.size(); i < n; i++) {
            String header = row.get(i);
            ColumnValue column = columnMap.get(header);
            if (column == null) continue;

            map.put(i, column);
        }
        return map;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void writeToCsv(String newFile) throws IOException {
        writeToCsv(new File(newFile));
    }

    public void writeToCsv(File newFile) throws IOException {
        try (FileWriter writer = new FileWriter(newFile)) {
            writeToCsv(writer);
        }
    }

    public void writeToTsv(String newFile) throws IOException {
        writeToTsv(new File(newFile));
    }

    public void writeToTsv(File newFile) throws IOException {
        try (FileWriter writer = new FileWriter(newFile)) {
            writeToTsv(writer);
        }
    }

    public void writeToCsv(Writer writer) throws IOException {
        writeTo(writer, ",", csvEscapeChars);
    }

    public void writeToTsv(Writer writer) throws IOException {
        writeTo(writer, "\t", tsvEscapeChars);
    }

    // 1. Each record is located on a separate line, delimited by a line break (CRLF)
    // 2. The last record in the file may or may not have an ending line break
    public void writeTo(Writer writer, String separator, String escapeChars)
            throws IOException {
        for (Iterable<String> row : this) {
            StringBuilder rowString = new StringBuilder();
            Iterator<String> iterator = row.iterator();
            while (iterator.hasNext()) {
                String value = iterator.next();
                String literal = StringUtil.escape(value, escapeChars);
                rowString.append(literal);
                if (iterator.hasNext()) {
                    rowString.append(separator);
                }
            }

            writer.write(rowString.toString());
            writer.write("\r\n");
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @Override
    public Iterator<Iterable<String>> iterator() {
        return this.new Iter();
    }

    class Iter implements Iterator<Iterable<String>> {

        final Iterator<T> iterator;

        Iter() {
            iterator = values.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Iterable<String> next() {
            T row = iterator.next();
            if (row instanceof List) {
                return ((List<String>) row);
            }
            return BeanUtil.toList(row).stream().map(StringUtil::toString)
                    .collect(Collectors.toList());
        }
    }
}
