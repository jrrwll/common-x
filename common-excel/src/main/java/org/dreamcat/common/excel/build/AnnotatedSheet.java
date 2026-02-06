package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.asm.BeanMapUtil;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.content.ExcelUnionContent;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelStyle;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Create by tuke on 2020/7/26
 */
@Setter
public class AnnotatedSheet<T> implements IExcelSheet {

    @Getter
    private String name;
    private ExcelStyle defaultStyle;
    private boolean headerless;

    private final Class<T> header;
    private List<T> body; // data

    private transient XlsHeaderMeta headerMeta;

    public AnnotatedSheet(Class<T> header) {
        this.header = header;
        this.headerMeta = XlsHeaderMeta.parse(header);
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
        ListIter rowIter;
        int nextOffset;

        private Iter() {
            size = body != null ? body.size() : 0;

            if (!headerless) {
                headerIter = headerMeta.getHeaderCells().iterator();
                hasData = true;
                return;
            }
            T row = skipEmptyRows();
            if (row != null) {
                rowIter = new ListIter(row, defaultStyle);
                hasData = true;
            }
        }

        private T skipEmptyRows() {
            while (index < size) {
                T row = body.get(index);
                if (row != null) {
                    return row;
                }
                index++;
            }
            return null;
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

    private class ListIter implements Iterator<IExcelCell>, IExcelCell {

        Map<String, Object> row;
        int size;
        int offset;

        ExcelUnionContent prevContent = new ExcelUnionContent();
        int prevOffset;

        private void reset(T row) {
            this.row = BeanMapUtil.toShallowMap(row);
        }

        @Override
        public int getRowIndex() {
            return 0;
        }

        @Override
        public int getColumnIndex() {
            return prevOffset;
        }

        @Override
        public IExcelContent getContent() {
            return prevContent;
        }

        @Override
        public ExcelStyle getStyle() {
            return IExcelCell.super.getStyle();
        }

        @Override
        public boolean hasNext() {
            return offset < size;
        }

        @Override
        public IExcelCell next() {
            return null;
        }

    }
}
