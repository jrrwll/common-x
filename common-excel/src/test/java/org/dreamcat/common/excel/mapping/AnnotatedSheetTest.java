package org.dreamcat.common.excel.mapping;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.ExcelSheet;
import org.dreamcat.common.excel.callback.AutoWidthWriteCallback;
import org.dreamcat.common.excel.callback.FitWidthWriteCallback;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.junit.jupiter.api.Test;

/**
 * Create by tuke on 2020/7/26
 */
class AnnotatedSheetTest extends BaseTest {

    @Test
    void testSmall() {
        // body + head + body + head + body
        AnnotatedSheet sheet1 = new AnnotatedSheet("Sheet One");
        sheet1.add(XlsMetaTest.newPojo());
        sheet1.add(headerSheet());
        sheet1.add(XlsMetaTest.newPojo());
        sheet1.add(headerSheet());
        sheet1.add(XlsMetaTest.newPojo());

        // head + body + head + body + head
        AnnotatedSheet sheet2 = new AnnotatedSheet("Sheet Two");
        sheet2.setAnnotationStyle(true);
        sheet2.addWriteCallback(new FitWidthWriteCallback());
        sheet2.add(headerSheet());
        sheet2.add(XlsMetaTest.newPojo());
        sheet2.add(headerSheet());
        sheet2.add(XlsMetaTest.newPojo());
        sheet2.add(headerSheet());
        printSheetVerbose(sheet2);

        writeXlsx("testSmall", sheet1, sheet2);
    }

    @Test
    void test() {
        // body + head + body + head + body
        AnnotatedSheet sheet1 = new AnnotatedSheet("Sheet One");
        sheet1.setAnnotationStyle(true);
        // sheet1.setWriteCallback(new FitWidthWriteCallback());
        sheet1.addWriteCallback(new AutoWidthWriteCallback());

        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet1.add(headerSheet());
        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet1.add(headerSheet());
        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());

        // head + body + head + body + head
        AnnotatedSheet sheet2 = new AnnotatedSheet("Sheet Two");
        for (int i = 0; i < 6; i++) sheet2.add(headerSheet());
        for (int i = 0; i < 6; i++) sheet2.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet2.add(headerSheet());
        for (int i = 0; i < 6; i++) sheet2.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet2.add(headerSheet());

        writeXlsx("test", sheet1, sheet2);
    }

    private ExcelSheet headerSheet() {
        ExcelSheet headerSheet = new ExcelSheet(null);
        // col1
        headerSheet.addCell(new ExcelCell("A1:A2", 0, 0, 2, 1)
                .setStyle(new ExcelStyle().setVerticalAlignment(VerticalAlignment.CENTER)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .fgColor(IndexedColors.ROSE)
                        .setFont(new ExcelFont().height(24).color(IndexedColors.RED1))));
        // col2
        headerSheet.addCell(new ExcelCell("B1", 0, 1, 2, 1)
                .setStyle(new ExcelStyle()
                        .fgColor(IndexedColors.VIOLET)
                        .fontHeight(32)));

        // col3
        headerSheet.addCell(new ExcelCell("C1:D1", 0, 2, 1, 2)
                .setStyle(new ExcelStyle()
                        .setVerticalAlignment(VerticalAlignment.CENTER)
                        .fgColor(IndexedColors.LEMON_CHIFFON)
                        .fontHeight(16)));
        headerSheet.addCell(new ExcelCell("C2", 1, 2)
                .setStyle(new ExcelStyle()
                        .fgColor(IndexedColors.GREY_50_PERCENT)
                        .fontHeight(14)));
        headerSheet.addCell(new ExcelCell("D2", 1, 3)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.LAVENDER)));

        // col4
        headerSheet.addCell(new ExcelCell("E1:F1", 0, 4, 1, 2)
                .setStyle(new ExcelStyle()
                        .setVerticalAlignment(VerticalAlignment.CENTER)
                        .fgColor(IndexedColors.AQUA)
                        .fontHeight(12)));
        headerSheet.addCell(new ExcelCell("E2", 1, 4)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.OLIVE_GREEN).fontHeight(10)));
        headerSheet.addCell(new ExcelCell("F2", 1, 5)
                .setStyle(new ExcelStyle().fgColor(IndexedColors.PALE_BLUE).fontHeight(8)));
        return headerSheet;
    }

}
