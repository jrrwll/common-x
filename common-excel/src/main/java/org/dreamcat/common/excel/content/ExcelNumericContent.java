package org.dreamcat.common.excel.content;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Create by tuke on 2020/7/21
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ExcelNumericContent implements IExcelContent {

    private double value;

    @Override
    public void fill(Cell cell) {
        cell.setCellValue(value);
    }

    @Override
    public String toString() {
        long round = Math.round(value);
        if (value == (double) round) {
            return Long.toString(round);
        }
        return Double.toString(value);
    }
}
