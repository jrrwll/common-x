package org.dreamcat.common.excel.content;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.dreamcat.common.excel.style.ExcelRichString;
import org.dreamcat.common.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Create by tuke on 2020/7/21
 */
public interface IExcelContent {

    static IExcelContent from(Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            return new ExcelNumericContent(number.doubleValue());
        } else if (value instanceof Boolean) {
            return new ExcelBooleanContent((Boolean) value);
        } else if (value instanceof Date) {
            return new ExcelDateContent((Date) value);
        } else if (value instanceof Calendar) {
            return new ExcelDateContent(((Calendar) value).getTime());
        } else if (value instanceof LocalDate) {
            LocalDateTime localDateTime = ((LocalDate) value).atStartOfDay();
            return new ExcelDateContent(DateUtil.toDate(localDateTime));
        } else if (value instanceof LocalDateTime) {
            return new ExcelDateContent(DateUtil.toDate((LocalDateTime) value));
        } else if (value instanceof IExcelContent) {
            return (IExcelContent) value;
        } else {
            return ExcelStringContent.from(value == null ? "" : value.toString());
        }
    }

    static IExcelContent fromCell(Cell cell) {
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                RichTextString richTextString = cell.getRichStringCellValue();
                return new ExcelStringContent(ExcelRichString.from(richTextString));
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return new ExcelDateContent(cell.getDateCellValue());
                }
                return new ExcelNumericContent(cell.getNumericCellValue());
            case BOOLEAN:
                return new ExcelBooleanContent(cell.getBooleanCellValue());
            case FORMULA:
                return new ExcelFormulaContent(cell.getCellFormula());
            default:
                return new ExcelStringContent();
        }
    }

    static Object valueOf(Cell cell) {
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                // string
                return cell.getStringCellValue();
            case NUMERIC:
                // double
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                double value = cell.getNumericCellValue();
                long round = Math.round(value);
                if (value == (double) round) {
                    return round;
                }
                return value;
            case BOOLEAN:
                // boolean
                return cell.getBooleanCellValue();
            case FORMULA:
                // string
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    void fill(Cell cell);

}
