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
@SuppressWarnings("rawtypes")
public class ExcelUnionContent implements IExcelContent {

    private final ExcelStringContent stringContent;
    private final ExcelNumericContent numericContent;
    private final ExcelBooleanContent booleanContent;
    private final ExcelDateContent dateContent;
    // transient
    private IExcelContent rawContent;
    private Class type;

    protected ExcelUnionContent() {
        this.stringContent = new ExcelStringContent();
        this.numericContent = new ExcelNumericContent();
        this.booleanContent = new ExcelBooleanContent();
        this.dateContent = new ExcelDateContent();
    }

    public ExcelUnionContent(String value) {
        this();
        setStringContent(value);
    }

    public ExcelUnionContent(double value) {
        this();
        setNumericContent(value);
    }

    public ExcelUnionContent(boolean value) {
        this();
        setBooleanContent(value);
    }

    public ExcelUnionContent(Object value) {
        this();
        setContent(value);
    }

    public void setStringContent(String value) {
        this.stringContent.setValue(ExcelRichString.from(value));
        this.type = ExcelStringContent.class;
    }

    public void setNumericContent(double value) {
        this.numericContent.setValue(value);
        this.type = ExcelNumericContent.class;
    }

    public void setBooleanContent(boolean value) {
        this.booleanContent.setValue(value);
        this.type = ExcelBooleanContent.class;
    }

    public void setDateContent(Date value) {
        this.dateContent.setValue(value);
        this.type = ExcelDateContent.class;
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
        this.rawContent = rawContent;
        this.type = IExcelContent.class;
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
        } else if (value != null){
            setStringContent(value.toString());
        }
    }

    @Override
    public String toString() {
        if (type == null) {
            return String.valueOf(rawContent);
        } else if (type.equals(ExcelStringContent.class)) {
            return String.valueOf(stringContent);
        } else if (type.equals(ExcelNumericContent.class)) {
            return String.valueOf(numericContent);
        } else if (type.equals(ExcelBooleanContent.class)) {
            return String.valueOf(booleanContent);
        } else if (type.equals(ExcelDateContent.class)) {
            return String.valueOf(dateContent);
        } else {
            return String.valueOf(rawContent);
        }
    }

    @Override
    public void fill(Cell cell) {
        if (type == null) {
            cell.setBlank();
        } else if (type.equals(ExcelStringContent.class)) {
            stringContent.fill(cell);
        } else if (type.equals(ExcelNumericContent.class)) {
            numericContent.fill(cell);
        } else if (type.equals(ExcelBooleanContent.class)) {
            booleanContent.fill(cell);
        } else if (rawContent != null) {
            rawContent.fill(cell);
        }
    }
}
