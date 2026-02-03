package org.dreamcat.common.excel.build;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.excel.IExcelCell;
import org.dreamcat.common.excel.content.IExcelContent;
import org.dreamcat.common.excel.style.ExcelComment;
import org.dreamcat.common.excel.style.ExcelHyperLink;
import org.dreamcat.common.excel.style.ExcelStyle;

/**
 * @author Jerry Will
 * @version 2026-02-02
 */
@Getter
@Setter
class ExcelCellWithOffset implements IExcelCell {

    // as row index offset since row based structure
    int offset;
    IExcelCell cell;

    @Override
    public int getRowIndex() {
        return cell.getRowIndex() + offset;
    }

    @Override
    public int getColumnIndex() {
        return cell.getColumnIndex();
    }

    @Override
    public int getRowSpan() {
        return cell.getRowSpan();
    }

    @Override
    public int getColumnSpan() {
        return cell.getColumnSpan();
    }

    @Override
    public IExcelContent getContent() {
        return cell.getContent();
    }

    @Override
    public ExcelStyle getStyle() {
        return cell.getStyle();
    }

    @Override
    public ExcelHyperLink getHyperLink() {
        return cell.getHyperLink();
    }

    @Override
    public ExcelComment getComment() {
        return cell.getComment();
    }

    @Override
    public boolean hasMergedRegion() {
        return cell.hasMergedRegion();
    }
}
