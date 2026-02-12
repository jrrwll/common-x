package org.dreamcat.common.csv;

import java.util.function.Function;

/**
 * @author Jerry Will
 * @version 2026-02-04
 */
public @interface CsvType {

    boolean onlyAnnotated() default false;

    // the first non-empty line is a header row
    boolean headerless() default false;

    char delimiter() default ',';

    // skip empty (or blank) lines during reading
    boolean skipEmptyLines() default true;

    Class<? extends Function<Object, String[]>> serializer() default NoneSerializer.class;

    Class<? extends Function<String[], Object>> deserializer() default NoneDeserializer.class;

    class NoneSerializer implements Function<Object, String[]> {

        @Override
        public String[] apply(Object o) {
            throw new UnsupportedOperationException();
        }
    }

    class NoneDeserializer implements Function<String[], Object> {

        @Override
        public Object apply(String[] o) {
            throw new UnsupportedOperationException();
        }
    }
}
