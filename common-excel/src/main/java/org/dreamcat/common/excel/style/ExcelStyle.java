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

    public ExcelStyle copy() {
        ExcelStyle newStyle = new ExcelStyle();
        if (this.font != null) {
            newStyle.font = this.font.copy();
        }
        newStyle.dataFormat = this.dataFormat;
        newStyle.horizontalAlignment = this.horizontalAlignment;
        newStyle.verticalAlignment = this.verticalAlignment;
        newStyle.hidden = this.hidden;
        newStyle.wrapText = this.wrapText;
        newStyle.locked = this.locked;
        newStyle.quotePrefix = this.quotePrefix;
        newStyle.shrinkToFit = this.shrinkToFit;
        newStyle.indent = this.indent;
        newStyle.rotation = this.rotation;
        newStyle.bgColor = this.bgColor;
        newStyle.fgColor = this.fgColor;
        newStyle.fillPattern = this.fillPattern;

        newStyle.borderBottom = this.borderBottom;
        newStyle.borderLeft = this.borderLeft;
        newStyle.borderTop = this.borderTop;
        newStyle.borderRight = this.borderRight;
        newStyle.bottomBorderColor = this.bottomBorderColor;
        newStyle.leftBorderColor = this.leftBorderColor;
        newStyle.topBorderColor = this.topBorderColor;
        newStyle.rightBorderColor = this.rightBorderColor;
        return newStyle;
    }

    public void merge(ExcelStyle style) {
        if (style == null) return;

        if (style.font != null) {
            if (this.font == null) {
                this.font = new ExcelFont();
            }
            this.font.merge(style.font);
        }
        if (style.dataFormat != null) {
            this.dataFormat = style.dataFormat;
        }
        if (style.horizontalAlignment != null) {
            this.horizontalAlignment = style.horizontalAlignment;
        }
        if (style.verticalAlignment != null) {
            this.verticalAlignment = style.verticalAlignment;
        }
        if (style.hidden != null) {
            this.hidden = style.hidden;
        }
        if (style.wrapText != null) {
            this.wrapText = style.wrapText;
        }
        if (style.locked != null) {
            this.locked = style.locked;
        }
        if (style.quotePrefix != null) {
            this.quotePrefix = style.quotePrefix;
        }
        if (style.shrinkToFit != null) {
            this.shrinkToFit = style.shrinkToFit;
        }
        if (style.indent != -1) {
            this.indent = style.indent;
        }
        if (style.rotation != 0) {
            this.rotation = style.rotation;
        }
        if (hasColor(style.bgColor)) {
            this.bgColor = style.bgColor;
        }
        if (hasColor(style.fgColor)) {
            this.fgColor = style.fgColor;
        }
        if (style.fillPattern != null) {
            this.fillPattern = style.fillPattern;
        }
        if (style.borderBottom != null) {
            this.borderBottom = style.borderBottom;
        }
        if (style.borderLeft != null) {
            this.borderLeft = style.borderLeft;
        }
        if (style.borderTop != null) {
            this.borderTop = style.borderTop;
        }
        if (style.borderRight != null) {
            this.borderRight = style.borderRight;
        }
        if (hasColor(style.bottomBorderColor)) {
            this.bottomBorderColor = style.bottomBorderColor;
        }
        if (hasColor(style.leftBorderColor)) {
            this.leftBorderColor = style.leftBorderColor;
        }
        if (hasColor(style.topBorderColor)) {
            this.topBorderColor = style.topBorderColor;
        }
        if (hasColor(style.rightBorderColor)) {
            this.rightBorderColor = style.rightBorderColor;
        }
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
