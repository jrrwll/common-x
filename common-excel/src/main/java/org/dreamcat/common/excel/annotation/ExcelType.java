package org.dreamcat.common.excel.annotation;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.Triple;
import org.dreamcat.common.asm.BeanMapUtil;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.annotation.ExcelColumn.SubValue;
import org.dreamcat.common.excel.annotation.ExcelColumn.Value;
import org.dreamcat.common.excel.model.DefaultDataFormat;
import org.dreamcat.common.excel.style.ExcelStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create by tuke on 2020/7/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelType {

    // sheet name
    String name() default "";

    boolean onlyAnnotated() default false;

    ExcelColumnStyle headerStyle() default @ExcelColumnStyle();

    ExcelColumnStyle bodyStyle() default @ExcelColumnStyle();

    ExcelColumnStyle style() default @ExcelColumnStyle();

    ExcelColumnFont headerFont() default @ExcelColumnFont();

    ExcelColumnFont bodyFont() default @ExcelColumnFont();

    ExcelColumnFont font() default @ExcelColumnFont();

    @Getter
    @Setter
    class Value {

        private String name;
        private List<ExcelColumn.Value> columns;
        // any column expanded
        private boolean subheader;

        // cells, rowSpan, columnSpan
        private transient Triple<List<ExcelCell>, Integer, Integer> headerCells;

        public static Value parse(Class<?> clazz, DefaultDataFormat defaultDataFormat) {
            Value typeValue = new Value();
            typeValue.columns = ExcelColumn.Value.parseFields(
                    clazz, ExcelColumn.Value::new,
                    true, typeValue, defaultDataFormat);
            return typeValue;
        }

        public void applyHeaderStyle(ExcelStyle style) {
            for (ExcelColumn.Value column : columns) {
                ExcelStyle excelStyle = column.headerStyle;
                if (excelStyle != null) {
                    excelStyle.merge(style);
                } else {
                    column.headerStyle = style.copy();
                }
            }
        }

        public void applyBodyStyle(ExcelStyle style) {
            for (ExcelColumn.Value column : columns) {
                ExcelStyle excelStyle = column.bodyStyle;
                if (excelStyle != null) {
                    excelStyle.merge(style);
                } else {
                    column.bodyStyle = style.copy();
                }
            }
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

                ExcelStyle style = column.getHeaderStyle();
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
                    ExcelStyle subStyle = subValue.getHeaderStyle();
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
                    cells.add(new ExcelCell(v, 0, offset).setStyle(column.getBodyStyle()));
                    offset += span;
                    continue;
                }

                Map<String, Object> subMap = BeanMapUtil.toShallowMap(v);
                for (SubValue subValue : subValues) {
                    Object subVal = subMap.get(subValue.getFieldName());
                    if (subVal != null) {
                        cells.add(new ExcelCell(subVal, 0, offset).setStyle(column.getHeaderStyle()));
                    }
                    offset++;
                }
            }
            return cells;
        }
    }
}
