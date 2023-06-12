package org.dreamcat.common.excel.parse;

import java.io.File;
import java.util.List;
import org.dreamcat.common.excel.ExcelUtil;

/**
 * Create by tuke on 2020/8/14
 */
public interface IExcelParser<T> {

    default List<T> readSheetAsValue(File excelFile, int sheetIndex) throws Exception {
        return readSheetAsValue(ExcelUtil.parseAsString(excelFile, sheetIndex));
    }

    default List<T> readSheetAsValue(File excelFile, String sheetName) throws Exception {
        return readSheetAsValue(ExcelUtil.parseAsString(excelFile, sheetName));
    }

    List<T> readSheetAsValue(List<List<String>> sheet) throws Exception;
}
