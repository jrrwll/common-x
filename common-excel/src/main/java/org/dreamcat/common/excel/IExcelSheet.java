package org.dreamcat.common.excel;

import org.dreamcat.common.excel.content.ExcelPicture;

import java.util.Collections;
import java.util.List;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelSheet extends Iterable<IExcelCell> {

    String getName();

    default List<IExcelWriteCallback> getWriteCallbacks() {
        return Collections.emptyList();
    }

    default List<ExcelPicture> getPictures() {
        return Collections.emptyList();
    }
}
