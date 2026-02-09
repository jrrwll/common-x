package org.dreamcat.common.excel.build;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.annotation.ExcelColumn;
import org.dreamcat.common.excel.model.DetailRow;
import org.dreamcat.common.excel.model.MasterDetailRow;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Create by tuke on 2021/2/16
 */
public class MasterDetailSheetEdgeTest extends BaseTest {

    @Test
    void testMasterDetail() {
        Builder<?, ?> builder = new Builder<>(Master.class, Detail.class, Master::build, Detail::build);

        writeXlsx("testMasterDetail",
                builder.buildSheet(10, 5, 0, 0)
        );
    }

    @RequiredArgsConstructor
    private static class Builder<M, D> {

        final Class<M> masterClass;
        final Class<D> detailClass;
        final Function<Integer, M> masterBuilder;
        final BiFunction<Integer, Integer, D> detailBuilder;

        public MasterDetailSheet<?, ?> buildSheet(int total, int detailBound, int masterMapSize, int detailMapSize) {
            return build(total, detailBound, masterMapSize, detailMapSize,
                    masterClass, detailClass, masterBuilder, detailBuilder);
        }
    }

    private static <M, D> MasterDetailSheet<M, D> build(
            int total, int detailBound, int masterMapSize, int detailMapSize,
            Class<M> masterClass, Class<D> detailClass,
            Function<Integer, M> masterBuilder, BiFunction<Integer, Integer, D> detailBuilder) {
        MasterDetailSheet<M, D> sheet = new MasterDetailSheet<>(masterClass, detailClass);

        List<MasterDetailRow<M, D>> body = new ArrayList<>();
        for (int i = 1; i <= total; i++) {
            MasterDetailRow<M, D> md = new MasterDetailRow<>();
            body.add(md);

            md.setMaster(masterBuilder.apply(i));
            if (masterMapSize > 0) {
                md.setMasterExtra(extraMap(masterMapSize, "m"));
            }

            if (detailBound == 0) continue;
            List<DetailRow<D>> details = new ArrayList<>();
            md.setDetails(details);

            int detailCount = randi(1, detailBound + 1);
            for (int j = 1; j <= detailCount; j++) {
                DetailRow<D> d = new DetailRow<>();
                d.setDetail(detailBuilder.apply(i, j));
                if (detailMapSize > 0) {
                    d.setDetailExtra(extraMap(detailMapSize, "d"));
                }
                details.add(d);
            }
        }
        sheet.setBody(body);
        return sheet;
    }

    @Getter
    @Setter
    public static class Master {

        private int id;
        private String name = choose26(randi(4, 7));
        private Date date = new Date(System.currentTimeMillis() - randi(180 * 24 * 3600_000L));

        public static Master build(int id) {
            Master master = new Master();
            master.setId(id);
            return master;
        }
    }

    @Getter
    @Setter
    public static class MasterExpended extends Master {

        @ExcelColumn(expanded = true)
        private Expended expended;
        private boolean flag = rand() > 0.5;
        private BigDecimal amount = new BigDecimal(rand(-100, 100));

        public static MasterExpended build(int id) {
            MasterExpended master = new MasterExpended();
            master.setId(id);
            master.setExpended(new Expended());
            return master;
        }
    }

    @Getter
    @Setter
    public static class Expended {
        private String code = "ex-" + choose26(randi(2, 5));
        private String remark = choose26(randi(1, 3));
        private double price = rand();
    }

    @Getter
    @Setter
    public static class Detail {
        private int masterId;
        private String seq;
        private String name = "Detail-" + choose26(randi(2, 5));
        private LocalDate localDate = LocalDate.now().plusMonths(randi(100));

        public static Detail build(int masterId, int seq) {
            Detail detail = new Detail();
            detail.setMasterId(masterId);
            detail.setSeq(String.format("%07d-%07d", masterId, seq));
            return detail;
        }
    }

    @Getter
    @Setter
    public static class DetailExpend extends Detail {

        @ExcelColumn(expanded = true)
        private Expended expended;
        private BigDecimal amount2 = new BigDecimal(randi(-1000, 0));
        private boolean flag2 = rand() > 0.5;

        public static DetailExpend build(int masterId, int seq) {
            DetailExpend detail = new DetailExpend();
            detail.setMasterId(masterId);
            detail.setSeq(String.format("%07d-%07d", masterId, seq));
            detail.setExpended(new Expended());
            return detail;
        }
    }

    private final static List<Supplier<?>> mapFns = Arrays.asList(
            () -> randi(1, 11),
            () -> "E-" + choose26(randi(3, 7)),
            () -> LocalDateTime.now().minusWeeks(randi(1000))
    );

    private static Map<String, Object> extraMap(int size, String prefix) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 1; i <= size; i++) {
            Object v = mapFns.get(i % mapFns.size()).get();
            map.put(prefix + i, v);
        }
        return map;
    }
}