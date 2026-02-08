package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.annotation.ExcelType;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ObjectUtil;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Create by tuke on 2020/7/26
 */
@Setter
public class AnnotatedSheet<T> implements IExcelSheet {

    @Getter
    private String name;
    private ExcelStyle defaultStyle;
    private boolean headerless;

    private List<T> body; // data

    private final ExcelType.Value excelType;

    public AnnotatedSheet(Class<T> header) {
        this.excelType = ExcelType.Value.parse(header);
        this.name = excelType.getName();
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    private class Iter extends ExcelCellWithOffset implements Iterator<IExcelCell> {

        int size;
        int index;

        boolean hasData;
        Iterator<IExcelCell> headerIter;
        Iterator<IExcelCell> rowIter;
        int nextOffset;

        private Iter() {
            size = body != null ? body.size() : 0;

            if (!headerless) {
                headerIter = excelType.getHeaderCells().iterator();
                hasData = true;
                return;
            }
            List<IExcelCell> row = skipEmptyRows();
            if (row != null) {
                rowIter = row.iterator();
                hasData = true;
            }
        }

        private List<IExcelCell> skipEmptyRows() {
            while (index < size) {
                T row = body.get(index);
                if (row != null) {
                    List<IExcelCell> cells = excelType.getColumnCells(row);
                    if (ObjectUtil.isNotEmpty(cells)) {
                        return cells;
                    }
                }
                index++;
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return hasData;
        }

        @Override
        public IExcelCell next() {
            if (!hasNext()) throw new NoSuchElementException();

            offset = nextOffset;
            if (headerIter != null && headerIter.hasNext()) {
                cell = headerIter.next();
                if (!headerIter.hasNext()) {
                    List<IExcelCell> row = skipEmptyRows();
                    if (row != null) {
                        rowIter = row.iterator();
                        nextOffset++;
                    } else {
                        hasData = false;
                    }
                }
                return this;
            }

            cell = rowIter.next();
            if (!rowIter.hasNext()) {
                index++;
                List<IExcelCell> row = skipEmptyRows();
                if (row != null) {
                    rowIter = row.iterator();
                    nextOffset++;
                } else {
                    hasData = false;
                }
            }
            return this;
        }
    }
}
