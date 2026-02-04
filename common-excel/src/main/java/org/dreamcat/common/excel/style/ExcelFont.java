package org.dreamcat.common.excel.style;

import static org.dreamcat.common.excel.style.ExcelStyle.hasColor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.dreamcat.common.excel.annotation.XlsFont;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ExcelFont {

    // https://en.wikipedia.org/wiki/Calibri
    // https://en.wikipedia.org/wiki/Helvetica
    private String name;
    private Boolean bold;
    private Boolean italic;
    /**
     * @see Font#U_NONE
     * @see Font#U_SINGLE
     * @see Font#U_DOUBLE
     * @see Font#U_SINGLE_ACCOUNTING
     * @see Font#U_DOUBLE_ACCOUNTING
     */
    private byte underline = -1;
    // use a strikeout horizontal line through the text or not
    private Boolean strikeout;
    /**
     * @see Font#SS_NONE
     * @see Font#SS_SUPER
     * @see Font#SS_SUB
     */
    private short typeOffset = -1;
    /**
     * @see Font#COLOR_NORMAL
     * @see Font#COLOR_RED
     */
    private short color = -1;
    // font height in points, such as 10 or 14 or 28
    private short height = 0;

    public ExcelFont(String fontName) {
        this.name = fontName;
    }

    public static ExcelFont merge(ExcelFont font, ExcelFont defaultFont) {
        if (font == null) return defaultFont;
        if (defaultFont == null) return font;

        ExcelFont newFont = new ExcelFont();
        newFont.name = defaultFont.name;
        if (font.name != null) {
            newFont.name = font.name;
        }
        newFont.bold = defaultFont.bold;
        if (font.bold != null) {
            newFont.bold = font.bold;
        }
        newFont.italic = defaultFont.italic;
        if (font.italic != null) {
            newFont.italic = font.italic;
        }
        newFont.underline = defaultFont.underline;
        if (font.underline != -1) {
            newFont.underline = font.underline;
        }
        newFont.strikeout = defaultFont.strikeout;
        if (font.strikeout != null) {
            newFont.strikeout = font.strikeout;
        }
        newFont.typeOffset = defaultFont.typeOffset;
        if (font.typeOffset != -1) {
            newFont.typeOffset = font.typeOffset;
        }
        newFont.color = defaultFont.color;
        if (hasColor(font.color)) {
            newFont.color = font.color;
        }
        newFont.height = defaultFont.height;
        if (font.height != 0) {
            newFont.height = font.height;
        }
        return newFont;
    }

    public static ExcelFont from(Font font) {
        ExcelFont excelFont = new ExcelFont();
        excelFont.setName(font.getFontName());
        excelFont.setBold(font.getBold());
        excelFont.setItalic(font.getItalic());
        excelFont.setUnderline(font.getUnderline());
        excelFont.setStrikeout(font.getStrikeout());
        excelFont.setTypeOffset(font.getTypeOffset());
        excelFont.setColor(font.getColor());
        excelFont.setHeight(font.getFontHeightInPoints());
        return excelFont;
    }

    public static ExcelFont from(Workbook workbook, CellStyle style) {
        Font font;
        if (style instanceof XSSFCellStyle) {
            font = ((XSSFCellStyle) style).getFont();
        } else if (style instanceof HSSFCellStyle) {
            font = ((HSSFCellStyle) style).getFont(workbook);
        } else {
            return null;
        }

        return from(font);
    }

    public static ExcelFont from(XlsFont xlsFont) {
        ExcelFont font = new ExcelFont();

        if (!xlsFont.name().isEmpty()) font.setName(xlsFont.name());
        font.setBold(xlsFont.bold());
        font.setItalic(xlsFont.italic());

        font.setStrikeout(xlsFont.strikeout());
        if (xlsFont.underline() != -1) font.setUnderline(xlsFont.underline());
        if (xlsFont.typeOffset() != -1) font.setTypeOffset(xlsFont.typeOffset());

        if (xlsFont.color() != -1) {
            font.setColor(xlsFont.color());
        } else {
            font.setColor(xlsFont.indexedColor().getIndex());
        }
        if (xlsFont.height() != -1) font.setHeight(xlsFont.height());

        return font;
    }

    public void fill(Font font) {
        if (name != null) font.setFontName(name);
        if (bold != null) font.setBold(bold);
        if (italic != null) font.setItalic(italic);
        if (underline != -1) font.setUnderline(underline);
        if (strikeout != null) font.setStrikeout(strikeout);
        if (typeOffset != -1) font.setTypeOffset(typeOffset);
        if (hasColor(color)) font.setColor(color);
        if (height != 0) font.setFontHeightInPoints(height);
    }

    public static Font getFont(int fontIndex, Workbook workbook) {
        int fontNum = workbook.getNumberOfFonts();
        if (fontIndex >= 0 && fontIndex < fontNum) {
            return workbook.getFontAt(fontIndex);
        }
        return null;
    }

    public ExcelFont color(IndexedColors color) {
        this.color = color.getIndex();
        return this;
    }

    public ExcelFont height(int height) {
        this.height = (short) height;
        return this;
    }
}
