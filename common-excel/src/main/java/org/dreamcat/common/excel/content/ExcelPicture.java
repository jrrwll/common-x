package org.dreamcat.common.excel.content;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.dreamcat.common.excel.ExcelAnchor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry Will
 * @version 2026-01-04
 */
@Data
public class ExcelPicture {

    private ExcelPictureData pictureData;
    private ExcelAnchor anchor;

    private double scaleX;
    private double scaleY;

    public void setResize(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public ExcelPicture(ExcelPictureData pictureData, Picture picture) {
        this.pictureData = pictureData;

        ClientAnchor clientAnchor = picture.getClientAnchor();
        this.anchor = ExcelAnchor.from(clientAnchor);
    }
}
