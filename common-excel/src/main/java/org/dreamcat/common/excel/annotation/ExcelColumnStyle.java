package org.dreamcat.common.excel.annotation;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by tuke on 2020/7/23
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelColumnStyle {

    HorizontalAlignment horizontalAlignment() default HorizontalAlignment.RIGHT;

    VerticalAlignment verticalAlignment() default VerticalAlignment.CENTER;

    String dataFormat() default "";

    boolean hidden() default false;

    boolean wrapText() default false;

    boolean locked() default false;

    boolean quotePrefix() default false;

    boolean shrinkToFit() default false;

    /// rich style

    short indent() default -1;

    // HSSF uses values from -90 to 90 degrees,
    // whereas XSSF uses values from 0 to 180 degrees
    short rotation() default 0;

    // RGB or RGBA, e.g. #FFFFFF, #FFFFFFFF
    String fillColor() default "";

    String fillBaseColor() default "";

    IndexedColors fillColorIndex() default IndexedColors.AUTOMATIC;

    IndexedColors fillBaseColorIndex() default IndexedColors.AUTOMATIC;

    FillPatternType fillPattern() default FillPatternType.SOLID_FOREGROUND;

    BorderStyle borderBottom() default BorderStyle.NONE;

    BorderStyle borderLeft() default BorderStyle.NONE;

    BorderStyle borderTop() default BorderStyle.NONE;

    BorderStyle borderRight() default BorderStyle.NONE;

    String bottomBorderColor() default "";

    String leftBorderColor() default "";

    String topBorderColor() default "";

    String rightBorderColor() default "";

    IndexedColors bottomBorderColorIndex() default IndexedColors.AUTOMATIC;

    IndexedColors leftBorderColorIndex() default IndexedColors.AUTOMATIC;

    IndexedColors topBorderColorIndex() default IndexedColors.AUTOMATIC;

    IndexedColors rightBorderColorIndex() default IndexedColors.AUTOMATIC;
}
