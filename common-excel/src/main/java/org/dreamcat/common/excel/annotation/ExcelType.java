package org.dreamcat.common.excel.annotation;

import lombok.Getter;
import org.dreamcat.common.asm.BeanMapUtil;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.annotation.ExcelColumn.SubValue;
import org.dreamcat.common.excel.style.ExcelFont;
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

    // default style
    ExcelColumnFont font() default @ExcelColumnFont();

    ExcelColumnStyle style() default @ExcelColumnStyle();

    @Getter
    class Value {

        private String name;
        private final List<ExcelColumn.Value> columns = new ArrayList<>();
        // any column expanded
        private boolean subheader;

        private transient List<IExcelCell> headerCells;

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
                defaultStyle = ExcelStyle.from(excelType.style());
                defaultStyle.setFont(ExcelFont.from(excelType.font()));

                onlyAnnotated = excelType.onlyAnnotated();
            }

            List<ExcelColumn.Value> annotatedUnsortColumns = new ArrayList<>();
            Map<Integer, ExcelColumn.Value> annotatedSortedColumns = new TreeMap<>();

            List<Field> fields = ReflectUtil.retrieveBeanFields(clazz);
            for (Field field : fields) {
                ExcelColumn.Value columnValue = new ExcelColumn.Value();
                String fieldName = field.getName();
                columnValue.setFieldName(fieldName);
                columnValue.setHeader(fieldName);

                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if (excelColumn == null) {
                    if (onlyAnnotated) continue;

                    typeValue.columns.add(columnValue);
                    continue;
                }

                columnValue.setHeader(excelColumn.header());
                ExcelStyle columnStyle = ExcelStyle.from(excelColumn.style());
                columnStyle.setFont(ExcelFont.from(excelColumn.font()));
                if (defaultStyle != null) {
                    columnStyle.merge(defaultStyle);
                }
                columnValue.setStyle(columnStyle);
                if (excelColumn.expanded()) {
                    if (excelColumn.subheader()) {
                        typeValue.subheader = true;
                    }
                    List<SubValue> subValues = SubValue.parse(field.getType());
                    columnValue.setSubValues(subValues);
                }

                int fieldIndex = excelColumn.fieldIndex();
                if (fieldIndex == -1) {
                    annotatedUnsortColumns.add(columnValue);
                } else {
                    annotatedSortedColumns.put(fieldIndex, columnValue);
                }
            }
            typeValue.columns.addAll(annotatedUnsortColumns);
            typeValue.columns.addAll(annotatedSortedColumns.values());

            return typeValue;
        }

        public synchronized List<IExcelCell> getHeaderCells() {
            if (headerCells == null) {
                initHeaderCells();
            }
            return headerCells;
        }

        private void initHeaderCells() {
            headerCells = new ArrayList<>();

            int offset = 0;
            int rowSpan = subheader ? 2 : 1;
            for (ExcelColumn.Value column : columns) {
                String header = column.getHeader();
                ExcelCell excelCell = new ExcelCell(header, 0, offset);
                excelCell.setRowSpan(rowSpan);
                headerCells.add(excelCell);

                ExcelStyle style = column.getStyle();
                if (style != null) {
                    excelCell.setStyle(style);
                }

                List<SubValue> subValues = column.getSubValues();
                if (subValues == null) {
                    excelCell.setColumnSpan(1);
                    offset++;
                    continue;
                }

                excelCell.setRowSpan(1);
                if (subheader) {
                    excelCell.setColumnSpan(subValues.size());
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
                }
            }
        }

        public List<IExcelCell> getColumnCells(Object row) {
            List<IExcelCell> cells = new ArrayList<>();
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
