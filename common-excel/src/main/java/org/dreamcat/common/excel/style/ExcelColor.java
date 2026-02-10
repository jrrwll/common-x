package org.dreamcat.common.excel.style;

import lombok.Getter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.function.Consumer;

/**
 * @author Jerry Will
 * @version 2026-02-04
 */
@Getter
public class ExcelColor {

    private String argbHex;
    private short index = -1; // index or theme, negative for theme
    private int themeIndex = -1;
    private double tint;

    public ExcelColor(short index) {
        this.index = index;
    }

    public ExcelColor(String argbHex) {
        this.argbHex = argbHex;
    }

    public ExcelColor(int themeIndex, double tint) {
        this.themeIndex = themeIndex;
        this.tint = tint;
    }

    public static ExcelColor fromRgba(String rgbOrRgba) {
        int n = rgbOrRgba.length();
        if (n == 7 || n ==9) {
            rgbOrRgba = rgbOrRgba.substring(1);
            n--;
        }
        if (n == 6) {
            return new ExcelColor(rgbOrRgba);
        } else if (n == 8) {
            // rgba -> argb
            rgbOrRgba = rgbOrRgba.substring(6) + rgbOrRgba.substring(0, 6);
            return new ExcelColor(rgbOrRgba);
        } else {
            throw new IllegalArgumentException("Must be like one of [112233, #112233, FFEEDDCC, #FFEEDDCC]");
        }
    }

    public static ExcelColor from(Color color) {
        if (color == null) return null;
        if (color instanceof XSSFColor) {
            XSSFColor xssfColor = (XSSFColor)color;
            if (xssfColor.isIndexed()) {
                short index = xssfColor.getIndex();
                return new ExcelColor(index);
            } else if (xssfColor.isRGB()) {
                String argbHex = xssfColor.getARGBHex();
                return new ExcelColor(argbHex);
            } else if (xssfColor.isThemed()) {
                int themeIndex = xssfColor.getTheme();
                double tint = xssfColor.getTint();
                return new ExcelColor(themeIndex, tint);
            } else {
                return null;
            }
        } else if (color instanceof HSSFColor) {
            HSSFColor hssfColor = (HSSFColor)color;
            short index = hssfColor.getIndex();
            return new ExcelColor(index);
        } else {
            return null;
        }
    }

    public void fill(Consumer<Short> indexSetter) {
        if (index != -1) {
            if (index != IndexedColors.AUTOMATIC.getIndex()) {
                indexSetter.accept(index);
            }
        }
    }

    public void fill(Consumer<? super XSSFColor> colorSetter, Consumer<Short> indexSetter) {
        if (index != -1) {
            if (index != IndexedColors.AUTOMATIC.getIndex()) {
                indexSetter.accept(index);
            }
            return;
        }
        XSSFColor color = new XSSFColor();
        if (argbHex != null) {
            color.setARGBHex(argbHex);
        } else if (themeIndex != -1) {
            color.setTheme(themeIndex);
            color.setTint(tint);
        } else {
            return;
        }
        colorSetter.accept(color);
    }
}