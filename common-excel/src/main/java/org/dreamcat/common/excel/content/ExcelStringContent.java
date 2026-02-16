package org.dreamcat.common.excel.content;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Objects;

/**
 * Create by tuke on 2020/7/21
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ExcelStringContent implements IExcelContent {

    private String value;

    @Override
    public void fill(Cell cell) {
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }
}
