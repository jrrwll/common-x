package org.dreamcat.common.excel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.function.IBiConsumer;
import org.dreamcat.common.function.IConsumer;

/**
 * Create by tuke on 2021/2/15
 */
public class BaseTest {

    protected static final String basePath = System.getenv("HOME") + "/Downloads";

    protected static final File baseDir = new File(basePath);

    /// pattern

    @SafeVarargs
    public final <T extends IExcelSheet> void writeXlsxWithBigGrid(String prefix, T... sheets) {
        writeExcel(prefix, "xlsx", IExcelWorkbook::writeToWithBigGrid, sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeXlsx(String prefix, T... sheets) {
        writeExcel(prefix, "xlsx", IExcelWorkbook::writeTo, sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeXlsx(
            ExcelWorkbook<T> book, String prefix, T... sheets) {
        writeExcel(book, prefix, "xlsx", sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeExcel(
            ExcelWorkbook<T> book, String prefix, String suffix, T... sheets) {
        writeExcel(book, prefix, suffix, IExcelWorkbook::writeTo, sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeExcel(
            String prefix, String suffix,
            IBiConsumer<IExcelWorkbook<?>, File, IOException> writer, T... sheets) {
        ExcelWorkbook<T> book = new ExcelWorkbook<>();
        writeExcel(book, prefix, suffix, writer, sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeExcel(
            ExcelWorkbook<T> book, String prefix, String suffix,
            IBiConsumer<IExcelWorkbook<?>, File, IOException> writer, T... sheets) {
        File file = new File(baseDir, prefix + "." + suffix);
        System.out.printf("writing to %s\n", file);
        try {
            writer.accept(book.addSheets(Arrays.asList(sheets)), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readXlsx(String prefix,
            IConsumer<ExcelSheet, IOException> callback) {
        readExcel(prefix, "xlsx", callback);
    }

    public void readExcel(String prefix, String suffix,
            IConsumer<ExcelSheet, IOException> callback) {
        ExcelWorkbook<ExcelSheet> book;
        try {
            book = ExcelWorkbook.from(new File(baseDir, prefix + "." + suffix));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<ExcelSheet> sheets = book.getSheets();
        for (ExcelSheet sheet : sheets) {
            try {
                callback.accept(sheet);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /// util

    public void printSheet(IExcelSheet sheet) {
        for (IExcelCell cell : sheet) {
            System.out.printf("[%d, %d, %d, %d] %s\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent());
        }
    }

    public void printSheetVerbose(IExcelSheet sheet) {
        for (IExcelCell cell : sheet) {
            ExcelFont font = null;
            ExcelStyle style = cell.getStyle();
            if (style != null) {
                font = style.getFont();
            }

            System.out.printf("[%d, %d, %d, %d] %s\n%s\n%s\n\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent(),
                    font, style
            );
        }
    }
}
