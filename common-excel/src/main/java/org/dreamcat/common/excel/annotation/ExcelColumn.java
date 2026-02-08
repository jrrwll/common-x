package org.dreamcat.common.excel.annotation;

import lombok.Getter;
import lombok.Setter;
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
 * Create by tuke on 2021/2/22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    int fieldIndex() default -1;

    String header() default "";

    // column style
    ExcelColumnFont font() default @ExcelColumnFont();

    ExcelColumnStyle style() default @ExcelColumnStyle();

    boolean expanded() default false;

    // enable subheader when it is expanded
    boolean subheader() default false;

    @Getter
    @Setter
    class Value extends SubValue {

        private List<SubValue> subValues;
    }

    @Getter
    @Setter
    class SubValue {

        private String fieldName;
        private String header;
        private ExcelStyle style;

        static List<SubValue> parse(Class<?> fieldType) {
            boolean onlyAnnotated = false;
            ExcelStyle defaultStyle = null;
            ExcelType excelType = ReflectUtil.retrieveAnnotation(fieldType, ExcelType.class);
            if (excelType != null) {
                defaultStyle = ExcelStyle.from(excelType.style());
                defaultStyle.setFont(ExcelFont.from(excelType.font()));

                onlyAnnotated = excelType.onlyAnnotated();
            }

            List<SubValue> subValues = new ArrayList<>();
            List<SubValue> annotatedUnsortColumns = new ArrayList<>();
            Map<Integer, SubValue> annotatedSortedColumns = new TreeMap<>();

            List<Field> fields = ReflectUtil.retrieveBeanFields(fieldType);
            for (Field field : fields) {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                if (excelColumn == null) {
                    if (onlyAnnotated) continue;

                    SubValue subValue = new SubValue();
                    subValue.setFieldName(field.getName());
                    subValue.setHeader(field.getName());
                    subValues.add(subValue);
                    continue;
                }

                SubValue subValue = new SubValue();
                subValue.fieldName = field.getName();
                subValue.header = excelColumn.header();
                ExcelStyle style = ExcelStyle.from(excelColumn.style());
                style.setFont(ExcelFont.from(excelColumn.font()));
                subValue.style = style;

                int fieldIndex = excelColumn.fieldIndex();
                if (fieldIndex == -1) {
                    annotatedUnsortColumns.add(subValue);
                } else {
                    annotatedSortedColumns.put(fieldIndex, subValue);
                }
            }

            subValues.addAll(annotatedUnsortColumns);
            subValues.addAll(annotatedSortedColumns.values());
            return subValues;
        }
    }
}
