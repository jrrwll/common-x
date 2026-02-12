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
public class TableSheet implements IExcelSheet {

    @Getter
    private String name;
    private ExcelStyle defaultStyle;
    private ExcelStyle headerStyle;
    private List<ExcelStyle> headerStyles; // headerStyles first, headerStyle second
    private List<ExcelStyle> bodyStyles;
    private DefaultDataFormat defaultDataFormat = new DefaultDataFormat();

    private List<String> header; // header only support one line
    private List<List<Object>> body;

    @Setter(AccessLevel.NONE)
    private transient ExcelStyle finalHeaderStyle;
    @Setter(AccessLevel.NONE)
    private transient List<ExcelStyle> finalHeaderStyles;
    @Setter(AccessLevel.NONE)
    private transient List<ExcelStyle> finalBodyStyles;

    @Override
    public Iterator<IExcelCell> iterator() {
        // header style
        if (defaultStyle == null) {
            finalHeaderStyle = headerStyle;
            finalHeaderStyles = headerStyles;
        } else {
            if (headerStyle == null) {
                finalHeaderStyle = defaultStyle;
            } else {
                finalHeaderStyle = defaultStyle.copy();
                finalHeaderStyle.merge(headerStyle);
            }
            if (ObjectUtil.isNotEmpty(headerStyles)) {
                finalHeaderStyles = mergeDefaultStyle(headerStyles);
            }
        }

        if (defaultStyle != null) {
            if (finalHeaderStyle == null) {
                finalHeaderStyle = defaultStyle;
            } else {
                finalHeaderStyle = defaultStyle.copy();
            }
        }

        // body style
        finalBodyStyles = bodyStyles;
        // add default data format for body
        if (!defaultDataFormat.isDisable() && body != null && !body.isEmpty()) {
            for (List<Object> row : body) {
                if (row != null && !row.isEmpty()) {
                    finalBodyStyles = computeDataFormat(bodyStyles, row);
                    break;
                }
            }
        } else if (defaultStyle != null) {
            if (bodyStyles != null) {
                finalBodyStyles = mergeDefaultStyle(bodyStyles);
            }
        }
        return this.new Iter();
    }

    private List<ExcelStyle> computeDataFormat(List<ExcelStyle> styles, List<Object> row) {
        Map<Integer, String> dataFormatMap = new HashMap<>();
        int n = row.size();
        for (int i = 0; i < n; i++) {
            Object cell = row.get(i);
            String dataFormat = defaultDataFormat.getDataFormat(cell);
            if (dataFormat == null) continue;

            dataFormatMap.put(i, dataFormat);
        }
        if (dataFormatMap.isEmpty()) {
            if (ObjectUtil.isEmpty(styles)) {
                return styles;
            } else {
                return mergeDefaultStyle(styles);
            }
        }
        if (styles == null || styles.isEmpty()) {
            List<ExcelStyle> newStyles = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                String dataFormat = dataFormatMap.get(i);
                if (dataFormat != null) {
                    ExcelStyle style = new ExcelStyle().setDataFormat(dataFormat);
                    if (defaultStyle != null) {
                        style = defaultStyle.copy().merge(style);
                    }
                    newStyles.add(style);
                } else {
                    newStyles.add(null);
                }
            }
            return newStyles;
        }

        int m = Math.max(styles.size(), n);
        List<ExcelStyle> newStyles = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            ExcelStyle style = i < styles.size() ? styles.get(i) : null;
            String dataFormat = i < n ? dataFormatMap.get(i) : null;
            if (dataFormat != null) {
                if (style == null) {
                    if (defaultStyle != null) {
                        style = defaultStyle.copy();
                    } else {
                        style = new ExcelStyle();
                    }
                } else {
                    if (defaultStyle != null) {
                        style = defaultStyle.copy().merge(style);
                    } else {
                        style = style.copy(); // avoid reuse styles
                    }
                }
                style.setDataFormat(dataFormat);
            } else if (style != null) {
                if (defaultStyle != null) {
                    style = defaultStyle.copy().merge(style);
                }
            }
            newStyles.add(style);
        }
        return newStyles;
    }

    private List<ExcelStyle> mergeDefaultStyle(List<ExcelStyle> styles) {
        if (defaultStyle == null) return styles;
        return styles.stream().map(style -> {
            ExcelStyle newStyle = defaultStyle.copy();
            newStyle.merge(style);
            return newStyle;
        }).collect(Collectors.toList());
    }

    private class Iter extends RowBasedSheetIter<List<Object>> {

        ListIter rowIter;

        private Iter() {
            super(TableSheet.this.body);
        }

        @Override
        Iterator<IExcelCell> getHeaderCells() {
            if (ObjectUtil.isEmpty(header)) return null;

            return new ListIter(header, finalHeaderStyles, finalHeaderStyle);
        }

        @Override
        Iterator<IExcelCell> getColumnCells(List<Object> row) {
            if (rowIter == null) {
                rowIter = new ListIter(row, finalBodyStyles, defaultStyle);
            } else {
                rowIter.reset(row);
            }
            return rowIter;
        }
    }

    private static class ListIter implements Iterator<IExcelCell>, IExcelCell {

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
            if (styles != null && prevOffset < styles.size()) {
                return styles.get(prevOffset);
            }
            return defaultStyle;
        }

        @Override
        public boolean hasNext() {
            return offset < size;
        }

        @Override
        public IExcelCell next() {
            prevContent.setContent(row.get(offset));
            prevOffset = offset++;
            return this;
        }
    }
}
