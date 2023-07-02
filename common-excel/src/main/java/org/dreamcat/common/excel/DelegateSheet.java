package org.dreamcat.common.excel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Create by tuke on 2021/2/16
 */
@Data
public class DelegateSheet implements IExcelSheet {

    private final IExcelSheet delegate;
    private String name;
    private List<IExcelWriteCallback> writeCallbacks;

    public DelegateSheet(IExcelSheet delegate) {
        this.delegate = delegate;
        this.name = delegate.getName();
        this.writeCallbacks = new ArrayList<>(delegate.getWriteCallbacks());
    }

    @Override
    public void fill(Sheet sheet, int sheetIndex, IExcelWorkbook<?> excelWorkbook) {
        delegate.fill(sheet, sheetIndex, excelWorkbook);
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return delegate.iterator();
    }
}
