package org.dreamcat.common.excel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;

/**
 * Create by tuke on 2020/8/13
 */
class ExcelUtilTest {

    private static final File file = new File(System.getenv("HOME") + "/Downloads/parse1.xlsx");

    @Test
    void test() throws IOException, InvalidFormatException {
        List<List<String>> book = ExcelUtil.parseAsString(file, 0);
        // book.forEach(it -> it.forEach(System.out::println));
        book.forEach(System.out::println);
    }
}
