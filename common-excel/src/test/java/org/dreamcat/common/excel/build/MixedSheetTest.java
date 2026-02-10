package org.dreamcat.common.excel.build;

import static org.dreamcat.common.util.RandomUtil.choose72;
import static org.dreamcat.common.util.RandomUtil.rand;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.ExcelSheet;
import org.dreamcat.common.excel.IExcelSheet;
import org.dreamcat.common.excel.build.CompositeSheet2Test.Pojo;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by tuke on 2020/7/22
 */
public class MixedSheetTest extends BaseTest {

    @Test
    void testSmall() {
        CompositeSheet sheet1 = new CompositeSheet();
        sheet1.setName("Sheet One");
        List<IExcelSheet> sheets1 = new ArrayList<>();
        sheet1.setSheets(sheets1);
        for (int i = 0; i < 5; i++) {
            TableSheet tableSheet = new TableSheet();
            tableSheet.setDefaultStyle(TableSheetTest.defaultStyle);
            tableSheet.setHeader(TableSheetTest.buildHeader());
            tableSheet.setBody(TableSheetTest.buildBody());
            sheets1.add(tableSheet);
        }

        CompositeSheet sheet2 = new CompositeSheet();
        sheet2.setName("Sheet2");
        List<IExcelSheet> sheets2 = new ArrayList<>();
        sheet2.setSheets(sheets2);
        for (int i = 0; i < 4; i++) {
            TableSheet tableSheet = new TableSheet();
            tableSheet.setDefaultStyle(TableSheetTest.defaultStyle);
            tableSheet.setHeaderStyle(TableSheetTest.defaultHeaderStyle);
            tableSheet.setHeader(TableSheetTest.buildHeader());
            tableSheet.setBody(TableSheetTest.buildBody());
            sheets2.add(tableSheet);
        }
        writeXlsx("testSmall", sheet1, sheet2);
    }

    @Test
    void testSmallParse() {
        readXlsx("testSmall", sheet -> {
            System.out.println("sheetName: " + sheet.getName());
            System.out.println(sheet);
        });
    }

    @Test
    void test() {
        // header
        ExcelSheet sheet1 = createHeaderSheet();

        BeanSheet<Pojo> sheet2 = new BeanSheet<>(Pojo.class);
        sheet2.setBody(Arrays.asList(
                new Pojo(1, rand(), null, choose72(6)),
                new Pojo(2, rand() * (1 << 16), null, choose72(2))
        ));

        ExcelSheet sheet3 = new ExcelSheet();
        sheet3.addCell(new ExcelCell("A6:B6", 0, 0, 1, 2)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.PINK)));
        sheet3.addCell(new ExcelCell("A7", 1, 0, 1, 1)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.LIGHT_YELLOW)));
        sheet3.addCell(new ExcelCell("B7:C7", 1, 1, 1, 2)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.ORANGE)));
        sheet3.addCell(new ExcelCell("C6", 0, 2, 1, 1)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.YELLOW1)));
        sheet3.addCell(new ExcelCell("D6:D7", 0, 3, 2, 1)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.TURQUOISE)));

        BeanSheet<Pojo> sheet4 = new BeanSheet<>(Pojo.class);
        sheet2.setBody(Arrays.asList(
                new Pojo(3, rand(), null, choose72(6)),
                new Pojo(4, rand() * (1 << 16), null, choose72(2))
        ));

        CompositeSheet sheet = new CompositeSheet();
        sheet.setName("Sheet One");
        sheet.setSheets(Arrays.asList(sheet1, sheet2, sheet3, sheet4));
        writeXlsxWithBigGrid("test", sheet);
    }

    static ExcelSheet createHeaderSheet() {
        ExcelSheet headerSheet = new ExcelSheet();
        headerSheet.addCell(new ExcelCell("A1:C2", 0, 0, 2, 3)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.GREY_50_PERCENT)));
        headerSheet.addCell(new ExcelCell("D1:D3", 0, 3, 3, 1)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.RED)));
        headerSheet.addCell(new ExcelCell("B3:C3", 2, 1, 1, 2)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.GREEN)));
        headerSheet.addCell(new ExcelCell("A3", 2, 0)
                .setStyle(new ExcelStyle().fillColor(IndexedColors.LIGHT_BLUE)));
        return headerSheet;
    }
}
