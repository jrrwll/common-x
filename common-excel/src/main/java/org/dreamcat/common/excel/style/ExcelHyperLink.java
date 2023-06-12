package org.dreamcat.common.excel.style;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.excel.IExcelCell;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelHyperLink {

    private HyperlinkType type;
    private String address;
    private String label;

    public static ExcelHyperLink from(Hyperlink hyperlink) {
        ExcelHyperLink link = new ExcelHyperLink();
        link.setType(hyperlink.getType());
        link.setAddress(hyperlink.getAddress());
        link.setLabel(hyperlink.getLabel());
        return link;
    }

    public void fill(Cell cell, Workbook workbook, IExcelCell excelCell) {
        CreationHelper creationHelper = workbook.getCreationHelper();
        Hyperlink link = creationHelper.createHyperlink(type);
        link.setAddress(address);
        if (label != null) link.setLabel(label);

        if (excelCell.hasMergedRegion()) {
            link.setFirstRow(excelCell.getRowIndex());
            link.setLastRow(excelCell.getRowIndex() + excelCell.getRowSpan() - 1);
            link.setFirstColumn(excelCell.getColumnIndex());
            link.setLastColumn(excelCell.getColumnIndex() + excelCell.getColumnSpan() - 1);
        }
        cell.setHyperlink(link);
    }

}
