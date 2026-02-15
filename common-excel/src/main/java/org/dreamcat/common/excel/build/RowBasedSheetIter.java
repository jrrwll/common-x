package org.dreamcat.common.excel.build;

import org.dreamcat.common.excel.IExcelCell;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Jerry Will
 * @version 2026-02-04
 */
abstract class RowBasedSheetIter<T> extends ExcelCellWithOffset implements Iterator<IExcelCell> {

    final List<T> body;

    int size;
    int index;

    boolean hasData;
    Iterator<IExcelCell> headerIter;
    Iterator<IExcelCell> rowIter;
    int prevOffset;
    int maxRowOffset;

    abstract Iterator<IExcelCell> getHeaderCells();

    abstract Iterator<IExcelCell> getColumnCells(T row);

    RowBasedSheetIter(List<T> body) {
        this.body = body;

        size = body != null ? body.size() : 0;

        headerIter = getHeaderCells();
        if (headerIter != null) {
            hasData = true;
            return;
        }
        rowIter = skipEmptyRows();
        if (rowIter != null) {
            hasData = true;
        }
    }

    Iterator<IExcelCell> skipEmptyRows() {
        while (index < size) {
            T row = body.get(index);
            if (row != null) {
                Iterator<IExcelCell> cells = getColumnCells(row);
                if (cells.hasNext()) {
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

        offset = prevOffset;
        if (headerIter != null && headerIter.hasNext()) {
            cell = headerIter.next();
            // update the max row offset
            maxRowOffset = Math.max(cell.getRowIndex() + cell.getRowSpan(), maxRowOffset);

            if (!headerIter.hasNext()) {
                rowIter = skipEmptyRows();
                if (rowIter != null) {
                    prevOffset += maxRowOffset;
                    maxRowOffset = 0;
                } else {
                    hasData = false;
                }
            }
            return this;
        }

        cell = rowIter.next();
        // update the max row offset
        maxRowOffset = Math.max(cell.getRowIndex() + cell.getRowSpan(), maxRowOffset);
        if (!rowIter.hasNext()) {
            index++;
            rowIter = skipEmptyRows();
            if (rowIter != null) {
                prevOffset += maxRowOffset;
                maxRowOffset = 0;
            } else {
                hasData = false;
            }
        }
        return this;
    }
}
