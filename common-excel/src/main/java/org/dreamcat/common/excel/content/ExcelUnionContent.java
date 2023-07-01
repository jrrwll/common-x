package org.dreamcat.common.excel.content;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.dreamcat.common.excel.style.ExcelRichString;
import org.dreamcat.common.util.DateUtil;

/**
 * Create by tuke on 2020/7/22
 */
public class ExcelUnionContent implements IExcelContent {

    private final ExcelStringContent stringContent = new ExcelStringContent();
    private final ExcelNumericContent numericContent = new ExcelNumericContent();
    private final ExcelBooleanContent booleanContent = new ExcelBooleanContent();
    private final ExcelDateContent dateContent = new ExcelDateContent();
    // transient
    private IExcelContent rawContent = ExcelBlankContent.INSTANCE;

    public ExcelUnionContent(){
    }

    public ExcelUnionContent(String value) {
        setStringContent(value);
    }

    public ExcelUnionContent(double value) {
        setNumericContent(value);
    }

    public ExcelUnionContent(boolean value) {
        setBooleanContent(value);
    }

    public ExcelUnionContent(Date value) {
        setDateContent(value);
    }

    public ExcelUnionContent(Calendar value) {
        setDateContent(value);
    }

    public ExcelUnionContent(LocalDate value) {
        setDateContent(value);
    }

    public ExcelUnionContent(LocalDateTime value) {
        setDateContent(value);
    }

    public ExcelUnionContent(Object value) {
        setContent(value);
    }

    public void setStringContent(String value) {
        this.stringContent.setValue(ExcelRichString.from(value));
        this.rawContent = this.stringContent;
    }

    public void setNumericContent(double value) {
        this.numericContent.setValue(value);
        this.rawContent = this.numericContent;
    }

    public void setBooleanContent(boolean value) {
        this.booleanContent.setValue(value);
        this.rawContent = this.booleanContent;
    }

    public void setDateContent(Date value) {
        this.dateContent.setValue(value);
        this.rawContent = this.dateContent;
    }

    public void setDateContent(Calendar value) {
        setDateContent(value.getTime());
    }

    public void setDateContent(LocalDate value) {
        setDateContent(DateUtil.asDate(value.atStartOfDay()));
    }

    public void setDateContent(LocalDateTime value) {
        setDateContent(DateUtil.asDate(value));
    }

    public void setRawContent(IExcelContent rawContent) {
        if (rawContent == null) {
            rawContent = ExcelBlankContent.INSTANCE;
        }
        this.rawContent = rawContent;
    }

    public void setContent(Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            setNumericContent(number.doubleValue());
        } else if (value instanceof Boolean) {
            Boolean bool = (Boolean) value;
            setBooleanContent(bool);
        } else if (value instanceof IExcelContent) {
            setRawContent((IExcelContent) value);
        } else if (value instanceof Date) {
            setDateContent((Date) value);
        } else if (value instanceof Calendar) {
            setDateContent((Calendar) value);
        } else if (value instanceof LocalDate) {
            setDateContent((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            setDateContent((LocalDateTime) value);
        } else if (value instanceof String) {
            setStringContent((String) value);
        } else if (value != null) {
            setStringContent(value.toString());
        } else {
            this.rawContent = ExcelBlankContent.INSTANCE;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(rawContent);
    }

    @Override
    public void fill(Cell cell) {
        rawContent.fill(cell);
    }
}
