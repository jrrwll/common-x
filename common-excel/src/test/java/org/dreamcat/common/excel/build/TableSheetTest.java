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
class TableSheetTest extends BaseTest {

    static ExcelStyle defaultStyle = new ExcelStyle()
            .fontName("Times New Roman")
            .fontHeight(12);
    static ExcelStyle defaultHeaderStyle = new ExcelStyle()
            .setHorizontalAlignment(HorizontalAlignment.CENTER)
            .setVerticalAlignment(VerticalAlignment.CENTER)
            .setWrapText(true)
            .fontName("宋体")
            .fontHeight(14)
            .fontBold()
            .setLocked(true);

    @Test
    void testSmall() {
        TableSheet sheet1 = new TableSheet();
        sheet1.setDefaultStyle(defaultStyle);
        sheet1.setName("small");
        sheet1.setHeader(buildHeader());
        sheet1.setBody(buildBody());

        TableSheet sheet2 = new TableSheet();
        sheet2.setHeaderStyle(defaultHeaderStyle);
        sheet2.setName("small_style");
        sheet2.setHeader(buildHeader());
        sheet2.setBody(buildBody());

        ExcelStyle style1 = new ExcelStyle()
                .fontName("Arial")
                .fontHeight(10)
                .fontBold()
                .fillColor(IndexedColors.SKY_BLUE)
                .borderColor(IndexedColors.BLACK);
        ExcelStyle style2 = new ExcelStyle()
                .fontName("宋体")
                .fontHeight(13)
                .fillColor(IndexedColors.ORANGE)
                .borderColor(IndexedColors.RED, BorderStyle.DOUBLE);
        sheet2.setColumnStyles(Arrays.asList(style1, style2));

        TableSheet sheet3 = new TableSheet();
        sheet3.setDefaultStyle(defaultStyle);
        sheet3.setHeaderStyle(defaultHeaderStyle);
        sheet3.setName("small_style2");
        sheet3.setHeader(Arrays.asList("id", "name", "age"));
        sheet3.setBody(buildBody());
        sheet3.setColumnStyles(Arrays.asList(style1, null, style2));

        writeXlsx("testSmall", sheet1, sheet2, sheet3);
    }

    static List<String> buildHeader() {
        return Arrays.asList("id", "name", "age");
    }

    static List<List<Object>> buildBody() {
        return Arrays.asList(
                Arrays.asList(1, "Tom", 18),
                Arrays.asList(2, "Jerry", 19),
                Arrays.asList(3, "Mike", 50)
        );
    }
}
