package org.dreamcat.common.excel.annotation;

import lombok.Getter;
import org.dreamcat.common.Triple;
import org.dreamcat.common.asm.BeanMapUtil;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.annotation.ExcelColumn.SubValue;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ReflectUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Create by tuke on 2020/7/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelType {

    // sheet name
    String name() default "";

    boolean onlyAnnotated() default false;

    @Getter
    class Value {

        private String name;
        private List<ExcelColumn.Value> columns;
        // any column expanded
        private boolean subheader;

        // cells, rowSpan, columnSpan
        private transient Triple<List<ExcelCell>, Integer, Integer> headerCells;

        public static Value parse(Class<?> clazz) {
            Value typeValue = new Value();
            ExcelType excelType = ReflectUtil.retrieveAnnotation(clazz, ExcelType.class);
            boolean onlyAnnotated = false;
            ExcelStyle defaultStyle = null;
            if (excelType != null) {
                String name = excelType.name();
                if (!name.isEmpty()) {
                    typeValue.name = name;
                }
                onlyAnnotated = excelType.onlyAnnotated();

                defaultStyle = ExcelColumn.SubValue.parseStyle(clazz, null);
            }

            List<ExcelColumn.Value> unsortedColumns = new ArrayList<>();
            Map<Integer, ExcelColumn.Value> annotatedSortedColumns = new TreeMap<>();

            List<Field> fields = ReflectUtil.retrieveBeanFields(clazz);
            for (Field field : fields) {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if (excelColumn == null) {
                    if (onlyAnnotated) continue;
                }

                ExcelColumn.Value columnValue = new ExcelColumn.Value();
                columnValue.style = ExcelColumn.SubValue.parseStyle(field, defaultStyle);

                String fieldName = field.getName();
                columnValue.fieldName = fieldName;
                columnValue.header = fieldName;

                if (excelColumn != null) {
                    String header = excelColumn.header();
                    if (!header.isEmpty()) {
                        columnValue.header = header;
                    }
                } else {
                    unsortedColumns.add(columnValue);
                    continue;
                }


                if (excelColumn.expanded()) {
                    if (excelColumn.subheader()) {
                        typeValue.subheader = true;
                    }
                    columnValue.subValues = SubValue.parse(field.getType());
                }

                int fieldIndex = excelColumn.fieldIndex();
                if (fieldIndex == -1) {
                    unsortedColumns.add(columnValue);
                } else {
                    annotatedSortedColumns.put(fieldIndex, columnValue);
                }
            }

            typeValue.columns = new ArrayList<>();
            typeValue.columns.addAll(unsortedColumns);
            typeValue.columns.addAll(annotatedSortedColumns.values());
            return typeValue;
        }

        public synchronized Triple<List<ExcelCell>, Integer, Integer> getHeaderCells() {
            if (headerCells == null) {
                initHeaderCells();
            }
            return headerCells;
        }

        private void initHeaderCells() {
            List<ExcelCell> cells = new ArrayList<>();

            int offset = 0;
            int rowSpan = subheader ? 2 : 1;
            for (ExcelColumn.Value column : columns) {
                String header = column.getHeader();
                ExcelCell excelCell = new ExcelCell(header, 0, offset);
                excelCell.setRowSpan(rowSpan);

                ExcelStyle style = column.getStyle();
                if (style != null) {
                    excelCell.setStyle(style);
                }

                List<SubValue> subValues = column.getSubValues();
                if (subValues == null) {
                    excelCell.setColumnSpan(1);
                    offset++;
                    cells.add(excelCell);
                    continue;
                }

                excelCell.setRowSpan(1);
                if (subheader) {
                    excelCell.setColumnSpan(subValues.size());
                    cells.add(excelCell);
                }
                for (SubValue subValue : subValues) {
                    ExcelCell subExcelCell = new ExcelCell(subValue.getHeader(), 0, offset++);
                    ExcelStyle subStyle = subValue.getStyle();
                    if (subStyle != null) {
                        subExcelCell.setStyle(subStyle);
                    }

                    if (subheader) {
                        subExcelCell.setRowIndex(1);
                    }
                    cells.add(subExcelCell);
                }
            }
            headerCells = Triple.of(cells, rowSpan, offset);
        }

        public List<ExcelCell> getColumnCells(Object row) {
            List<ExcelCell> cells = new ArrayList<>();
            Map<String, Object> rowMap = BeanMapUtil.toShallowMap(row);

            int offset = 0;
            for (ExcelColumn.Value column : columns) {
                Object v = rowMap.get(column.getFieldName());
                List<SubValue> subValues = column.getSubValues();
                int span = subValues == null ? 1 : subValues.size();
                if (v == null) {
                    offset += span;
                    continue;
                }

                if (subValues == null) {
                    cells.add(new ExcelCell(v, 0, offset).setStyle(column.getStyle()));
                    offset += span;
                    continue;
                }

                Map<String, Object> subMap = BeanMapUtil.toShallowMap(v);
                for (SubValue subValue : subValues) {
                    Object subVal = subMap.get(subValue.getFieldName());
                    if (subVal != null) {
                        cells.add(new ExcelCell(subVal, 0, offset).setStyle(column.getStyle()));
                    }
                    offset++;
                }
            }
            return cells;
        }
    }
}
