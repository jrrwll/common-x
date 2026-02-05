package org.dreamcat.common.excel.build;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.content.ExcelUnionContent;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.ArrayUtil;
import org.dreamcat.common.util.ListUtil;
import org.dreamcat.common.util.ObjectUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    private boolean disableDefaultDataFormat;
    private String defaultDateTimeDataFormat = "yyyy-MM-dd HH:mm:ss";
    private String defaultDateDataFormat = "yyyy-MM-dd";
    private String defaultTimeDataFormat = "HH:mm:ss";

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
            if (headerStyle != null) {
                defaultHeaderStyle = headerStyle.copy();
            }
        } else {
            defaultHeaderStyle = defaultStyle.copy();
            if (headerStyle != null) {
                defaultHeaderStyle.merge(headerStyle);
            }
        }

        headerStyles = mergeStyles(columnStyles, defaultHeaderStyle);
        bodyStyles = mergeStyles(columnStyles, defaultStyle);

        // add default data format for body
        if (!disableDefaultDataFormat && ObjectUtil.isNotEmpty(body)) {
            List<Object> firstRow = null;
            for (List<Object> row : body) {
                if (ObjectUtil.isNotEmpty(row)) {
                    firstRow = row;
                    break;
                }
            }
            if (firstRow != null) {
                bodyStyles = computeDataFormat(bodyStyles, firstRow);
            }
        }

        return this.new Iter();
    }

    private static List<ExcelStyle> mergeStyles(List<ExcelStyle> columnStyles, ExcelStyle style) {
        if (ObjectUtil.isEmpty(columnStyles)) {
            return null;
        }
        return columnStyles.stream().map(columnStyle -> {
            ExcelStyle defaultStyle = style != null ? style.copy() : null;
            if (columnStyle == null) {
                return defaultStyle;
            } else if (defaultStyle == null) {
                return columnStyle.copy();
            } else {
                defaultStyle.merge(columnStyle);
                return defaultStyle;
            }
        }).collect(Collectors.toList());
    }

    private List<ExcelStyle> computeDataFormat(List<ExcelStyle> styles, List<Object> row) {
        Map<Integer, String> dataFormatMap = new HashMap<>();
        int n = row.size();
        for (int i = 0; i < n; i++) {
            Object cell = row.get(i);
            String dataFormat;
            if (cell instanceof Date || cell instanceof LocalDateTime) {
                dataFormat = defaultDateTimeDataFormat;
            } else if (cell instanceof LocalDate) {
                dataFormat = defaultDateDataFormat;
            } else if (cell instanceof LocalTime) {
                dataFormat = defaultTimeDataFormat;
            } else {
                continue;
            }
            dataFormatMap.put(i, dataFormat);
        }
        if (dataFormatMap.isEmpty()) {
            return styles;
        }
        if (ObjectUtil.isEmpty(styles)) {
            return ArrayUtil.mapRangeToList(0, n, i -> {
                String dataFormat = dataFormatMap.get(i);
                if (dataFormat == null) return null;
                else return new ExcelStyle().setDataFormat(dataFormat);
            });
        }
        int m = Math.max(styles.size(), n);
        List<ExcelStyle> newStyles = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            ExcelStyle style = ListUtil.getOrNull(styles, i);
            String dataFormat = dataFormatMap.get(i);
            if (dataFormat == null) continue;

            if (style == null) {
                style = new ExcelStyle();
            }
            style.setDataFormat(dataFormat);
            newStyles.add(style);
        }
        return newStyles;
    }

    private class Iter extends ExcelCellWithOffset implements Iterator<IExcelCell> {

        int size;
        int index;

        boolean hasData;
        ListIter headerIter;
        ListIter rowIter;
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
                if (ObjectUtil.isNotEmpty(row)) {
                    rowIter = new ListIter(row, bodyStyles, defaultStyle);
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
                        if (ObjectUtil.isNotEmpty(row)) {
                            rowIter = new ListIter(row, bodyStyles, defaultStyle);
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
                    if (ObjectUtil.isNotEmpty(row)) {
                        rowIter.reset(row);
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

        final List<ExcelStyle> styles;
        final ExcelStyle defaultStyle;

        List<?> row;
        int size;
        int offset;

        ExcelUnionContent prevContent = new ExcelUnionContent();
        int prevOffset;

        private ListIter(List<?> row, List<ExcelStyle> styles, ExcelStyle defaultStyle) {
            this.reset(row);
            this.styles = styles;
            this.defaultStyle = defaultStyle;
        }

        private void reset(List<?> row) {
            this.row = row;
            this.size = row.size();
            this.offset = 0;
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
            return ListUtil.getOrElse(this.styles, prevOffset, this.defaultStyle);
        }

        @Override
        public boolean hasNext() {
            return offset < size;
        }

        @Override
        public IExcelCell next() {
            prevContent.setContent(row.get(offset));
            prevOffset = offset;

            offset++;
            return this;
        }
    }

}
