package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import java.util.Arrays;
import org.dreamcat.common.elasticsearch.param.EsSearchParam;
import org.dreamcat.common.elasticsearch.param.EsSortParam;
import org.junit.jupiter.api.Test;

/**
 * @author Jerry Will
 * @version 2022-04-04
 */
class EsQueryClientTest {

    @Test
    void search() throws Exception {
        EsSearchParam param = new EsSearchParam();
        param.setFrom(0);
        param.setSize(20);
        param.setSort(Arrays.asList(
                EsSortParam.builder().field("name.keyword").desc(true).build(),
                EsSortParam.builder().field("foot.toes.count").nested(true).build()));

        SearchRequest request = param.searchRequest();
        System.out.println(ElasticsearchUtil.serialize(request));

        request = SearchRequest.of(b -> {
            b.sort(SortOptions.of(b1 -> b1
                    .field(b2 -> b2
                            .field("foot.color")
                            .nested(b3 -> b3.path("foot"))
                            .order(SortOrder.Desc))));
            return b;
        });
        System.out.println(ElasticsearchUtil.serialize(request));

        request = SearchRequest.of(b -> {
            b.sort(SortOptions.of(b1 -> b1
                    .field(b2 -> b2
                            .field("foot.toes.count")
                            .nested(b3 -> b3.path("foot")
                                    .nested(b4 -> b4.path("foot.toes")))
                            .order(SortOrder.Desc))));
            return b;
        });
        System.out.println(ElasticsearchUtil.serialize(request));
    }
}
