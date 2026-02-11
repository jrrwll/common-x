package org.dreamcat.common.excel;

import lombok.SneakyThrows;
import org.dreamcat.common.util.ClassLoaderUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

/**
 * Create by tuke on 2020/7/21
 */
class ExcelWorkbookTest extends BaseTest {

    @Test
    void export() {
        ExcelSheet sheet = new ExcelSheet("sheet1");
        sheet.addCell(new ExcelCell("A1:B2", 0, 0, 2, 2));
        sheet.addCell(new ExcelCell("C1:C2", 0, 2, 2, 1));
        sheet.addCell(new ExcelCell("D1:D2", 0, 3, 2, 1));
        sheet.addCell(new ExcelCell("A3:B3", 2, 0, 1, 2));
        sheet.addCell(new ExcelCell("C3", 2, 2));
        sheet.addCell(new ExcelCell("D3", 2, 3));
        sheet.addCell(new ExcelCell("A4:B4", 3, 0, 1, 2));
        sheet.addCell(new ExcelCell("C4", 3, 2));
        sheet.addCell(new ExcelCell("D4", 3, 3));

        writeXlsx("export", sheet);
    }

    @Test
    void reExport() throws Exception {
        ExcelWorkbook<ExcelSheet> workbook = ExcelWorkbook.from(outputFile("export", "xlsx"));
        File file = outputFile("reExport", "xlsx");
        System.out.println("output file: " + file);
        workbook.writeTo(file);
    }

    @Test
    void fromAnnotationRowSheetTestTest() {
        readXlsx("export", this::printSheetVerbose);
    }

    @SneakyThrows
    @Test
    void load() {
        String filename = ClassLoaderUtil.getResource("all_type.xlsx").getFile();
        System.out.println(filename);
        ExcelWorkbook<ExcelSheet> workbook = ExcelWorkbook.from(new File(filename));

        List<ExcelSheet> sheets = workbook.getSheets();
        for (ExcelSheet sheet : sheets) {
            printSheetVerbose(sheet);
        }
    }
}
