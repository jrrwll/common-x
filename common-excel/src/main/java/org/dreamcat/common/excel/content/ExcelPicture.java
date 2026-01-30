package org.dreamcat.common.excel.content;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.dreamcat.common.excel.ExcelAnchor;

/**
 * @author Jerry Will
 * @version 2026-01-04
 */
@Getter
@Setter
public class ExcelPicture {

    private ExcelPictureData pictureData;
    private ExcelAnchor anchor;

    private double scaleX;
    private double scaleY;

    public void setResize(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public static ExcelPicture from(ExcelPictureData pictureData, Picture picture) {
        ExcelPicture excelPicture = new ExcelPicture();
        excelPicture.setPictureData(pictureData);

        ClientAnchor clientAnchor = picture.getClientAnchor();
        excelPicture.setAnchor(ExcelAnchor.from(clientAnchor));
        return excelPicture;
    }
}
