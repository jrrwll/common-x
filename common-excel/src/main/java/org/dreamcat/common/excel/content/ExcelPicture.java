package org.dreamcat.common.excel.content;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry Will
 * @version 2026-01-04
 */
@Data
public class ExcelPicture {

    private int col1;
    private int row1;
    private int col2;
    private int row2;

    private int dx1;
    private int dy1;
    private int dx2;
    private int dy2;

    private int anchorType;

    private double scaleX;
    private double scaleY;

    private ExcelPictureData pictureData;

    public void setResize(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public static List<ExcelPicture> from(Drawing<?> drawing) {
        List<ExcelPicture> pictures = new ArrayList<>();
        if (drawing instanceof XSSFDrawing) {
            XSSFDrawing xssfDrawing = (XSSFDrawing) drawing;
            List<XSSFShape> shapes = xssfDrawing.getShapes();
            for (XSSFShape shape : shapes) {
                if (shape instanceof XSSFPicture) {
                    pictures.add(new ExcelPicture((XSSFPicture)shape));
                }
            }
        } else if (drawing instanceof SXSSFDrawing) {
            SXSSFDrawing sxssfDrawing = (SXSSFDrawing) drawing;
            for (XSSFShape shape : sxssfDrawing) {
                if (shape instanceof XSSFPicture) {
                    pictures.add(new ExcelPicture((XSSFPicture)shape));
                }
            }
        } else if (drawing instanceof HSSFPatriarch){
            HSSFPatriarch hssfPatriarch = (HSSFPatriarch) drawing;
            for (HSSFShape shape : hssfPatriarch) {
                if (shape instanceof HSSFPicture) {
                    pictures.add(new ExcelPicture((HSSFPicture)shape));
                }
            }
        }
        return pictures;
    }

    private ExcelPicture(XSSFPicture picture) {
        picture.getShapeName();

        ClientAnchor anchor = picture.getClientAnchor();
        this.col1 = anchor.getCol1();
        this.row1 = anchor.getRow1();
        this.col2 = anchor.getCol2();
        this.row2 = anchor.getRow2();

        this.dx1 = anchor.getDx1();
        this.dy1 = anchor.getDy1();
        this.dx2 = anchor.getDx2();
        this.dy2 = anchor.getDy2();
        this.anchorType = anchor.getAnchorType().value;

        this.pictureData = ExcelPictureData.from(picture.getPictureData());
    }

    private ExcelPicture(HSSFPicture picture) {
        picture.getShapeId();
        picture.getPictureData();
        ClientAnchor anchor = picture.getClientAnchor();
    }

    public void fill(Sheet sheet, int pictureIndex) {
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2);

        Picture picture = drawing.createPicture(anchor, pictureIndex);
        if (scaleX > 0 || scaleY > 0) {
            picture.resize(scaleX, scaleY);
        }
    }
}
