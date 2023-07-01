package org.dreamcat.common.excel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.excel.content.ExcelBooleanContent;
import org.dreamcat.common.excel.content.ExcelDateContent;
import org.dreamcat.common.excel.content.ExcelNumericContent;
import org.dreamcat.common.excel.content.ExcelStringContent;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelHyperLink;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.util.DateUtil;

/**
 * Create by tuke on 2020/7/22
 */
public final class ExcelBuilder {

    private ExcelBuilder() {
    }

    public static SheetTerm sheet(String sheetName) {
        return new SheetTerm(new ExcelSheet(sheetName));
    }

    public static WorkbookTerm workbook() {
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();
        return new WorkbookTerm(book);
    }

    public static IExcelContent term(Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            return new ExcelNumericContent(number.doubleValue());
        } else if (value instanceof Boolean) {
            return new ExcelBooleanContent((Boolean) value);
        } else if (value instanceof Date) {
            return new ExcelDateContent((Date) value);
        } else if (value instanceof Calendar) {
            return new ExcelDateContent(((Calendar) value).getTime());
        } else if (value instanceof LocalDate) {
            LocalDateTime localDateTime = ((LocalDate) value).atStartOfDay();
            return new ExcelDateContent(DateUtil.asDate(localDateTime));
        } else if (value instanceof LocalDateTime) {
            return new ExcelDateContent(DateUtil.asDate((LocalDateTime) value));
        } else if (value instanceof IExcelContent) {
            return (IExcelContent) value;
        } else {
            return ExcelStringContent.from(value == null ? "" : value.toString());
        }
    }

    public static IExcelContent term(String string) {
        return ExcelStringContent.from(string);
    }

    public static IExcelContent term(double number) {
        return new ExcelNumericContent(number);
    }

    public static ExcelCell term(String string, int rowIndex, int columnIndex) {
        return new ExcelCell(term(string), rowIndex, columnIndex);
    }

    public static ExcelCell term(String string, int rowIndex, int columnIndex, int rowSpan,
            int columnSpan) {
        return new ExcelCell(term(string), rowIndex, columnIndex, rowSpan, columnSpan);
    }

    public static StyleTerm style() {
        return new StyleTerm();
    }

    public static FontTerm font() {
        return new FontTerm();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @RequiredArgsConstructor
    public static class WorkbookTerm {

        private final ExcelWorkbook<ExcelSheet> book;

        public WorkbookTerm sheet(SheetTerm sheetTerm) {
            return sheet(sheetTerm.finish());
        }

        public WorkbookTerm sheet(ExcelSheet sheet) {
            book.getSheets().add(sheet);
            return this;
        }

        public ExcelWorkbook<ExcelSheet> finish() {
            return book;
        }
    }

    @RequiredArgsConstructor
    public static class SheetTerm {

        private final ExcelSheet sheet;

        public ExcelSheet finish() {
            return sheet;
        }

        public SheetTerm cell(IExcelCell cell) {
            sheet.getCells().add(cell);
            return this;
        }

        public SheetTerm cell(IExcelContent term, int rowIndex, int columnIndex) {
            return cell(term, rowIndex, columnIndex, 1, 1);
        }

        public SheetTerm cell(double number, int rowIndex, int columnIndex) {
            return cell(term(number), rowIndex, columnIndex);
        }

        public SheetTerm cell(double number, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return cell(term(number), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public SheetTerm cell(String string, int rowIndex, int columnIndex) {
            return cell(term(string), rowIndex, columnIndex);
        }

        public SheetTerm cell(String string, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return cell(term(string), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public SheetTerm cell(IExcelContent term, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            sheet.getCells().add(new ExcelCell(term, rowIndex, columnIndex, rowSpan, columnSpan));
            return this;
        }

        public CellTerm richCell(String string, int rowIndex, int columnIndex) {
            return richCell(term(string), rowIndex, columnIndex, 1, 1);
        }

        public CellTerm richCell(double number, int rowIndex, int columnIndex) {
            return richCell(term(number), rowIndex, columnIndex, 1, 1);
        }

        public CellTerm richCell(IExcelContent term, int rowIndex, int columnIndex) {
            return richCell(term, rowIndex, columnIndex, 1, 1);
        }

        public CellTerm richCell(String string, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return richCell(term(string), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public CellTerm richCell(double number, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return richCell(term(number), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public CellTerm richCell(IExcelContent term, int rowIndex, int columnIndex,
                int rowSpan, int columnSpan) {
            ExcelCell cell = new ExcelCell(term, rowIndex, columnIndex, rowSpan, columnSpan);
            sheet.getCells().add(cell);
            return new CellTerm(this, cell);
        }
    }

    public static class CellTerm {

        private final SheetTerm sheetTerm;
        private final ExcelCell cell;

        public CellTerm(SheetTerm sheetTerm, ExcelCell cell) {
            this.sheetTerm = sheetTerm;
            this.cell = cell;
        }

        public SheetTerm finishCell() {
            return sheetTerm;
        }

        public CellTerm hyperLink(String address) {
            return hyperLink(address, null);
        }

        public CellTerm hyperLink(String address, String label) {
            return hyperLink(address, label, HyperlinkType.URL);
        }

        public CellTerm hyperLink(String address, String label, HyperlinkType type) {
            cell.setHyperLink(new ExcelHyperLink(type, address, label));
            return this;
        }

        public CellTerm style(ExcelStyle style) {
            cell.setStyle(style);
            return this;
        }

        public CellTerm font(ExcelFont font) {
            cell.setFont(font);
            return this;
        }
    }

    public static class StyleTerm {

        private final ExcelStyle style = new ExcelStyle();

        public ExcelStyle finish() {
            return style;
        }

        public StyleTerm horizontalAlignment(HorizontalAlignment horizontalAlignment) {
            style.setHorizontalAlignment(horizontalAlignment);
            return this;
        }

        public StyleTerm verticalAlignment(VerticalAlignment verticalAlignment) {
            style.setVerticalAlignment(verticalAlignment);
            return this;
        }

        public StyleTerm hidden() {
            return hidden(true);
        }

        public StyleTerm hidden(boolean hidden) {
            style.setHidden(hidden);
            return this;
        }

        public StyleTerm wrapText() {
            return wrapText(true);
        }

        public StyleTerm wrapText(boolean wrapText) {
            style.setWrapText(wrapText);
            return this;
        }

        public StyleTerm locked() {
            return locked(true);
        }

        public StyleTerm locked(boolean locked) {
            style.setLocked(locked);
            return this;
        }

        public StyleTerm quotePrefix() {
            return quotePrefix(true);
        }

        public StyleTerm quotePrefix(boolean quotePrefix) {
            style.setQuotePrefix(quotePrefix);
            return this;
        }

        public StyleTerm shrinkToFit() {
            return shrinkToFit(true);
        }

        public StyleTerm shrinkToFit(boolean shrinkToFit) {
            style.setShrinkToFit(shrinkToFit);
            return this;
        }

        // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

        public StyleTerm rotation(short rotation) {
            style.setRotation(rotation);
            return this;
        }

        public StyleTerm bgColor(short bgColor) {
            style.setBgColor(bgColor);
            return this;
        }

        public StyleTerm bgColor(IndexedColors bgColor) {
            return bgColor(bgColor.getIndex());
        }

        public StyleTerm fgColor(short fgColor) {
            style.setFgColor(fgColor);
            return this;
        }

        public StyleTerm fgColor(IndexedColors fgColor) {
            return fgColor(fgColor.getIndex());
        }

        public StyleTerm fillPattern(FillPatternType fillPatternType) {
            style.setFillPattern(fillPatternType);
            return this;
        }

        public StyleTerm borderBottom(BorderStyle borderBottom) {
            style.setBorderBottom(borderBottom);
            return this;
        }

        public StyleTerm borderLeft(BorderStyle borderLeft) {
            style.setBorderLeft(borderLeft);
            return this;
        }

        public StyleTerm borderTop(BorderStyle borderTop) {
            style.setBorderTop(borderTop);
            return this;
        }

        public StyleTerm borderRight(BorderStyle borderRight) {
            style.setBorderRight(borderRight);
            return this;
        }

        public StyleTerm bottomBorderColor(short bottomBorderColor) {
            style.setBottomBorderColor(bottomBorderColor);
            return this;
        }

        public StyleTerm leftBorderColor(short leftBorderColor) {
            style.setLeftBorderColor(leftBorderColor);
            return this;
        }

        public StyleTerm topBorderColor(short topBorderColor) {
            style.setTopBorderColor(topBorderColor);
            return this;
        }

        public StyleTerm rightBorderColor(short rightBorderColor) {
            style.setRightBorderColor(rightBorderColor);
            return this;
        }
    }

    public static class FontTerm {

        private final ExcelFont font = new ExcelFont();

        public ExcelFont finish() {
            return font;
        }

        public FontTerm bold() {
            return bold(true);
        }

        public FontTerm bold(boolean bold) {
            font.setBold(bold);
            return this;
        }

        public FontTerm italic() {
            return italic(true);
        }

        public FontTerm italic(boolean italic) {
            font.setItalic(italic);
            return this;
        }

        public FontTerm underline() {
            return underline(Font.U_SINGLE);
        }

        public FontTerm underline(byte underline) {
            font.setUnderline(underline);
            return this;
        }

        public FontTerm strikeout() {
            return strikeout(true);
        }

        public FontTerm strikeout(boolean strikeout) {
            font.setStrikeout(strikeout);
            return this;
        }

        public FontTerm typeOffset() {
            return typeOffset(Font.SS_NONE);
        }

        public FontTerm typeOffset(short typeOffset) {
            font.setTypeOffset(typeOffset);
            return this;
        }

        public FontTerm color() {
            return color(Font.COLOR_NORMAL);
        }

        public FontTerm color(short color) {
            font.setColor(color);
            return this;
        }

        public FontTerm height(int height) {
            font.setHeight((short) height);
            return this;
        }
    }

}
