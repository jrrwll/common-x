package org.dreamcat.common.excel.style;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.dreamcat.common.excel.ExcelWorkbook;

/**
 * Create by tuke on 2021/2/14
 */
@Getter
@Setter
@Accessors(chain = true)
public class ExcelComment {

    private boolean visible;
    private String author;
    private ExcelRichString string;
    private ExcelClientAnchor clientAnchor;

    public static ExcelComment from(Comment comment, ExcelWorkbook<?> excelWorkbook) {
        ExcelComment excelComment = new ExcelComment();
        excelComment.setVisible(comment.isVisible());
        excelComment.setAuthor(comment.getAuthor());
        // string
        RichTextString string = comment.getString();
        excelComment.setString(ExcelRichString.from(string, excelWorkbook));
        // anchor
        ClientAnchor clientAnchor = comment.getClientAnchor();
        excelComment.setClientAnchor(ExcelClientAnchor.from(clientAnchor));
        return excelComment;
    }

    public void fill(Cell cell, ExcelWorkbook<?> excelWorkbook) {
        Drawing<?> drawing = cell.getSheet().createDrawingPatriarch();
        ClientAnchor anchor;
        if (clientAnchor != null) {
            anchor = clientAnchor.createAnchor(drawing);
        } else {
            anchor = ExcelClientAnchor.createAnchor(drawing, cell);
        }

        Comment comment = drawing.createCellComment(anchor);
        comment.setVisible(visible);
        comment.setAuthor(author);

        comment.setString(string.toRichTextString(cell, excelWorkbook));
        cell.setCellComment(comment);
    }
}
