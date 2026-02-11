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
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.dreamcat.common.excel.annotation.ExcelColumnStyle;
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

    private ExcelColor fillColor;
    private ExcelColor fillBaseColor; // barely used, don't work on SOLID_FOREGROUND;
    private FillPatternType fillPattern;

    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderTop;
    private BorderStyle borderRight;

    private ExcelColor bottomBorderColor;
    private ExcelColor leftBorderColor;
    private ExcelColor topBorderColor;
    private ExcelColor rightBorderColor;

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
        newStyle.fillColor = this.fillColor;
        newStyle.fillBaseColor = this.fillBaseColor;
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

    public ExcelStyle merge(ExcelStyle style) {
        if (style == null) return this;

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
        if (style.fillColor != null) {
            this.fillColor = style.fillColor;
        }
        if (style.fillBaseColor != null) {
            this.fillBaseColor = style.fillBaseColor;
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
        if (style.bottomBorderColor != null) {
            this.bottomBorderColor = style.bottomBorderColor;
        }
        if (style.leftBorderColor != null) {
            this.leftBorderColor = style.leftBorderColor;
        }
        if (style.topBorderColor != null) {
            this.topBorderColor = style.topBorderColor;
        }
        if (style.rightBorderColor != null) {
            this.rightBorderColor = style.rightBorderColor;
        }
        return this;
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
        excelStyle.setIndent(style.getIndention());
        excelStyle.setRotation(style.getIndention());

        excelStyle.setFillColor(ExcelColor.from(style.getFillForegroundColorColor()));
        excelStyle.setFillBaseColor(ExcelColor.from(style.getFillBackgroundColorColor()));
        excelStyle.setFillPattern(style.getFillPattern());

        excelStyle.setBorderBottom(style.getBorderBottom());
        excelStyle.setBorderLeft(style.getBorderLeft());
        excelStyle.setBorderTop(style.getBorderTop());
        excelStyle.setBorderRight(style.getBorderRight());

        if (style instanceof XSSFCellStyle) {
            XSSFCellStyle xssfStyle = (XSSFCellStyle) style;
            excelStyle.setBottomBorderColor(ExcelColor.from(xssfStyle.getBottomBorderXSSFColor()));
            excelStyle.setLeftBorderColor(ExcelColor.from(xssfStyle.getLeftBorderXSSFColor()));
            excelStyle.setTopBorderColor(ExcelColor.from(xssfStyle.getTopBorderXSSFColor()));
            excelStyle.setRightBorderColor(ExcelColor.from(xssfStyle.getRightBorderXSSFColor()));
        } else {
            excelStyle.setBottomBorderColor(new ExcelColor(style.getBottomBorderColor()));
            excelStyle.setLeftBorderColor(new ExcelColor(style.getLeftBorderColor()));
            excelStyle.setTopBorderColor(new ExcelColor(style.getTopBorderColor()));
            excelStyle.setRightBorderColor(new ExcelColor(style.getRightBorderColor()));
        }
        return excelStyle;
    }

    public static ExcelStyle from(ExcelColumnStyle columnStyle) {
        ExcelStyle style = new ExcelStyle();
        style.setDataFormat(columnStyle.dataFormat());
        style.setHorizontalAlignment(columnStyle.horizontalAlignment());
        style.setVerticalAlignment(columnStyle.verticalAlignment());
        style.setHidden(columnStyle.hidden());
        style.setWrapText(columnStyle.wrapText());
        style.setLocked(columnStyle.locked());
        style.setQuotePrefix(columnStyle.quotePrefix());
        style.setShrinkToFit(columnStyle.shrinkToFit());
        // rich style
        style.setIndent(columnStyle.indent());
        style.setRotation(columnStyle.rotation());

        String fillColor = columnStyle.fillColor();
        if (!fillColor.isEmpty()) {
            style.setFillColor(ExcelColor.fromRgba(fillColor));
        } else if (columnStyle.fillColorIndex() != IndexedColors.AUTOMATIC){
            style.setFillColor(new ExcelColor(columnStyle.fillColorIndex().getIndex()));
        }
        String fillBaseColor = columnStyle.fillBaseColor();
        if (!fillBaseColor.isEmpty()) {
            style.setFillBaseColor(ExcelColor.fromRgba(fillBaseColor));
        } else if (columnStyle.fillBaseColorIndex() != IndexedColors.AUTOMATIC){
            style.setFillBaseColor(new ExcelColor(columnStyle.fillBaseColorIndex().getIndex()));
        }
        if (style.getFillColor() != null) {
            style.setFillPattern(columnStyle.fillPattern());
        }

        style.setBorderBottom(columnStyle.borderBottom());
        style.setBorderLeft(columnStyle.borderLeft());
        style.setBorderTop(columnStyle.borderTop());
        style.setBorderRight(columnStyle.borderRight());

        String bottomBorderColor = columnStyle.bottomBorderColor();
        if (!bottomBorderColor.isEmpty()) {
            style.setBottomBorderColor(ExcelColor.fromRgba(bottomBorderColor));
        } else if (columnStyle.bottomBorderColorIndex() != IndexedColors.AUTOMATIC){
            style.setBottomBorderColor(new ExcelColor(columnStyle.bottomBorderColorIndex().getIndex()));
        }
        String leftBorderColor = columnStyle.leftBorderColor();
        if (!leftBorderColor.isEmpty()) {
            style.setLeftBorderColor(ExcelColor.fromRgba(leftBorderColor));
        } else if (columnStyle.leftBorderColorIndex() != IndexedColors.AUTOMATIC){
            style.setLeftBorderColor(new ExcelColor(columnStyle.leftBorderColorIndex().getIndex()));
        }
        String topBorderColor = columnStyle.topBorderColor();
        if (!topBorderColor.isEmpty()) {
            style.setTopBorderColor(ExcelColor.fromRgba(topBorderColor));
        } else if (columnStyle.topBorderColorIndex() != IndexedColors.AUTOMATIC){
            style.setTopBorderColor(new ExcelColor(columnStyle.topBorderColorIndex().getIndex()));
        }
        String rightBorderColor = columnStyle.rightBorderColor();
        if (!rightBorderColor.isEmpty()) {
            style.setRightBorderColor(ExcelColor.fromRgba(rightBorderColor));
        } else if (columnStyle.rightBorderColorIndex() != IndexedColors.AUTOMATIC){
            style.setRightBorderColor(new ExcelColor(columnStyle.rightBorderColorIndex().getIndex()));
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
        if (fillColor != null) {
            fillColor.fill(style::setFillForegroundColor, style::setFillForegroundColor);
            if (fillBaseColor != null) {
                fillBaseColor.fill(style::setFillBackgroundColor, style::setFillBackgroundColor);
            }
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

        if (style instanceof XSSFCellStyle) {
            XSSFCellStyle xssfStyle = (XSSFCellStyle) style;
            if (bottomBorderColor != null) {
                bottomBorderColor.fill(xssfStyle::setBottomBorderColor, xssfStyle::setBottomBorderColor);
            }
            if (leftBorderColor != null) {
                leftBorderColor.fill(xssfStyle::setLeftBorderColor, xssfStyle::setLeftBorderColor);
            }
            if (topBorderColor != null) {
                topBorderColor.fill(xssfStyle::setTopBorderColor, xssfStyle::setTopBorderColor);
            }
            if (rightBorderColor != null) {
                rightBorderColor.fill(xssfStyle::setRightBorderColor, xssfStyle::setRightBorderColor);
            }
        } else {
            if (bottomBorderColor != null) {
                bottomBorderColor.fill(style::setBottomBorderColor);
            }
            if (leftBorderColor != null) {
                leftBorderColor.fill(style::setLeftBorderColor);
            }
            if (topBorderColor != null) {
                topBorderColor.fill(style::setTopBorderColor);
            }
            if (rightBorderColor != null) {
                rightBorderColor.fill(style::setRightBorderColor);
            }
        }
    }

    static boolean hasColor(int color) {
        return color != -1 && color != IndexedColors.AUTOMATIC.getIndex();
    }

    public ExcelStyle fillColor(IndexedColors color) {
        this.fillColor = new ExcelColor(color.getIndex());
        return this;
    }

    public ExcelStyle borderColor(IndexedColors color) {
        ExcelColor excelColor = new ExcelColor(color.getIndex());
        this.bottomBorderColor = excelColor;
        this.leftBorderColor = excelColor;
        this.topBorderColor = excelColor;
        this.rightBorderColor = excelColor;
        return this;
    }

    public ExcelStyle borderColor(IndexedColors color, BorderStyle style) {
        this.borderBottom = style;
        this.borderLeft = style;
        this.borderTop = style;
        this.borderRight = style;
        return borderColor(color);
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

    public ExcelStyle fontBold() {
        if (this.font == null) {
            this.font = new ExcelFont();
        }
        this.font.setBold(true);
        return this;
    }
}
