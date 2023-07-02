package org.dreamcat.common.excel.tow;

import static org.dreamcat.common.excel.ExcelBuilder.sheet;
import static org.dreamcat.common.util.RandomUtil.choose36;
import static org.dreamcat.common.util.RandomUtil.randi;

import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dreamcat.common.excel.ExcelBuilder;
import org.dreamcat.common.excel.ExcelSheet;
import org.dreamcat.common.excel.ExcelUtil;
import org.dreamcat.common.excel.ExcelWorkbook;
import org.dreamcat.common.excel.callback.AutoWidthWriteCallback;
import org.dreamcat.common.excel.parse.SVParser;
import org.dreamcat.common.excel.parse.SVRow;
import org.dreamcat.common.util.BeanUtil;
import org.junit.jupiter.api.Test;

/**
 * Create by tuke on 2020/8/19
 */
class SVParserTest {

    private final File book1 = new File(System.getenv("HOME") + "/Downloads/parse1.xlsx");

    @Test
    void test() throws Exception {
        if (!book1.exists()) {
            prepareExcel();
        }
        SVParser<Dancer, Maid> parser = new SVParser<>(Dancer.class, Maid.class, 1);
        parser.setHeaderIndex(2);
        parser.setVectorFirstHeaderName("maid-bm");
        List<SVRow<Dancer, Maid>> rows = parser.readSheetAsValue(book1, 0);
        rows.forEach(row -> System.out.println(BeanUtil.toPrettyString(row)));
        System.out.println("total " + rows.size());
    }

    @Test
    void prepareExcel() throws IOException {
        ExcelBuilder.SheetTerm sheetTerm = newSheetTerm();

        int offset = 3;
        for (int i = 0; i++ < randi(6, 12); ) {
            sheetTerm.cell(choose36(3), offset, 1);
            sheetTerm.cell(randi(10), offset, 2);
            sheetTerm.cell(choose36(randi(1, 4)), offset, 3);
            sheetTerm.cell(choose36(randi(1, 4)), offset, 4);
            sheetTerm.cell(choose36(randi(1, 4)), offset, 5);

            sheetTerm.cell(choose36(randi(1, 2)), offset, 6);
            sheetTerm.cell(randi(2, 10), offset, 7);

            sheetTerm.cell(choose36(randi(3, 6)), offset, 8);
            sheetTerm.cell(choose36(randi(3, 6)), offset, 9);
            sheetTerm.cell(choose36(randi(3, 6)), offset, 10);
            sheetTerm.cell(choose36(randi(3, 6)), offset, 11);

            for (int j = -1; j++ < randi(1, 6); ) {
                offset++;

                sheetTerm.cell(choose36(randi(1, 2)), offset, 6);
                sheetTerm.cell(randi(2, 10), offset, 7);

                sheetTerm.cell(choose36(randi(3, 6)), offset, 8);
                sheetTerm.cell(choose36(randi(3, 6)), offset, 9);
                sheetTerm.cell(choose36(randi(3, 6)), offset, 10);
                sheetTerm.cell(choose36(randi(3, 6)), offset, 11);
            }
            offset++;
        }

        ExcelSheet sheet = sheetTerm.finish();
        sheet.setWriteCallback(new AutoWidthWriteCallback());
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();
        book.addSheet(sheet).writeTo(book1);
    }

    @Test
    void testMapper() throws Exception {
        List<List<Object>> sheet = ExcelUtil.parse(book1, 0);
        sheet.forEach(System.out::println);

        List<List<String>> sheet2 = ExcelUtil.parseAsString(book1, 0);
        sheet2.forEach(System.out::println);

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class Dancer extends BasicDancer {

        private String name;
        private int height;
    }

    @Data
    public static class Maid {

        private String name;
        private int version;
    }

    @Data
    public static class BasicDancer {

        private String message;
    }

    static ExcelBuilder.SheetTerm newSheetTerm() {
        return sheet("Sheet Cell")
                .cell("errorMessage", 2, 0)

                .cell("dancer", 2, 1)
                .cell("dancer(rita) height", 2, 2)

                .cell("wind-1", 2, 3)
                .cell("$$$", 2, 4)
                .cell("wind-xxx", 2, 5)

                .cell("maid-bm", 2, 6)
                .cell("maid-version", 2, 7)

                .cell("water-start", 2, 8)
                .cell("dancer", 2, 9)
                .cell("maid", 2, 10)
                .cell("water-end", 2, 11);
    }

}
