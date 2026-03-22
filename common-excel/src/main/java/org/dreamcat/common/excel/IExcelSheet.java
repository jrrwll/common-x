package org.dreamcat.common.excel;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.excel.content.ExcelPicture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelSheet extends Iterable<IExcelCell> {

    String getName();

    List<IExcelWriteCallback> getWriteCallbacks();

    void addWriteCallback(IExcelWriteCallback callback);

    default List<ExcelPicture> getPictures() {
        return Collections.emptyList();
    }

    @Getter
    @Setter
    abstract class Base implements IExcelSheet {

        protected String name;
        protected final List<IExcelWriteCallback> writeCallbacks = new ArrayList<>();

        @Override
        public void addWriteCallback(IExcelWriteCallback writeCallback) {
            writeCallbacks.add(writeCallback);
        }
    }
}
