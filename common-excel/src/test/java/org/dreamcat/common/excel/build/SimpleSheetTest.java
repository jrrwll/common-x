package org.dreamcat.common.excel.build;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Create by tuke on 2020/7/22
 */
class SimpleSheetTest extends BaseTest {

    @Test
    void testSmall() {
        ExcelStyle defaultStyle = new ExcelStyle()
                .fontName("Times New Roman")
                .fontHeight(12);
        ExcelStyle defaultHeaderStyle = new ExcelStyle()
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.CENTER);

        SimpleSheet sheet1 = new SimpleSheet();
        sheet1.setDefaultStyle(defaultStyle);
        sheet1.setName("small");
        sheet1.setHeader(Arrays.asList("id", "name", "age"));
        sheet1.setBody(buildBody());

        SimpleSheet sheet2 = new SimpleSheet();
        sheet2.setHeaderStyle(defaultHeaderStyle);
        sheet2.setName("small_style");
        sheet2.setHeader(Arrays.asList("id", "name", "age"));
        sheet2.setBody(buildBody());

        ExcelStyle style1 = new ExcelStyle()
                .fontName("Arial")
                .fontHeight(10)
                .fontBold(true)
                .fgColor(IndexedColors.SKY_BLUE)
                .borderColor(IndexedColors.BLACK);
        ExcelStyle style2 = new ExcelStyle()
                .fontName("宋体")
                .fontHeight(13)
                .fgColor(IndexedColors.ORANGE)
                .borderColor(IndexedColors.RED, BorderStyle.DOUBLE);
        sheet2.setColumnStyles(Arrays.asList(style1, style2));

        SimpleSheet sheet3 = new SimpleSheet();
        sheet3.setDefaultStyle(defaultStyle);
        sheet3.setHeaderStyle(defaultHeaderStyle);
        sheet3.setName("small_style2");
        sheet3.setHeader(Arrays.asList("id", "name", "age"));
        sheet3.setBody(buildBody());
        sheet3.setColumnStyles(Arrays.asList(style1, null, style2));

        writeXlsx(getClass().getSimpleName() + "_testSmall", sheet1, sheet2, sheet3);
    }

    private List<List<Object>> buildBody() {
        return Arrays.asList(
                Arrays.asList(1, "Tom", 18),
                Arrays.asList(2, "Jerry", 19),
                Arrays.asList(3, "Mike", 50)
        );
    }
}
