package org.dreamcat.common.excel;

import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.dreamcat.common.json.JsonUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Create by tuke on 2020/8/13
 */
class ExcelUtilTest {

    @Test
    void test() throws IOException, InvalidFormatException {
        File file = new File(System.getenv("HOME") + "/Downloads/parse1.xlsx");
        if (!file.exists()) {
            System.out.println("file not exists");
            return;
        }

        List<List<String>> book = ExcelUtil.parseAsString(file, 0);
        // book.forEach(it -> it.forEach(System.out::println));
        book.forEach(System.out::println);
    }

    @SneakyThrows
    @Test
    void testPicture() {
        File file = new File(System.getenv("HOME") + "/Downloads/parse2.xlsx");
        if (!file.exists()) {
            System.out.println("file not exists");
            return;
        }

        List<List<String>> book1 = ExcelUtil.parseAsString(file, 0);
        System.out.println(JsonUtil.toJsonWithPretty(book1));

        List<List<String>> book2 = ExcelUtil.parseAsString(file, 1);
        System.out.println(JsonUtil.toJsonWithPretty(book2));

        ExcelWorkbook<ExcelSheet> workbook = ExcelWorkbook.from(file);
        workbook.getPictureDatas().stream().map(Object::hashCode)
                .forEach(System.out::println);

        for (ExcelSheet sheet : workbook.getSheets()) {
            System.out.println("\nsheet " + sheet.getName());
            sheet.getPictures().forEach(System.out::println);
        }
    }
}
