package org.dreamcat.common.excel.build;

import lombok.SneakyThrows;
import org.dreamcat.common.excel.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;


/**
 * Create by tuke on 2020/7/22
 */
public class ExcelBuilderTest extends BaseTest {

    @Test
    @SneakyThrows
    void test() {
        ExcelBuilder.build()
                .addSheet(sheet -> sheet
                        .header(Arrays.asList("id", "name", "age", "birthday")))
                // .addSheet()
                .writeTo(outputFile("test", "xlsx"));
    }
}
