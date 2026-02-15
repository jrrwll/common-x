package org.dreamcat.common.excel.annotation;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.Pair;
import org.dreamcat.common.excel.model.DefaultDataFormat;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ObjectUtil;
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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Create by tuke on 2021/2/22
 */
@SuppressWarnings("rawtypes")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    int fieldIndex() default -1;

    boolean ignore() default false;

    String header() default "";

    ExcelColumnStyle headerStyle() default @ExcelColumnStyle();

    ExcelColumnStyle bodyStyle() default @ExcelColumnStyle();

    ExcelColumnStyle style() default @ExcelColumnStyle();

    ExcelColumnFont headerFont() default @ExcelColumnFont();

    ExcelColumnFont bodyFont() default @ExcelColumnFont();

    ExcelColumnFont font() default @ExcelColumnFont();

    boolean expanded() default false;

    // enable subheader when it is expanded
    boolean subheader() default false;

    Class<? extends Function> serializer() default None.class;

    Class<? extends Function> deserializer() default None.class;

    class None implements Function {

        @Override
        public Object apply(Object o) {
            throw new IllegalStateException("this method may not be invoked");
        }
    }

    @Getter
    class Value extends SubValue {

        List<SubValue> subValues;

        static <T extends SubValue> List<T> parseFields(
                Class<?> clazz, Supplier<T> columnValueConstructor,
                boolean enableExpanded, ExcelType.Value typeValue, DefaultDataFormat defaultDataFormat) {
            ExcelType excelType = ReflectUtil.retrieveAnnotation(clazz, ExcelType.class);
            boolean onlyAnnotated = false;
            ExcelStyle defaultHeaderStyle = null, defaultBodyStyle = null;
            if (excelType != null) {
                String name = excelType.name();
                if (!name.isEmpty()) {
                    if (typeValue != null) {
                        typeValue.setName(name);
                    }
                }
                onlyAnnotated = excelType.onlyAnnotated();

                Pair<ExcelStyle, ExcelStyle> pair = parseStyle(excelType);
                defaultHeaderStyle = pair.first();
                defaultBodyStyle = pair.second();
            }

            List<T> unsortedColumns = new ArrayList<>();
            Map<Integer, T> annotatedSortedColumns = new TreeMap<>();

            List<Field> fields = ReflectUtil.retrieveBeanFields(clazz);
            for (Field field : fields) {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if (excelColumn == null) {
                    if (onlyAnnotated) continue;
                } else if(excelColumn.ignore()) {
                    continue;
                }

                T columnValue = columnValueConstructor.get();

                String fieldName = field.getName();
                columnValue.fieldName = fieldName;
                columnValue.header = fieldName;

                if (excelColumn == null) {
                    columnValue.headerStyle = defaultHeaderStyle != null ? defaultHeaderStyle.copy() : null;
                    columnValue.bodyStyle = defaultBodyStyle != null ? defaultBodyStyle.copy() : null;
                    columnValue.bodyStyle = fillDefaultDataFormat(
                            columnValue.bodyStyle, defaultDataFormat, field.getType());
                    unsortedColumns.add(columnValue);
                    continue;
                }

                String header = excelColumn.header();
                if (!header.isEmpty()) {
                    columnValue.header = header;
                }

                Pair<ExcelStyle, ExcelStyle> pair = parseStyle(excelColumn);
                ExcelStyle headerStyle = pair.first();
                ExcelStyle bodyStyle = pair.second();
                if (defaultHeaderStyle != null) {
                    headerStyle = defaultHeaderStyle.copy().merge(headerStyle);
                }
                if (defaultBodyStyle != null) {
                    bodyStyle = defaultBodyStyle.copy().merge(bodyStyle);
                }
                columnValue.headerStyle = headerStyle;
                columnValue.bodyStyle = bodyStyle;
                columnValue.bodyStyle = fillDefaultDataFormat(
                        columnValue.bodyStyle, defaultDataFormat, field.getType());

                if (excelColumn.serializer() != ExcelColumn.None.class) {
                    columnValue.serializer = ReflectUtil.newInstance(excelColumn.serializer());
                }
                if (excelColumn.deserializer() != ExcelColumn.None.class) {
                    columnValue.deserializer = ReflectUtil.newInstance(excelColumn.deserializer());
                }

                if (enableExpanded && excelColumn.expanded()) {
                    if (excelColumn.subheader()) {
                        if (typeValue != null) {
                            typeValue.setSubheader(true);
                        }
                    }

                    List<SubValue> subValues = parseFields(
                            field.getType(), SubValue::new, false,
                            null, defaultDataFormat);
                    ((ExcelColumn.Value) columnValue).subValues = subValues;
                    // filed style override type style
                    for (SubValue subValue : subValues) {
                        if (subValue.headerStyle != null) {
                            subValue.headerStyle.merge(headerStyle);
                        } else {
                            subValue.headerStyle = headerStyle.copy();
                        }
                        if (subValue.bodyStyle != null) {
                            subValue.bodyStyle.merge(bodyStyle);
                        } else {
                            subValue.bodyStyle = bodyStyle.copy();
                        }
                    }
                }

                int fieldIndex = excelColumn.fieldIndex();
                if (fieldIndex == -1) {
                    unsortedColumns.add(columnValue);
                } else {
                    annotatedSortedColumns.put(fieldIndex, columnValue);
                }
            }
            List<T> columns = new ArrayList<>();
            columns.addAll(unsortedColumns);
            columns.addAll(annotatedSortedColumns.values());
            return columns;
        }

        private static Pair<ExcelStyle, ExcelStyle> parseStyle(
                ExcelType excelType) {
            ExcelStyle headerStyle = ExcelStyle.from(excelType.headerStyle());
            ExcelFont headerFont = ExcelFont.from(excelType.headerFont());
            headerStyle.setFont(headerFont);

            ExcelStyle bodyStyle = ExcelStyle.from(excelType.bodyStyle());
            ExcelFont bodyFont = ExcelFont.from(excelType.bodyFont());
            bodyStyle.setFont(bodyFont);

            ExcelStyle defaultStyle = ExcelStyle.from(excelType.style());
            ExcelFont defaultFont = ExcelFont.from(excelType.font());
            defaultStyle.setFont(defaultFont);

            headerStyle = defaultStyle.copy().merge(headerStyle);
            bodyStyle = defaultStyle.merge(bodyStyle);
            return Pair.of(headerStyle, bodyStyle);
        }

        private static Pair<ExcelStyle, ExcelStyle> parseStyle(
                ExcelColumn excelColumn) {
            ExcelStyle headerStyle = ExcelStyle.from(excelColumn.headerStyle());
            ExcelFont headerFont = ExcelFont.from(excelColumn.headerFont());
            headerStyle.setFont(headerFont);

            ExcelStyle bodyStyle = ExcelStyle.from(excelColumn.bodyStyle());
            ExcelFont bodyFont = ExcelFont.from(excelColumn.bodyFont());
            bodyStyle.setFont(bodyFont);

            ExcelStyle defaultStyle = ExcelStyle.from(excelColumn.style());
            ExcelFont defaultFont = ExcelFont.from(excelColumn.font());
            defaultStyle.setFont(defaultFont);

            headerStyle = defaultStyle.copy().merge(headerStyle);
            bodyStyle = defaultStyle.merge(bodyStyle);
            return Pair.of(headerStyle, bodyStyle);
        }

        private static ExcelStyle fillDefaultDataFormat(
                ExcelStyle style, DefaultDataFormat defaultDataFormat, Class<?> fieldType) {
            if (defaultDataFormat.isDisable()) return style;
            if (style == null || ObjectUtil.isEmpty(style.getDataFormat())) {
                String dataFormat = defaultDataFormat.getDataFormat(fieldType);
                if (dataFormat != null) {
                    if (style == null) {
                        style = new ExcelStyle();
                    }
                    style.setDataFormat(dataFormat);
                }
            }
            return style;
        }
    }

    @Getter
    @Setter
    class SubValue {

        String fieldName;
        Function serializer;
        Function deserializer;

        String header;
        ExcelStyle headerStyle;
        ExcelStyle bodyStyle;
    }
}
