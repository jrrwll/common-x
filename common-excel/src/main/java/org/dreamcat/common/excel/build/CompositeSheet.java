package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.util.ObjectUtil;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Create by tuke on 2021/5/29
 */
@Setter
@NoArgsConstructor
public class CompositeSheet implements IExcelSheet {

    @Getter
    private String name;

    private List<IExcelSheet> sheets;

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    private class Iter extends ExcelCellWithOffset implements Iterator<IExcelCell> {

        int size;
        int index;

        Iterator<IExcelCell> sheetIter;
        int prevOffset;
        int maxRowOffset;

        private Iter() {
            if (ObjectUtil.isEmpty(sheets)) {
                return;
            }

            index = 0;
            size = sheets.size();
            sheetIter = skipEmptySheets();
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public IExcelCell next() {
            if (!hasNext()) throw new NoSuchElementException();

            offset = prevOffset;
            cell = sheetIter.next();
            // update the max row offset
            maxRowOffset = Math.max(cell.getRowIndex() + cell.getRowSpan(), maxRowOffset);

            if (!sheetIter.hasNext()) {
                index++;
                sheetIter = skipEmptySheets();
                prevOffset += maxRowOffset;
            }
            return this;
        }

        private Iterator<IExcelCell> skipEmptySheets() {
            while (index < size) {
                Iterator<IExcelCell> sheet = sheets.get(index).iterator();
                if (sheet.hasNext()) {
                    return sheet;
                }
                index++;
            }
            return null;
        }
    }
}
