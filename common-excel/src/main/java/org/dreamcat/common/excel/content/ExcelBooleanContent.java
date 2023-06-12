package org.dreamcat.common.excel.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelBooleanContent implements IExcelContent {

    private boolean value;

    @Override
    public void fill(Cell cell) {
        cell.setCellValue(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
