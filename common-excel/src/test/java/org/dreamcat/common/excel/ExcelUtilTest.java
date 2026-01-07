package org.dreamcat.common.excel;

import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.common.excel.content.ExcelPicture;
import org.dreamcat.common.excel.content.ExcelPictureData;
import org.dreamcat.common.json.JsonUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Create by tuke on 2020/8/13
 */
class ExcelUtilTest {

    @Test
    void test() throws IOException, InvalidFormatException {
        File file = new File(System.getenv("HOME") + "/Downloads/parse1.xlsx");
        if (!file.exists()) {
            System.out.println("file not exists");
            return;
        }

        List<List<String>> book = ExcelUtil.parseAsString(file, 0);
        // book.forEach(it -> it.forEach(System.out::println));
        book.forEach(System.out::println);
    }

    @SneakyThrows
    @Test
    void testPicture() {
        File file1 = new File(System.getenv("HOME") + "/Movies/pic_test1.xlsx");
        if (!file1.exists()) {
            System.out.println("file not exists");
            return;
        }

        System.out.println("\n\n" + file1);
        ExcelWorkbook<ExcelSheet> book1 = ExcelWorkbook.from(file1);
        System.out.println("pictureData:");
        for (ExcelPictureData pictureData : book1.getPictureDatas()) {
            System.out.println(pictureData.hashCode() + " = " + pictureData);
        }
        for (ExcelSheet sheet : book1.getSheets()) {
            System.out.println("\n\nsheet " + sheet.getName());

            for (ExcelPicture picture : sheet.getPictures()) {
                System.out.println("picture:");
                System.out.println(picture.hashCode() + " = " + picture.getPictureData());
                System.out.println(JsonUtil.toJson(picture.getAnchor()));
            }
        }
        // write back
        book1.writeTo(new File(file1.getParentFile(), "pic_test1_back.xlsx"));

        File file2 = new File(System.getenv("HOME") + "/Movies/pic_test2.xls");
        if (!file2.exists()) {
            System.out.println("file not exists");
            return;
        }

        System.out.println("\n\n" + file2);
        ExcelWorkbook<ExcelSheet> book2 = ExcelWorkbook.from(file2);
        System.out.println("pictureData:");
        for (ExcelPictureData pictureData : book2.getPictureDatas()) {
            System.out.println(pictureData.hashCode() + " = " + pictureData);
        }
        for (ExcelSheet sheet : book2.getSheets()) {
            System.out.println("\n\nsheet " + sheet.getName());

            for (ExcelPicture picture : sheet.getPictures()) {
                System.out.println("picture:");
                System.out.println(picture.hashCode() + " = " + picture.getPictureData());
                System.out.println(JsonUtil.toJson(picture.getAnchor()));
            }
        }
        // write back
        book2.writeTo2003(new File(file2.getParentFile(), "pic_test2_back.xls"));
    }
}
