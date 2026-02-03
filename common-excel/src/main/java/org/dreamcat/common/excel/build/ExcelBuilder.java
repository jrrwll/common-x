package org.dreamcat.common.excel.build;

import org.dreamcat.common.excel.ExcelWorkbook;
import org.dreamcat.common.excel.IExcelSheet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Create by tuke on 2020/7/22
 */
public class ExcelBuilder {

    private final ExcelWorkbook<IExcelSheet> workbook = new ExcelWorkbook<>();

    private ExcelBuilder() {
    }

    public static ExcelBuilder build() {
        return new ExcelBuilder();
    }

    public void writeTo(File file) throws IOException {
        workbook.writeTo(file);
    }

    public void writeTo(OutputStream output) throws IOException {
        workbook.writeTo(output);
    }

    public ExcelBuilder addBasicSheet() {
        return this;
    }

    public ExcelBuilder addMixedSheet() {
        return this;
    }

    public ExcelBuilder addSheet(IExcelSheet sheet) {
        workbook.addSheet(sheet);
        return this;
    }

    public static void main(String[] args) throws Exception {
        ExcelBuilder.build()
                .addBasicSheet()
                .addMixedSheet()
                .addSheet(new MixedSheet(""))
                .writeTo(new File("test.xlsx"));
    }
}
