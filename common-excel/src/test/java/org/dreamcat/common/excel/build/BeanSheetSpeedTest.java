package org.dreamcat.common.excel.build;

import static org.dreamcat.common.excel.build.BeanSheetTest.Pojo;

import org.dreamcat.common.Timeit;
import org.dreamcat.common.asm.BeanMapUtil;
import org.dreamcat.common.excel.BaseTest;
import org.dreamcat.common.excel.build.BeanSheetTest.Item;
import org.dreamcat.common.excel.callback.FitWidthWriteCallback;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2020/7/27
 */
class BeanSheetSpeedTest extends BaseTest {

    // xmx128m, max 88%
    @Test
    void testByCopy() throws Exception {
        byCopy(newPojoList(64));
        byCopy(newPojoList(1024));
        byCopy(newPojoList(4096));
    }

    // xmx128m, max 70%
    @Test
    void testByReuse() throws Exception {
        byReuse(newPojoList(64));
        byReuse(newPojoList(1024));
        byReuse(newPojoList(4096));
    }

    @Test
    void test() {
        System.out.println("  \t\t\t\t copy \t reuse");
        for (int i = 1; i <= 4096; i *= 5) {
            int size = i;
            long[] ts = Timeit.ofActions()
                    .addUnaryAction(() -> newPojoList(size), this::byCopy)
                    .addUnaryAction(() -> newPojoList(size), this::byReuse)
                    .count(3).skip(1).run();
            String s = Arrays.stream(ts).mapToObj(it -> String.format("%6.3fms", it / 1000_000.))
                    .collect(Collectors.joining("\t"));
            System.out.printf("%05d \t %6.3f%% %s\n", i, (1 - ((0.0 + ts[1]) / ts[0])) * 100, s);
        }
    }

    private void byReuse(List<Pojo> pojoList) {
        BeanSheet<Pojo> sheet = new BeanSheet<>(Pojo.class);
        sheet.setName("Sheet One");
        sheet.setBody(pojoList);
        sheet.getWriteCallbacks().add(new FitWidthWriteCallback());
        writeXlsx("byReuse" + pojoList.size(), sheet);
    }

    public void byCopy(List<Pojo> pojoList) throws IOException {
        TableSheet sheet = new TableSheet();
        sheet.setName("Sheet One");

        List<List<Object>> body = new ArrayList<>();
        sheet.setBody(body);
        for (Pojo pojo : pojoList) {
            Map<String, Object> map = BeanMapUtil.toShallowMap(pojo);

            Item item = (Item) map.remove("item");
            Map<String, Object> itemMap = Collections.emptyMap();
            if (item != null) {
                itemMap = BeanMapUtil.toShallowMap(item);
            }

            if (itemMap.isEmpty()) {
                body.add(new ArrayList<>(map.values()));
            } else {
                List<Object> row = new ArrayList<>(map.size() + itemMap.size());
                row.addAll(map.values());
                row.addAll(itemMap.values());
                body.add(row);
            }
        }

        sheet.getWriteCallbacks().add(new FitWidthWriteCallback());
        writeXlsx("byCopy" + pojoList.size(), sheet);
    }

    private List<Pojo> newPojoList(int size) {
        List<Pojo> pojoList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            pojoList.add(Pojo.create(i + 1));
        }
        return pojoList;
    }

}
