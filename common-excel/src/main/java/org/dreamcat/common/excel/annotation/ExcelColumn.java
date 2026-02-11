package org.dreamcat.common.excel.annotation;

import lombok.Getter;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Function;

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
    }

    @Getter
    class SubValue {

        String fieldName;
        Function serializer;
        Function deserializer;

        String header;
        ExcelStyle style;

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
