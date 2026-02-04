package org.dreamcat.common.excel.style;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.excel.annotation.XlsStyle;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@Accessors(chain = true)
public class ExcelStyle {

    private ExcelFont font;

    private String dataFormat;
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private Boolean hidden;
    private Boolean wrapText;
    private Boolean locked;

    private Boolean quotePrefix;
    // Controls if the Cell should be auto-sized to shrink to fit if the text is too long
    private Boolean shrinkToFit;

    /// rich style

    private short indent = -1;
    // HSSF uses values from -90 to 90 degrees,
    // whereas XSSF uses values from 0 to 180 degrees
    private short rotation = 0;

    private short bgColor = -1;
    private short fgColor = -1;
    private FillPatternType fillPattern;

    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderTop;
    private BorderStyle borderRight;

    private short bottomBorderColor = -1;
    private short leftBorderColor = -1;
    private short topBorderColor = -1;
    private short rightBorderColor = -1;

    public static ExcelStyle merge(ExcelStyle style, ExcelStyle defaultStyle) {
        if (style == null) return defaultStyle;
        if (defaultStyle == null) return style;

        ExcelStyle newStyle = new ExcelStyle();
        newStyle.font = defaultStyle.font;
        if (style.font != null) {
            if (defaultStyle.font == null) {
                newStyle.font = style.font;
            } else {
                newStyle.font = ExcelFont.merge(style.font, defaultStyle.font);
            }
        }

        newStyle.dataFormat = defaultStyle.dataFormat;
        if (style.dataFormat != null) {
            newStyle.dataFormat = style.dataFormat;
        }
        newStyle.horizontalAlignment = defaultStyle.horizontalAlignment;
        if (style.horizontalAlignment != null) {
            newStyle.horizontalAlignment = style.horizontalAlignment;
        }
        newStyle.verticalAlignment = defaultStyle.verticalAlignment;
        if (style.verticalAlignment != null) {
            newStyle.verticalAlignment = style.verticalAlignment;
        }
        newStyle.hidden = defaultStyle.hidden;
        if (style.hidden != null) {
            newStyle.hidden = style.hidden;
        }
        newStyle.wrapText = defaultStyle.wrapText;
        if (style.wrapText != null) {
            newStyle.wrapText = style.wrapText;
        }
        newStyle.locked = defaultStyle.locked;
        if (style.locked != null) {
            newStyle.locked = style.locked;
        }

        newStyle.quotePrefix = defaultStyle.quotePrefix;
        if (style.quotePrefix != null) {
            newStyle.quotePrefix = style.quotePrefix;
        }
        newStyle.shrinkToFit = defaultStyle.shrinkToFit;
        if (style.shrinkToFit != null) {
            newStyle.shrinkToFit = style.shrinkToFit;
        }

        newStyle.indent = defaultStyle.indent;
        if (style.indent != -1) {
            newStyle.indent = style.indent;
        }
        newStyle.rotation = defaultStyle.rotation;
        if (style.rotation != 0) {
            newStyle.rotation = style.rotation;
        }

        newStyle.bgColor = defaultStyle.bgColor;
        if (hasColor(style.bgColor)) {
            newStyle.bgColor = style.bgColor;
        }
        newStyle.fgColor = defaultStyle.fgColor;
        if (hasColor(style.fgColor)) {
            newStyle.fgColor = style.fgColor;
        }
        newStyle.fillPattern = defaultStyle.fillPattern;
        if (style.fillPattern != null) {
            newStyle.fillPattern = style.fillPattern;
        }

        newStyle.borderBottom = defaultStyle.borderBottom;
        if (style.borderBottom != null) {
            newStyle.borderBottom = style.borderBottom;
        }
        newStyle.borderLeft = defaultStyle.borderLeft;
        if (style.borderLeft != null) {
            newStyle.borderLeft = style.borderLeft;
        }
        newStyle.borderTop = defaultStyle.borderTop;
        if (style.borderTop != null) {
            newStyle.borderTop = style.borderTop;
        }
        newStyle.borderRight = defaultStyle.borderRight;
        if (style.borderRight != null) {
            newStyle.borderRight = style.borderRight;
        }

        newStyle.bottomBorderColor = defaultStyle.bottomBorderColor;
        if (hasColor(style.bottomBorderColor)) {
            newStyle.bottomBorderColor = style.bottomBorderColor;
        }
        newStyle.leftBorderColor = defaultStyle.leftBorderColor;
        if (hasColor(style.leftBorderColor)) {
            newStyle.leftBorderColor = style.leftBorderColor;
        }
        newStyle.topBorderColor = defaultStyle.topBorderColor;
        if (hasColor(style.topBorderColor)) {
            newStyle.topBorderColor = style.topBorderColor;
        }
        newStyle.rightBorderColor = defaultStyle.rightBorderColor;
        if (hasColor(style.rightBorderColor)) {
            newStyle.rightBorderColor = style.rightBorderColor;
        }
        return newStyle;
    }

    public static ExcelStyle from(CellStyle style) {
        ExcelStyle excelStyle = new ExcelStyle();
        excelStyle.dataFormat = style.getDataFormatString();
        excelStyle.setHorizontalAlignment(style.getAlignment());
        excelStyle.setVerticalAlignment(style.getVerticalAlignment());
        excelStyle.setHidden(style.getHidden());
        excelStyle.setHidden(style.getWrapText());
        excelStyle.setHidden(style.getLocked());
        excelStyle.setQuotePrefix(style.getQuotePrefixed());
        excelStyle.setShrinkToFit(style.getShrinkToFit());
        // rich style
        excelStyle.setIndent(style.getIndention());
        excelStyle.setRotation(style.getIndention());
        excelStyle.setBgColor(style.getFillBackgroundColor());
        excelStyle.setFgColor(style.getFillForegroundColor());
        excelStyle.setFillPattern(style.getFillPattern());
        excelStyle.setBorderBottom(style.getBorderBottom());
        excelStyle.setBorderLeft(style.getBorderLeft());
        excelStyle.setBorderTop(style.getBorderTop());
        excelStyle.setBorderRight(style.getBorderRight());
        excelStyle.setBottomBorderColor(style.getBottomBorderColor());
        excelStyle.setLeftBorderColor(style.getLeftBorderColor());
        excelStyle.setTopBorderColor(style.getTopBorderColor());
        excelStyle.setRightBorderColor(style.getRightBorderColor());
        return excelStyle;
    }

    public static ExcelStyle from(XlsStyle xlsStyle) {
        ExcelStyle style = new ExcelStyle();
        style.setDataFormat(xlsStyle.dataFormat());
        style.setHorizontalAlignment(xlsStyle.horizontalAlignment());
        style.setVerticalAlignment(xlsStyle.verticalAlignment());
        style.setHidden(xlsStyle.hidden());
        style.setWrapText(xlsStyle.wrapText());
        style.setLocked(xlsStyle.locked());
        style.setQuotePrefix(xlsStyle.quotePrefix());
        style.setShrinkToFit(xlsStyle.shrinkToFit());
        // rich style
        style.setIndent(xlsStyle.indent());
        style.setRotation(xlsStyle.rotation());

        if (xlsStyle.bgColor() != -1) {
            style.setBgColor(xlsStyle.bgColor());
        } else {
            style.setBgColor(xlsStyle.bgIndexedColor().getIndex());
        }
        if (xlsStyle.fgColor() != -1) {
            style.setFgColor(xlsStyle.fgColor());
        } else {
            style.setFgColor(xlsStyle.fgIndexedColor().getIndex());
        }
        style.setFillPattern(xlsStyle.fillPattern());

        style.setBorderBottom(xlsStyle.borderBottom());
        style.setBorderLeft(xlsStyle.borderLeft());
        style.setBorderTop(xlsStyle.borderTop());
        style.setBorderRight(xlsStyle.borderRight());

        if (xlsStyle.bottomBorderColor() != -1) {
            style.setBottomBorderColor(xlsStyle.bottomBorderColor());
        } else {
            style.setBottomBorderColor(xlsStyle.bottomBorderIndexedColor().getIndex());
        }
        if (xlsStyle.leftBorderColor() != -1) {
            style.setLeftBorderColor(xlsStyle.leftBorderColor());
        } else {
            style.setLeftBorderColor(xlsStyle.leftBorderIndexedColor().getIndex());
        }
        if (xlsStyle.topBorderColor() != -1) {
            style.setTopBorderColor(xlsStyle.topBorderColor());
        } else {
            style.setTopBorderColor(xlsStyle.topBorderIndexedColor().getIndex());
        }
        if (xlsStyle.rightBorderColor() != -1) {
            style.setRightBorderColor(xlsStyle.rightBorderColor());
        } else {
            style.setRightBorderColor(xlsStyle.rightBorderIndexedColor().getIndex());
        }
        return style;
    }

    public void fill(CellStyle style, DataFormat dataFormat) {
        if (ObjectUtil.isNotEmpty(this.dataFormat)) {
            short format = dataFormat.getFormat(this.dataFormat);
            style.setDataFormat(format);
        }
        if (horizontalAlignment != null) style.setAlignment(horizontalAlignment);
        if (verticalAlignment != null) style.setVerticalAlignment(verticalAlignment);
        if (locked != null) style.setLocked(locked);
        if (quotePrefix != null) style.setQuotePrefixed(quotePrefix);
        if (shrinkToFit != null) style.setShrinkToFit(shrinkToFit);
        if (hidden != null) style.setHidden(hidden);
        if (wrapText != null) style.setWrapText(wrapText);

        if (indent != -1) style.setIndention(indent);
        if (rotation != 0) style.setRotation(rotation);
        if (hasColor(fgColor) || hasColor(bgColor)) {
            if (hasColor(fgColor)) style.setFillForegroundColor(fgColor);
            if (hasColor(bgColor)) style.setFillBackgroundColor(bgColor);
            if (fillPattern != null) {
                style.setFillPattern(fillPattern);
            } else {
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
        }

        if (borderBottom != null) style.setBorderBottom(borderBottom);
        if (borderLeft != null) style.setBorderLeft(borderLeft);
        if (borderTop != null) style.setBorderTop(borderTop);
        if (borderRight != null) style.setBorderRight(borderRight);

        if (hasColor(bottomBorderColor)) style.setBottomBorderColor(bottomBorderColor);
        if (hasColor(leftBorderColor)) style.setLeftBorderColor(leftBorderColor);
        if (hasColor(topBorderColor)) style.setTopBorderColor(topBorderColor);
        if (hasColor(rightBorderColor)) style.setRightBorderColor(rightBorderColor);
    }

    static boolean hasColor(int color) {
        return color != -1 && color != IndexedColors.AUTOMATIC.getIndex();
    }

    public ExcelStyle fgColor(IndexedColors color) {
        this.fgColor = color.getIndex();
        return this;
    }

    public ExcelStyle bgColor(IndexedColors color) {
        this.bgColor = color.getIndex();
        return this;
    }

    public ExcelStyle borderColor(IndexedColors color) {
        this.bottomBorderColor = color.getIndex();
        this.leftBorderColor = color.getIndex();
        this.topBorderColor = color.getIndex();
        this.rightBorderColor = color.getIndex();
        return this;
    }

    public ExcelStyle borderColor(IndexedColors color, BorderStyle style) {
        this.bottomBorderColor = color.getIndex();
        this.leftBorderColor = color.getIndex();
        this.topBorderColor = color.getIndex();
        this.rightBorderColor = color.getIndex();
        this.borderBottom = style;
        this.borderLeft = style;
        this.borderTop = style;
        this.borderRight = style;
        return this;
    }

    public ExcelStyle fontHeight(int height) {
        if (this.font == null) {
            this.font = new ExcelFont();
        }
        this.font.setHeight((short) height);
        return this;
    }

    public ExcelStyle fontName(String fontName) {
        if (this.font == null) {
            this.font = new ExcelFont();
        }
        this.font.setName(fontName);
        return this;
    }

    public ExcelStyle fontBold(boolean fontBold) {
        if (this.font == null) {
            this.font = new ExcelFont();
        }
        this.font.setBold(true);
        return this;
    }
}
