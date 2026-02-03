package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.excel.style.ExcelStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry Will
 * @version 2026-02-01
 */
@Getter
@Setter
class SheetStyle {

    private ExcelStyle globalStyle;
    private Map<Integer, ExcelStyle> columnStyles = new HashMap<>(); // columnIndex -> style

    ExcelStyle getStyle(int columnIndex) {
        return columnStyles.getOrDefault(columnIndex, globalStyle);
    }
}
