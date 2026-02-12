package org.dreamcat.common.csv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Create by tuke on 2020/8/10
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvColumn {

    boolean ignore() default false;

    int fieldIndex() default -1;

    String header() default "";

    Class<? extends Function<Object, String>> serializer() default NoneSerializer.class;

    Class<? extends Function<String, Object>> deserializer() default NoneDeserializer.class;

    class NoneSerializer implements Function<Object, String> {

        @Override
        public String apply(Object o) {
            throw new UnsupportedOperationException();
        }
    }

    class NoneDeserializer implements Function<String, Object> {

        @Override
        public Object apply(String o) {
            throw new UnsupportedOperationException();
        }
    }
}
