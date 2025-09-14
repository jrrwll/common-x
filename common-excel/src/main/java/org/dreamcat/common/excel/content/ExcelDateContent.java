package org.dreamcat.common.excel.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.dreamcat.common.util.DateUtil;

import java.util.Date;

/**
 * @author Jerry Will
 * @version 2023-06-30
 */
@Data
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
