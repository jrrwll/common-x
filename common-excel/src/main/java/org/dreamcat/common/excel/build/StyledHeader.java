package org.dreamcat.common.excel.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.content.ExcelStringContent;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelStyle;

/**
 * @author Jerry Will
 * @version 2026-02-01
 */
@Getter
@Setter
@AllArgsConstructor
public class StyledHeader {

    private String name;
    private ExcelStyle style;

    public StyledHeader(String name) {
        this.name = name;
    }

    public StyledHeader(ExcelStyle style) {
        this.style = style;
    }

    public IExcelCell toCell(int columnIndex) {
        return new Cell(columnIndex);
    }

    @RequiredArgsConstructor
    private class Cell implements IExcelCell {

        private final int columnIndex;

        @Override
        public int getRowIndex() {
            return 0;
        }

        @Override
        public int getColumnIndex() {
            return columnIndex;
        }

        @Override
        public IExcelContent getContent() {
            return ExcelStringContent.from(name);
        }

        @Override
        public ExcelStyle getStyle() {
            return style;
        }
    }
}
