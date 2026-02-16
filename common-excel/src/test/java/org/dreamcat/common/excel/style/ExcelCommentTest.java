package org.dreamcat.common.excel.style;

import static org.dreamcat.common.util.RandomUtil.uuid32;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.ExcelCell;
import org.dreamcat.common.excel.ExcelSheet;
import org.dreamcat.common.excel.build.ExcelBuilder;
import org.junit.jupiter.api.Test;

/**
 * @author Jerry Will
 * @version 2026-01-21
 */
public class ExcelCommentTest extends BaseTest {

    @Test
    void testComment() {
        ExcelSheet sheet = new ExcelSheet("Sheet One");
        for (int i = 0; i < 12; i++) {
            ExcelCell cell = new ExcelCell(uuid32(), 0, i)
                    .setStyle(ExcelBuilder.easyExcelStyle())
                    .setHyperLink(new ExcelHyperLink(
                            HyperlinkType.URL, "http://marry.me", "link"))
                    .setComment(new ExcelComment()
                            .setAuthor("tuke")
                            .setString(ExcelRichString.from("awesome"))
                            .setClientAnchor(new ExcelClientAnchor(AnchorType.MOVE_AND_RESIZE,
                                    0, 0, 0, 0,
                                    i, 0, i, 0)));
            sheet.addCell(cell);
        }
        for (int i = 0; i < 6; i++) {
            ExcelCell cell = new ExcelCell(uuid32(), 1, i)
                    .setComment(new ExcelComment()
                            .setAuthor("tuke")
                            .setString(ExcelRichString.from("dreamcat")));
            sheet.addCell(cell);
        }
        writeXlsx("testComment", sheet);
    }
}
