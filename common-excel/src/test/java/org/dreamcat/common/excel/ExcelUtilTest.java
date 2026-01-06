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
        try (Workbook workbook = new XSSFWorkbook(file1)) {
            List<? extends PictureData> allPictures = workbook.getAllPictures();
            for (PictureData pictureData : allPictures) {
                XSSFPictureData xssfPictureData = (XSSFPictureData) pictureData;
                String partName = xssfPictureData.getPackagePart().getPartName().getName();
                System.out.println(pictureData.hashCode() + " = " + pictureData + " = " + partName);
            }

            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println("\nsheet " + sheet.getSheetName());
                Drawing<?> drawing = sheet.getDrawingPatriarch();
                List<Picture> pictures = ExcelPicture.getPictures(drawing);
                for (Picture picture : pictures) {
                    System.out.println(picture.getPictureData().hashCode() + " = " + picture.getPictureData());
                }
            }
        }

        File file2 = new File(System.getenv("HOME") + "/Movies/pic_test2.xls");
        if (!file2.exists()) {
            System.out.println("file not exists");
            return;
        }

        System.out.println("\n\n" + file2);
        try (Workbook workbook = new HSSFWorkbook(new POIFSFileSystem(file2, true))) {
            List<? extends PictureData> allPictures = workbook.getAllPictures();
            for (PictureData pictureData : allPictures) {
                System.out.println(pictureData.hashCode() + " = " + pictureData + " = " + Arrays.hashCode(pictureData.getData()));
            }

            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println("\nsheet " + sheet.getSheetName());
                Drawing<?> drawing = sheet.getDrawingPatriarch();
                List<Picture> pictures = ExcelPicture.getPictures(drawing);
                for (Picture picture : pictures) {
                    System.out.println(picture.getPictureData().hashCode() + " = " + Arrays.hashCode(picture.getPictureData().getData()));
                }
            }
        }
    }
}
