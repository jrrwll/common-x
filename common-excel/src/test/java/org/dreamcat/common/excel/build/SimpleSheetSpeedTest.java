package org.dreamcat.common.excel.build;

import static org.dreamcat.common.util.RandomUtil.choose36;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import com.alibaba.excel.EasyExcel;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.dreamcat.common.Timeit;
import org.dreamcat.common.excel.ExcelWorkbook;
import org.dreamcat.common.excel.style.ExcelStyle;
import org.dreamcat.common.plot.plotly.Plotly;
import org.dreamcat.common.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jerry Will
 * @version 2026-02-04
 */
public class SimpleSheetSpeedTest {

    @Test
    void test() throws Exception {
        List<String> header = Arrays.asList(
                "序号", "文本", "整数", "实数", "布尔",
                "Date日期", "LocalDate日期", "LocalDateTime日期"
        );
        ExcelStyle defaultStyle = new ExcelStyle()
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.CENTER);
        ExcelStyle headerStyle = new ExcelStyle()
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.CENTER)
                .fontName("宋体")
                .fontHeight(14)
                .fontBold(true);

        List<List<String>> easyExcelHeader = header.stream()
                .map(Collections::singletonList)
                .collect(Collectors.toList());

        File dir = new File(System.getenv("HOME"), "Downloads/tmp");

        List<Object> xData = new ArrayList<>();
        List<long[]> tss = new ArrayList<>();
        System.out.println("        dreamcat  easyexcel   poi");
        for (int i = 1; i <= 100_0000; i *= 10) {
            List<List<Object>> body = buildBody(i);

            File file1 = new File(dir, String.format("dreamcat-%07d.xlsx", i));
            File file2 = new File(dir, String.format("easyexcel-%07d.xlsx", i));
            File file3 = new File(dir, String.format("poi-%07d.xlsx", i));
            Timeit timeit = Timeit.ofActions();
            long[] ts = timeit
                    .addAction(() -> {
                        SimpleSheet sheet = new SimpleSheet();
                        sheet.setDefaultStyle(defaultStyle);
                        sheet.setHeaderStyle(headerStyle);
                        sheet.setHeader(header);
                        sheet.setBody(body);
                        new ExcelWorkbook<>().addSheet(sheet).writeToWithBigGrid(file1);
                    })
                    .addAction(() -> {
                        EasyExcel.write(Files.newOutputStream(file2.toPath()))
                                .head(easyExcelHeader).sheet().doWrite(body);
                    })
                    .addAction(() -> {
                        poi(body, file3);
                    })
                    .run();
            System.out.printf("%07d %s\n", i, timeit.formatMs(ts, 16));

            xData.add(i);
            tss.add(ts);
        }
        Plotly.plotAndOpenTimeit(xData, tss, "dreamcat", "easyexcel", "poi");
    }

    private List<List<Object>> buildBody(int size) {
        List<List<Object>> body = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            body.add(Arrays.asList(
                    i, choose36(6),
                    randi(100),
                    rand(-10, 10),
                    rand() > 0.5,
                    new Date(System.currentTimeMillis() - randi(90 * 24 * 3600_000L)),
                    LocalDate.of(2020, randi(1, 13), randi(1, 29)),
                    DateUtil.ofEpochMilli(System.currentTimeMillis() + randi(360 * 24 * 3600_000L))
            ));
        }
        return body;
    }

    private void poi(List<List<Object>> body, File file) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            SXSSFSheet sheet = workbook.createSheet();
            for (int i = 0, m = body.size(); i < m; i++) {
                SXSSFRow row = sheet.createRow(i);

                List<Object> data = body.get(i);
                for (int j = 0, n = data.size(); j < n; j++) {
                    row.createCell(j).setCellValue(data.get(j).toString());
                }
            }
            workbook.write(Files.newOutputStream(file.toPath()));
        }
    }
}
