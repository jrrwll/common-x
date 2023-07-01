package org.dreamcat.common.excel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.function.IBiConsumer;
import org.dreamcat.common.function.IConsumer;
import org.dreamcat.common.util.SystemUtil;

/**
 * Create by tuke on 2021/2/15
 */
public class BaseTest {

    private static final String homeDir = SystemUtil.getEnvOrProperty("HOME", "user.dir", ".");
    protected static final File baseDir = new File(homeDir, "Downloads");
    protected static final String basePath = baseDir.getAbsolutePath();

    /// pattern

    @SafeVarargs
    public final <T extends IExcelSheet> void writeXlsxWithBigGrid(String name, T... sheets) {
        writeExcel(name, "xlsx", IExcelWorkbook::writeToWithBigGrid, sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeXlsx(String name, T... sheets) {
        writeExcel(name, "xlsx", IExcelWorkbook::writeTo, sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeXlsx(
            ExcelWorkbook<T> book, String name, T... sheets) {
        writeExcel(book, name, "xlsx", sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeExcel(
            ExcelWorkbook<T> book, String name, String suffix, T... sheets) {
        writeExcel(book, name, suffix, IExcelWorkbook::writeTo, sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeExcel(
            String name, String suffix,
            IBiConsumer<IExcelWorkbook<?>, File, IOException> writer, T... sheets) {
        ExcelWorkbook<T> book = new ExcelWorkbook<>();
        writeExcel(book, name, suffix, writer, sheets);
    }

    @SafeVarargs
    public final <T extends IExcelSheet> void writeExcel(
            ExcelWorkbook<T> book, String name, String suffix,
            IBiConsumer<IExcelWorkbook<?>, File, IOException> writer, T... sheets) {
        File file = new File(baseDir, getClass().getSimpleName() + "_" + name + "." + suffix);
        System.out.printf("writing to %s\n", file);
        try {
            writer.accept(book.addSheets(Arrays.asList(sheets)), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readXlsx(String name, IConsumer<ExcelSheet, IOException> callback) {
        readExcel(name, "xlsx", callback);
    }

    public void readExcel(String name, String suffix,
            IConsumer<ExcelSheet, IOException> callback) {
        ExcelWorkbook<ExcelSheet> book;
        try {
            book = ExcelWorkbook.from(new File(baseDir, getClass().getSimpleName() + "_" + name + "." + suffix));
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
            ExcelFont font = cell.getFont();
            ExcelStyle style = cell.getStyle();

            System.out.printf("[%d, %d, %d, %d] %s\n%s\n%s\n\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent(),
                    font, style
            );
        }
    }
}
