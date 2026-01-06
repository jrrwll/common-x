package org.dreamcat.common.excel.content;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPictureData;

import java.util.Arrays;
import java.util.Objects;

/**
 * Create by tuke on 2021/2/14
 */
@Data
@NoArgsConstructor
public class ExcelPictureData {

    @Getter(AccessLevel.NONE)
    private String id;
    private byte[] data;
    private int pictureType;

    public static ExcelPictureData from(PictureData picture) {
        ExcelPictureData data = new ExcelPictureData(picture.getData(), picture.getPictureType());
        if (picture instanceof XSSFPictureData) {
            String name = ((XSSFPictureData) picture).getPackagePart().getPartName().getName();
            data.setId(name);
        } else {
            data.setId(String.valueOf(Arrays.hashCode(data.getData())));
        }
        return data;
    }

    public static ExcelPictureData newJpeg(byte[] data) {
        return new ExcelPictureData(data, Workbook.PICTURE_TYPE_JPEG);
    }

    public static ExcelPictureData newPng(byte[] data) {
        return new ExcelPictureData(data, Workbook.PICTURE_TYPE_PNG);
    }

    private ExcelPictureData(byte[] data, int pictureType) {
        this.id = String.valueOf(Arrays.hashCode(data));
        this.data = data;
        this.pictureType = pictureType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
