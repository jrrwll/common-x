package org.dreamcat.common.excel.style;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.dreamcat.common.excel.ExcelInternalUtil;
import org.dreamcat.common.excel.ExcelWorkbook;
import org.dreamcat.common.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by tuke on 2021/2/14
 * <p>
 * Note that {@link HSSFRichTextString} is unsupported
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRichString {

    private String string;
    private List<FormattingRun> formattingRuns;

    public boolean hasFormatting() {
        return ObjectUtil.isNotEmpty(formattingRuns);
    }

    public static ExcelRichString from(String string) {
        return new ExcelRichString(string, null);
    }

    public static ExcelRichString from(
            RichTextString richTextString, ExcelWorkbook<?> excelWorkbook) {
        String string = richTextString.getString();
        int numFormattingRuns = richTextString.numFormattingRuns();
        if (numFormattingRuns == 0 || richTextString instanceof HSSFRichTextString) {
            return from(string);
        }
        XSSFRichTextString xssfRichTextString = (XSSFRichTextString) richTextString;

        List<FormattingRun> formattingRuns = new ArrayList<>(numFormattingRuns);
        for (int i = 0; i < numFormattingRuns; i++) {
            int index = xssfRichTextString.getIndexOfFormattingRun(i);
            int length = xssfRichTextString.getLengthOfFormattingRun(i);
            String substring = string.substring(index, index + length);

            FormattingRun formattingRun = new FormattingRun();
            formattingRun.setString(substring);

            XSSFFont xssfFont = xssfRichTextString.getFontOfFormattingRun(i);
            if (xssfFont != null) {
                ExcelFont font = ExcelInternalUtil.getOrCreateFont(excelWorkbook, xssfFont);
                formattingRun.setFont(font);
            }
            formattingRuns.add(formattingRun);
        }
        return new ExcelRichString(string, formattingRuns);
    }

    public RichTextString toRichTextString(Cell cell, ExcelWorkbook<?> excelWorkbook) {
        RichTextString richTextString;
        if (cell instanceof XSSFCell) {
            richTextString = new XSSFRichTextString(string);
        } else {
            richTextString = new HSSFRichTextString(string);
        }
        if (ObjectUtil.isEmpty(formattingRuns)) {
            return richTextString;
        }

        int startIndex = 0;
        int endIndex = 0;
        for (FormattingRun formattingRun : formattingRuns) {
            String substring = formattingRun.getString();
            endIndex += substring.length();

            ExcelFont excelFont = formattingRun.getFont();
            if (excelFont != null) {
                Font font = ExcelInternalUtil.getOrCreateFont(
                        cell.getSheet().getWorkbook(), excelWorkbook, excelFont);
                richTextString.applyFont(startIndex, endIndex, font);
            }
            startIndex = endIndex;
        }
        return richTextString;
    }

}
