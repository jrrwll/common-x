package org.dreamcat.common.elasticsearch.param;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import java.util.List;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2021/1/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsSearchParam {

    private String index;
    private List<EsQueryParam> query;
    @Nullable
    private List<EsSortParam> sort;
    @Nullable
    private Integer from;
    @Nullable
    private Integer size;
    @Nullable
    private Boolean fetchSource;
    @Nullable
    private List<String> includes;
    @Nullable
    private List<String> excludes;
    @Nullable
    private Time keepAlive;

    public SearchRequest searchRequest() {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index(index)
                .trackTotalHits(TrackHits.of(b -> b.enabled(true)));

        if (ObjectUtil.isNotEmpty(includes) || ObjectUtil.isNotEmpty(excludes)) {
            SourceFilter.Builder sourceFilterBuilder = new SourceFilter.Builder();
            if (ObjectUtil.isNotEmpty(includes)) {
                sourceFilterBuilder.includes(includes);
            }
            if (ObjectUtil.isNotEmpty(excludes)) {
                sourceFilterBuilder.excludes(excludes);
            }
            builder.source(SourceConfig.of(b -> b
                    .filter(sourceFilterBuilder.build())));
        } else {
            builder.source(SourceConfig.of(b -> b
                    .fetch(true)));
        }

        builder.query(EsQueryParam.query(query));

        if (from != null && size != null) {
            // there is a limit, from + size must be less than or equal to 10000 in default setting
            builder.from(from).size(size);
        }
        if (ObjectUtil.isNotEmpty(sort)) {
            List<SortOptions> sortOptions = EsSortParam.sortOptions(sort);
            builder.sort(sortOptions);
        }
        if (keepAlive != null) {
            builder.scroll(keepAlive);
        }
        return builder.build();
    }
}
