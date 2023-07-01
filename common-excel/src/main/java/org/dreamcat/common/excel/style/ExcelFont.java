package org.dreamcat.common.excel.style;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.dreamcat.common.excel.annotation.XlsFont;

/**
 * Create by tuke on 2020/7/21
 */
@Data
public class ExcelFont {

    private int index = -1; // unique index number, only for
    private String name;
    private boolean bold;
    private boolean italic;
    /**
     * @see Font#U_NONE
     * @see Font#U_SINGLE
     * @see Font#U_DOUBLE
     * @see Font#U_SINGLE_ACCOUNTING
     * @see Font#U_DOUBLE_ACCOUNTING
     */
    private byte underline;
    // use a strikeout horizontal line through the text or not
    private boolean strikeout;
    /**
     * @see Font#SS_NONE
     * @see Font#SS_SUPER
     * @see Font#SS_SUB
     */
    private short typeOffset;
    /**
     * @see Font#COLOR_NORMAL
     * @see Font#COLOR_RED
     */
    private short color;
    // font height in points, such as 10 or 14 or 28
    private short height;

    public ExcelFont() {
        // https://en.wikipedia.org/wiki/Calibri
        // https://en.wikipedia.org/wiki/Helvetica
        this("Helvetica");
    }

    public ExcelFont(String fontName) {
        this.name = fontName;
    }

    public static ExcelFont from(Font font) {
        ExcelFont excelFont = new ExcelFont();
        excelFont.index = font.getIndex();
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
        font.setFontName(name);
        font.setBold(bold);
        font.setItalic(italic);
        font.setUnderline(underline);
        font.setStrikeout(strikeout);
        font.setColor(color);
        font.setTypeOffset(typeOffset);
        font.setFontHeightInPoints(height);
    }

    public static Font getFont(int fontIndex, Workbook workbook) {
        int fontNum = workbook.getNumberOfFonts();
        if (fontIndex >= 0 && fontIndex < fontNum) {
            return workbook.getFontAt(fontIndex);
        }
        return null;
    }
}
