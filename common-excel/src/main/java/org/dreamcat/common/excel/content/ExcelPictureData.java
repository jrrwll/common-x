package org.dreamcat.common.excel.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Create by tuke on 2021/2/14
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ExcelPictureData {

    private final byte[] data;
    private final int pictureType;

    public static ExcelPictureData newJpeg(byte[] data) {
        return new ExcelPictureData(data, Workbook.PICTURE_TYPE_JPEG);
    }

    public static ExcelPictureData newPng(byte[] data) {
        return new ExcelPictureData(data, Workbook.PICTURE_TYPE_PNG);
    }
}
