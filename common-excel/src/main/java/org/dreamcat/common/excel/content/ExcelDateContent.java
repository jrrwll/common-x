package org.dreamcat.common.excel.content;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.dreamcat.common.util.DateUtil;

import java.util.Date;

/**
 * @author Jerry Will
 * @version 2023-06-30
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDateContent implements IExcelContent {

    private Date value;

    @Override
    public void fill(Cell cell) {
        cell.setCellValue(value);
    }

    @Override
    public String toString() {
        return DateUtil.format(value);
    }
}
