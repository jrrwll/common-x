package org.dreamcat.common.excel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Drawing;
import org.dreamcat.common.util.ArrayUtil;

/**
 * @author Jerry Will
 * @version 2026-01-04
 */
@Data
@Slf4j
public class ExcelAnchor {

    private int col1;
    private int row1;
    private int col2;
    private int row2;

    private int dx1;
    private int dy1;
    private int dx2;
    private int dy2;

    private int anchorType;

    public static ExcelAnchor from(ClientAnchor anchor) {
        ExcelAnchor excelAnchor = new ExcelAnchor();

        excelAnchor.col1 = anchor.getCol1();
        excelAnchor.row1 = anchor.getRow1();
        excelAnchor.col2 = anchor.getCol2();
        excelAnchor.row2 = anchor.getRow2();

        excelAnchor.dx1 = anchor.getDx1();
        excelAnchor.dy1 = anchor.getDy1();
        excelAnchor.dx2 = anchor.getDx2();
        excelAnchor.dy2 = anchor.getDy2();

        excelAnchor.anchorType = anchor.getAnchorType().value;
        return excelAnchor;
    }

    public ClientAnchor createClientAnchor(Drawing<?> drawing) {
        ClientAnchor clientAnchor = drawing.createAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2);
        AnchorType type = (AnchorType) ArrayUtil.getOrNull(AnchorType.values(), anchorType);
        if (type != null) {
            clientAnchor.setAnchorType(type);
        } else {
            log.warn("poi anchor type {} is not supported", anchorType);
        }
        return clientAnchor;
    }
}
