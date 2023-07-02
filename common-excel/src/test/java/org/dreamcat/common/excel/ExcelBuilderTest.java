package org.dreamcat.common.excel;

import static org.dreamcat.common.excel.ExcelBuilder.font;
import static org.dreamcat.common.excel.ExcelBuilder.sheet;
import static org.dreamcat.common.excel.ExcelBuilder.style;
import static org.dreamcat.common.excel.ExcelBuilder.term;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.excel.ExcelBuilder.SheetTerm;
import org.dreamcat.common.excel.callback.FitWidthWriteCallback;
import org.dreamcat.common.excel.callback.LoggingWriteCallback;
import org.dreamcat.common.excel.style.ExcelClientAnchor;
import org.dreamcat.common.excel.style.ExcelComment;
import org.dreamcat.common.excel.style.ExcelFont;
import org.dreamcat.common.excel.style.ExcelHyperLink;
import org.dreamcat.common.excel.style.ExcelRichString;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.junit.jupiter.api.Test;


/**
 * Create by tuke on 2020/7/22
 */
public class ExcelBuilderTest extends BaseTest {

    public static SheetTerm headerSheet() {
        return sheet("Sheet Cell")
                // col1
                .richCell(term("A1:A2"), 0, 0, 2, 1)
                .style(style().verticalAlignment(VerticalAlignment.CENTER)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .fgColor(IndexedColors.ROSE).finish())
                .font(font().height(24).color(IndexedColors.RED1.getIndex()).finish())
                .finishCell()
                // col2
                .richCell("B1", 0, 1, 2, 1)
                .style(style().fgColor(IndexedColors.VIOLET).finish())
                .font(font().height(32).finish())
                .finishCell()
                // col3
                .richCell(term("C1:D1"), 0, 2, 1, 2)
                .style(style().verticalAlignment(VerticalAlignment.CENTER)
                        .fgColor(IndexedColors.LEMON_CHIFFON).finish())
                .font(font().height(16).finish())
                .finishCell()
                .richCell("C2", 1, 2)
                .style(style().fgColor(IndexedColors.GREY_50_PERCENT).finish())
                .font(font().height(14).finish())
                .finishCell()
                .richCell("D2", 1, 3)
                .style(style().fgColor(IndexedColors.LAVENDER).finish())
                .finishCell()
                // col4
                .richCell(term("E1:F1"), 0, 4, 1, 2)
                .style(style().verticalAlignment(VerticalAlignment.CENTER)
                        .fgColor(IndexedColors.AQUA).finish())
                .font(font().height(12).finish())
                .finishCell()
                .richCell("E2", 1, 4)
                .style(style().fgColor(IndexedColors.OLIVE_GREEN).finish())
                .font(font().height(10).finish())
                .finishCell()
                .richCell("F2", 1, 5)
                .style(style().fgColor(IndexedColors.PALE_BLUE).finish())
                .font(font().height(8).finish())
                .finishCell();
    }

    @Test
    void testSmall() throws Exception {
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();
        ExcelSheet sheet = new ExcelSheet("Sheet One");

        IndexedColors[] colors = new IndexedColors[]{
                IndexedColors.BLUE_GREY,
                IndexedColors.BRIGHT_GREEN,
                IndexedColors.DARK_BLUE,
                IndexedColors.DARK_YELLOW,
        };
        for (int i = 0; i < 12; i++) {
            ExcelFont font = new ExcelFont();
            font.setHeight((short) 32);
            font.setBold(i % 2 == 0);
            font.setItalic(i % 3 == 0);
            font.setColor(colors[randi(128) % 4].getIndex());

            ExcelStyle style = new ExcelStyle();
            style.setFontIndex(i);
            style.setFgColor(IndexedColors.ROSE.getIndex());
            style.setHorizontalAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            ExcelCell cell = new ExcelCell(
                    term(rand() * (1 << 10)),
                    i, 0, 1, 1)
                    .setStyle(style)
                    .setFont(font)
                    .setHyperLink(new ExcelHyperLink(
                            HyperlinkType.URL, "http://marry.me", "link"))
                    .setComment(new ExcelComment(
                            true, "tuke",
                            ExcelRichString.from("awesom"),
                            new ExcelClientAnchor(AnchorType.MOVE_AND_RESIZE,
                                    0, 0, 0, 0,
                                    0, i, 0, i)));
            sheet.getCells().add(cell);
        }
        sheet.addWriteCallback(new FitWidthWriteCallback());

        book.addSheet(sheet);
        book.writeTo(basePath + "/book.xlsx");
    }

    @Test
    void test() {
        SheetTerm sheetTerm = headerSheet();
        ExcelSheet headerSheet = sheetTerm.finish();
        headerSheet.addWriteCallback(new LoggingWriteCallback());
        printSheetVerbose(headerSheet);
        writeXlsx("test", headerSheet);
    }
}
