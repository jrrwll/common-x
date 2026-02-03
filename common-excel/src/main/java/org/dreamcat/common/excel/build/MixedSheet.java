package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.util.ObjectUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Create by tuke on 2020/7/22
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class MixedSheet implements IExcelSheet {

    @Setter
    private String name;
    // [Sheet..., T1..., Sheet..., T2...], it mixes Sheet & Pojo up, and treat Pojo as a Sheet
    private final List schemes;

    public MixedSheet(String name) {
        this(name, new ArrayList<>(0));
    }

    public MixedSheet(String name, List schemes) {
        this.name = name;
        this.schemes = schemes;
    }

    public void addRow(IExcelSheet row) {
        schemes.add(row);
    }

    public void addRow(Object row) {
        schemes.add(row);
    }

    public <C extends Collection<?>> void addAll(C schemes) {
        this.schemes.addAll(schemes);
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    private class Iter extends ExcelCellWithOffset implements Iterator<IExcelCell> {

        int schemeSize;
        int schemeIndex;

        Iterator<IExcelCell> sheetIter;
        SchemaIter sheet;
        int maxRowOffset;
        boolean inSwitchOffsetCase;

        private Iter() {
            if (ObjectUtil.isEmpty(schemes)) {
                clear();
                return;
            }

            schemeIndex = 0;
            schemeSize = schemes.size();
            move();
        }

        @Override
        public boolean hasNext() {
            return schemeIndex < schemeSize;
        }

        @Override
        public IExcelCell next() {
            if (!hasNext()) throw new NoSuchElementException();

            cell = sheetIter.next();
            if (inSwitchOffsetCase) {
                offset += maxRowOffset;
                inSwitchOffsetCase = false;
                maxRowOffset = 0;
            }

            // update the max row offset
            maxRowOffset = Math.max(cell.getRowIndex() + cell.getRowSpan(), maxRowOffset);

            if (!sheetIter.hasNext()) {
                inSwitchOffsetCase = true;
                schemeIndex++;
                if (schemeIndex >= schemeSize) {
                    clear();
                } else {
                    move();
                }
            }
            return this;
        }

        // Note that it makes hasNext() return false
        private void clear() {
            schemeIndex = 0;
            schemeSize = 0;
            sheetIter = null;
        }

        private void move() {
            for (; ; ) {
                Object rawRow = schemes.get(schemeIndex);
                if (rawRow instanceof IExcelSheet) {
                    sheetIter = ((IExcelSheet) rawRow).iterator();
                } else {
                    // if (sheet != null) {
                    //     sheet.reset(rawRow);
                    // } else {
                    //     sheet = new MixedRowSheet2(name, rawRow);
                    //     sheet.setSchemeConverter(schemeConverter);
                    // }
                    // sheetIter = sheet.iterator();
                }
                if (sheetIter.hasNext()) break;

                // reach a empty sheet, then skip it
                schemeIndex++;
                if (schemeIndex >= schemeSize) {
                    clear();
                    return;
                }
            }
        }
    }

    private static class SchemaIter implements Iterator<IExcelCell> {

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
