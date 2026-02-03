package org.dreamcat.common.excel.build;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ListUtil;
import org.dreamcat.common.util.ObjectUtil;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2020/7/22
 */
@Setter
@NoArgsConstructor
public class SimpleSheet implements IExcelSheet {

    @Getter
    private String name;
    private ExcelStyle defaultStyle;
    private ExcelStyle headerStyle;

    private List<ExcelStyle> columnStyles;
    private List<String> header;
    private List<List<Object>> body;

    @Setter(AccessLevel.NONE)
    private transient ExcelStyle defaultHeaderStyle;
    @Setter(AccessLevel.NONE)
    private transient List<ExcelStyle> headerStyles;
    @Setter(AccessLevel.NONE)
    private transient List<ExcelStyle> bodyStyles;

    @Override
    public Iterator<IExcelCell> iterator() {
        if (defaultStyle == null) {
            defaultHeaderStyle = headerStyle;
        } else if (headerStyle == null) {
            defaultHeaderStyle = defaultStyle;
        } else {
            defaultHeaderStyle = ExcelStyle.merge(headerStyle, defaultStyle);
        }
        headerStyles = mergeStyles(columnStyles, defaultHeaderStyle);
        bodyStyles = mergeStyles(columnStyles, defaultStyle);

        return this.new Iter();
    }

    private static List<ExcelStyle> mergeStyles(List<ExcelStyle> styles, ExcelStyle defaultStyle) {
        if (defaultStyle == null) {
            return styles;
        }
        if (ObjectUtil.isEmpty(styles)) {
            return null;
        }
        return styles.stream().map(style -> {
            if (style == null) {
                return defaultStyle;
            }
            return ExcelStyle.merge(style, defaultStyle);
        }).collect(Collectors.toList());
    }

    private class Iter extends ExcelCellWithOffset implements Iterator<IExcelCell> {

        int size;
        int index;

        boolean hasData;
        Iterator<IExcelCell> headerIter;
        Iterator<IExcelCell> rowIter;
        int nextOffset;

        private Iter() {
            if (body != null) {
                this.size = body.size();
            }

            if (ObjectUtil.isNotEmpty(header)) {
                headerIter = new ListIter(header, headerStyles, defaultHeaderStyle);
                this.hasData = true;
                return;
            }
            while (index < size) {
                List<Object> row = body.get(index);
                rowIter = new ListIter(row, bodyStyles, defaultStyle);
                if (rowIter.hasNext()) {
                    this.hasData = true;
                    break;
                }
                index++;
            }
        }

        @Override
        public boolean hasNext() {
            return hasData;
        }

        @Override
        public IExcelCell next() {
            if (!hasNext()) throw new NoSuchElementException();

            offset = nextOffset;
            if (headerIter.hasNext()) {
                cell = headerIter.next();
                if (!headerIter.hasNext()) {
                    while (index < size) {
                        List<Object> row = body.get(index);
                        rowIter = new ListIter(row, bodyStyles, defaultStyle);
                        if (rowIter.hasNext()) {
                            nextOffset++;
                            break;
                        }
                        index++;
                    }
                    if (index == size) {
                        hasData = false;
                    }
                }
                return this;
            }

            cell = rowIter.next();
            if (!rowIter.hasNext()) {
                index++;
                while (index < size) {
                    List<Object> row = body.get(index);
                    rowIter = new ListIter(row, bodyStyles, defaultStyle);
                    if (rowIter.hasNext()) {
                        nextOffset++;
                        break;
                    }
                    index++;
                }

                if (index == size) {
                    hasData = false;
                }
            }
            return this;
        }
    }

    private class ListIter implements Iterator<IExcelCell>, IExcelCell {

        final List<?> row;
        final List<ExcelStyle> styles;
        final ExcelStyle defaultStyle;

        int prevOffset;
        int offset;
        int size;

        private ListIter(List<?> row, List<ExcelStyle> styles, ExcelStyle defaultStyle) {
            this.row = row;
            if (row != null) {
                this.size = row.size();
            }
            this.styles = styles;
            this.defaultStyle = defaultStyle;
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
            return IExcelContent.from(row.get(prevOffset));
        }

        @Override
        public ExcelStyle getStyle() {
            return ListUtil.getOrElse(this.styles, prevOffset, this.defaultStyle);
        }

        @Override
        public boolean hasNext() {
            return offset < size;
        }

        @Override
        public IExcelCell next() {
            prevOffset = offset;
            offset++;
            return this;
        }
    }

}
