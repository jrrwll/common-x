package org.dreamcat.common.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.util.ListUtil;
import org.dreamcat.common.util.StringUtil;

/**
 * Create by tuke on 2020/8/13
 */
public final class ExcelUtil {

    private ExcelUtil() {
    }

    public static List<List<List<String>>> parseAsString(String filename)
            throws IOException, InvalidFormatException {
        return stringify3d(parse(filename));
    }

    public static List<List<List<String>>> parseAsString(File file)
            throws IOException, InvalidFormatException {
        return stringify3d(parse(file));
    }

    public static List<List<List<String>>> parseAsString(InputStream input) throws IOException {
        return stringify3d(parse(input));
    }

    public static List<List<List<String>>> parseAsString(Workbook workbook) {
        return stringify3d(parse(workbook));
    }

    private static List<List<List<String>>> stringify3d(List<List<List<Object>>> list3d) {
        return ListUtil.map(list3d, (list2d, i) -> stringify2d(list2d));
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static List<List<String>> parseAsString(String filename, int sheetIndex)
            throws IOException, InvalidFormatException {

        return stringify2d(parse(filename, sheetIndex));
    }

    public static List<List<String>> parseAsString(File file, int sheetIndex)
            throws IOException, InvalidFormatException {
        return stringify2d(parse(file, sheetIndex));
    }

    public static List<List<String>> parseAsString(InputStream input, int sheetIndex)
            throws IOException {
        return stringify2d(parse(input, sheetIndex));
    }

    public static List<List<String>> parseAsString(String filename, String sheetName)
            throws IOException, InvalidFormatException {
        return stringify2d(parse(filename, sheetName));
    }

    public static List<List<String>> parseAsString(File file, String sheetName)
            throws IOException, InvalidFormatException {
        return stringify2d(parse(file, sheetName));
    }

    public static List<List<String>> parseAsString(InputStream input, String sheetName)
            throws IOException {
        return stringify2d(parse(input, sheetName));
    }

    public static List<List<String>> parseAsString(Workbook workbook, int sheetIndex) {
        return stringify2d(parse(workbook, sheetIndex));
    }

    public static List<List<String>> parseAsString(Workbook workbook, String sheetName) {
        return stringify2d(parse(workbook, sheetName));
    }

    public static List<List<String>> parseAsString(Sheet sheet) {
        return stringify2d(parse(sheet));
    }

    private static List<List<String>> stringify2d(List<List<Object>> list2d) {
        if (list2d == null) return null;
        List<List<String>> result2d = new ArrayList<>(list2d.size());
        for (List<Object> list : list2d) {
            if (list == null) {
                result2d.add(null);
                continue;
            }
            result2d.add(ListUtil.map(list, (v, i) -> StringUtil.toString(v)));
        }
        return result2d;
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static List<List<List<Object>>> parse(String filename)
            throws IOException, InvalidFormatException {
        return parse(new File(filename));
    }

    public static List<List<List<Object>>> parse(File file)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return parse(workbook);
        }
    }

    public static List<List<List<Object>>> parse(InputStream input) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(input)) {
            return parse(workbook);
        }
    }

    public static List<List<List<Object>>> parse(Workbook workbook) {
        int sheetNum = workbook.getNumberOfSheets();
        List<List<List<Object>>> sheets = new ArrayList<>(sheetNum);
        if (sheetNum == 0) return sheets;

        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            sheets.add(parse(sheet));
        }
        return sheets;
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static List<List<Object>> parse(String filename, int sheetIndex)
            throws IOException, InvalidFormatException {
        return parse(new File(filename), sheetIndex);
    }

    public static List<List<Object>> parse(File file, int sheetIndex)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return parse(workbook, sheetIndex);
        }
    }

    public static List<List<Object>> parse(InputStream input, int sheetIndex) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(input)) {
            return parse(workbook, sheetIndex);
        }
    }

    public static List<List<Object>> parse(String filename, String sheetName)
            throws IOException, InvalidFormatException {
        return parse(new File(filename), sheetName);
    }

    public static List<List<Object>> parse(File file, String sheetName)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return parse(workbook, sheetName);
        }
    }

    public static List<List<Object>> parse(InputStream input, String sheetName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(input)) {
            return parse(workbook, sheetName);
        }
    }

    public static List<List<Object>> parse(Workbook workbook, int sheetIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (sheet == null) return null;
        return parse(sheet);
    }

    public static List<List<Object>> parse(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return null;
        return parse(sheet);
    }

    /**
     * parse a sheet to 2-D list contains String/Double/Boolean
     *
     * @param sheet excel sheet
     * @return 2-D list
     * @see IExcelContent#valueOf(Cell)
     */
    public static List<List<Object>> parse(Sheet sheet) {
        int rowNum = sheet.getLastRowNum();
        List<List<Object>> rowValues = new ArrayList<>();
        for (int i = 0; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            // the row is not defined on the sheet
            if (row == null) {
                rowValues.add(null);
                continue;
            }

            int end = row.getLastCellNum();

            List<Object> columnValues = new ArrayList<>();
            for (int j = 0; j < end; j++) {
                Cell cell = row.getCell(j);
                // undefined cell
                if (cell == null) {
                    columnValues.add(null);
                    continue;
                }

                columnValues.add(IExcelContent.valueOf(cell));
            }
            rowValues.add(columnValues);
        }
        return rowValues;
    }
}
