package org.dreamcat.common.excel;

import java.io.File;
import java.util.List;
import lombok.SneakyThrows;
import org.dreamcat.common.excel.content.ExcelStringContent;
import org.dreamcat.common.util.ClassPathUtil;
import org.junit.jupiter.api.Test;

/**
 * Create by tuke on 2020/7/21
 */
class ExcelWorkbookTest extends BaseTest {

    @Test
    void export() {
        ExcelSheet sheet = new ExcelSheet("sheet1");
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("A1:B2"), 0, 0, 2, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("C1:C2"), 0, 2, 2, 1));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("D1:D2"), 0, 3, 2, 1));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("A3:B3"), 2, 0, 1, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("C3"), 2, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("D3"), 2, 3));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("A4:B4"), 3, 0, 1, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("C4"), 3, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("D4"), 3, 3));

        writeXlsx("export", sheet);
    }

    @Test
    void fromAnnotationRowSheetTestTest() {
        readXlsx("export", this::printSheetVerbose);
    }

    @SneakyThrows
    @Test
    void load() {
        String filename = ClassPathUtil.getResource("all_type.xlsx").getFile();
        System.out.println(filename);
        ExcelWorkbook<ExcelSheet> workbook = ExcelWorkbook.from(new File(filename));

        List<ExcelSheet> sheets = workbook.getSheets();
        for (ExcelSheet sheet : sheets) {
            printSheetVerbose(sheet);
        }
    }
}
