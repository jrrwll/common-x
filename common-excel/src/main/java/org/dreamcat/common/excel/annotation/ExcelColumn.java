package org.dreamcat.common.excel.annotation;

import lombok.Getter;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ReflectUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Create by tuke on 2021/2/22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    int fieldIndex() default -1;

    boolean ignore() default false;

    String header() default "";

    boolean expanded() default false;

    // enable subheader when it is expanded
    boolean subheader() default false;

    @Getter
    class Value extends SubValue {

        List<SubValue> subValues;
    }

    @Getter
    class SubValue {

        String fieldName;
        String header;
        ExcelStyle style;

        static List<SubValue> parse(Class<?> fieldType) {
            boolean onlyAnnotated = false;
            ExcelStyle defaultStyle = null;
            ExcelType excelType = ReflectUtil.retrieveAnnotation(fieldType, ExcelType.class);
            if (excelType != null) {
                onlyAnnotated = excelType.onlyAnnotated();
                defaultStyle = parseStyle(fieldType, null);
            }

            List<SubValue> subValues = new ArrayList<>();
            List<SubValue> unsortColumns = new ArrayList<>();
            Map<Integer, SubValue> annotatedSortedColumns = new TreeMap<>();

            List<Field> fields = ReflectUtil.retrieveBeanFields(fieldType);
            for (Field field : fields) {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if (excelColumn == null) {
                    if (onlyAnnotated) continue;
                }

                SubValue subValue = new SubValue();
                subValue.style = parseStyle(field, defaultStyle);

                String fieldName = field.getName();
                subValue.fieldName = fieldName;
                subValue.header = fieldName;
                if (excelColumn != null) {
                    String header = excelColumn.header();
                    if (!header.isEmpty()) {
                        subValue.header = header;
                    }
                } else {
                    unsortColumns.add(subValue);
                    continue;
                }

                int fieldIndex = excelColumn.fieldIndex();
                if (fieldIndex == -1) {
                    unsortColumns.add(subValue);
                } else {
                    annotatedSortedColumns.put(fieldIndex, subValue);
                }
            }

            subValues.addAll(unsortColumns);
            subValues.addAll(annotatedSortedColumns.values());
            return subValues;
        }

        static ExcelStyle parseStyle(AnnotatedElement element, ExcelStyle defaultStyle) {
            ExcelColumnStyle columnStyle = element.getAnnotation(ExcelColumnStyle.class);
            ExcelColumnFont columnFont = element.getAnnotation(ExcelColumnFont.class);

            if (columnStyle == null && columnFont == null) {
                return defaultStyle != null ? defaultStyle.copy() : null;
            }

            ExcelStyle style;
            if (columnStyle != null) {
                style = ExcelStyle.from(columnStyle);
            } else {
                style = new ExcelStyle();
            }
            if (columnFont != null) {
                ExcelFont font = ExcelFont.from(columnFont);
                style.setFont(font);
            }
            if (defaultStyle != null) {
                style.merge(defaultStyle);
            }
            return style;
        }
    }
}
