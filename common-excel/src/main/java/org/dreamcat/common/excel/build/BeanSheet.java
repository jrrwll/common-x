package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.annotation.ExcelType;
import org.dreamcat.common.excel.style.ExcelStyle;

import java.util.Iterator;
import java.util.List;

/**
 * Create by tuke on 2021/5/29
 */
@Setter
@SuppressWarnings({"rawtypes", "unchecked"})
public class BeanSheet<T> implements IExcelSheet {

    @Getter
    private String name;
    private ExcelStyle defaultStyle;
    private boolean headerless;
    private DefaultDataFormat defaultDataFormat = new DefaultDataFormat();

    private List<T> body; // data

    private final ExcelType.Value excelType;

    public BeanSheet(Class<T> header) {
        this.excelType = ExcelType.Value.parse(header, defaultDataFormat);
        this.name = excelType.getName();
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    private class Iter extends RowBasedSheetIter<T> {

        private Iter() {
            super(BeanSheet.this.body);
        }

        @Override
        Iterator<IExcelCell> getHeaderCells() {
            if (headerless) return null;

            return (Iterator) excelType.getHeaderCells().first().iterator();
        }

        @Override
        Iterator<IExcelCell> getColumnCells(T row) {
            return (Iterator) excelType.getColumnCells(row).iterator();
        }
    }

}
