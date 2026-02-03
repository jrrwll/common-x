package org.dreamcat.common.excel.build;

import lombok.RequiredArgsConstructor;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.content.IExcelContent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Create by tuke on 2020/7/22
 * <p>
 * schema type, one of:
 * - {@link Collection}
 * - {@link Object[]}
 * - {@link java.util.Map}
 * - {@link "Pojo Type"}
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SchemaSheet implements IExcelSheet {

    private SheetStyle style;
    private List schemas;

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    private class Iter implements Iterator<IExcelCell>, IExcelCell {

        int schemeSize;
        int schemeIndex;

        SchemaIter schemaIter;

        @Override
        public int getRowIndex() {
            return 0;
        }

        @Override
        public int getColumnIndex() {
            return 0;
        }

        @Override
        public IExcelContent getContent() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return schemeIndex < schemeSize;
        }

        @Override
        public IExcelCell next() {
            if (!hasNext()) throw new NoSuchElementException();

            return null;
        }

        // Note that it makes hasNext() return false
        private void clear() {
            schemeIndex = 0;
            schemeSize = 0;
            schemaIter = null;
        }

        private void move() {
            for (; ; ) {
                Object rawRow = schemas.get(schemeIndex);
                if (schemaIter != null) {
                    schemaIter.reset(rawRow);
                } else {
                    schemaIter = SchemaSheet.this.new SchemaIter(rawRow);
                }
                if (schemaIter.hasNext()) break;

                // reach an empty sheet, then skip it
                schemeIndex++;
                if (schemeIndex >= schemeSize) {
                    clear();
                    return;
                }
            }
        }
    }

    @RequiredArgsConstructor
    private class SchemaIter implements Iterator<IExcelCell> {

        private Object scheme;

        private SchemaIter(Object scheme) {
            reset(scheme);
        }

        public void reset(Object scheme) {
            this.scheme = scheme;
        }

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
