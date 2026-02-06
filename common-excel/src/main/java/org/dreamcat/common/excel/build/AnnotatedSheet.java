package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.style.ExcelStyle;

import java.util.Iterator;
import java.util.List;

/**
 * Create by tuke on 2020/7/26
 */
@Setter
@NoArgsConstructor
public class AnnotatedSheet<T> implements IExcelSheet {

    @Getter
    private String name;
    private ExcelStyle defaultStyle;

    private Class<T> headerClass;
    private List<T> data; // data

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    private class Iter extends ExcelCellWithOffset implements Iterator<IExcelCell> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public IExcelCell next() {
            return null;
        }
    }
}
