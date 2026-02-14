package org.dreamcat.common.excel.build;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.choose36;
import static org.dreamcat.common.util.RandomUtil.randi;

import lombok.Data;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.annotation.ExcelColumn;
import org.dreamcat.common.excel.annotation.ExcelType;
import org.dreamcat.common.util.ArrayUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author Jerry Will
 * @version 2026-02-14
 */
public class MasterDetailSheetTest extends BaseTest {

    @Test
    void test() throws Exception {
        List<PojoMaster> rows = ArrayUtil.mapRangeToList(
                1, 11, PojoMaster::new);
        ExcelBuilder.build()
                .addSheet(PojoMaster.class, PojoDetail.class, sheet -> {
                    sheet.body(rows, PojoMaster::getDetails);
                }).writeTo(outputFile("test", "xlsx"));
    }

    @Data
    @ExcelType(name = "PojoMaster")
    public static class PojoMaster {

        int M1;
        String M2 = choose36(randi(2, 7));
        @ExcelColumn(expanded = true)
        PojoMasterExpended expended = new PojoMasterExpended();
        int M3 = randi(2, 7);

        @ExcelColumn(ignore = true)
        List<PojoDetail> details = ArrayUtil.mapRangeToList(1, randi(2, 13), PojoDetail::new);

        public PojoMaster(int seq) {
            this.M1 = seq;
        }
    }

    @Data
    public static class PojoMasterExpended {

        private String ME1 = "me-" + choose26(randi(2, 5));
        private Date ME2 = new Date(System.currentTimeMillis() - randi(randi(100) * 24 * 3600_000L));
    }

    @Data
    public static class PojoDetail {

        int D1;
        @ExcelColumn(expanded = true)
        PojoDetailExpended expended = new PojoDetailExpended();
        LocalDate D2 = LocalDate.now().minusDays(randi(randi(100)));

        public PojoDetail(int seq) {
            this.D1 = seq;
        }
    }

    @Data
    public static class PojoDetailExpended {

        private String DE1 = "de-" + choose26(randi(2, 5));
        private double DE2 = Math.random();
    }
}
