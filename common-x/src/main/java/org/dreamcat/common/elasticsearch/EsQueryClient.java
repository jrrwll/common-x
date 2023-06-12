package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.ClearScrollRequest;
import co.elastic.clients.elasticsearch.core.ClearScrollResponse;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.MgetRequest;
import co.elastic.clients.elasticsearch.core.MgetResponse;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.get.GetResult;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.Triple;
import org.dreamcat.common.elasticsearch.param.EsSearchParam;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2021/1/20
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class EsQueryClient {

    private final ElasticsearchClient client;

    @SneakyThrows
    public boolean exists(String index, String id) {
        ExistsRequest request = ExistsRequest.of(b -> b.index(index).id(id));
        return client.exists(request).value();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public Map<String, Object> get(String index, String id) {
        return get(index, id, Map.class);
    }

    @SneakyThrows
    public <T> T get(String index, String id, Class<T> clazz) {
        GetRequest request = GetRequest.of(b -> b.index(index).id(id));
        GetResponse<?> response = client.get(request, clazz);
        return (T) response.source();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public List<Map<String, Object>> mget(String index, List<String> ids) {
        return mget(index, ids, null, null);
    }

    public List<Map<String, Object>> mget(
            String index, List<String> ids,
            @Nullable List<String> includes, @Nullable List<String> excludes) {
        return (List) mget(index, ids, includes, excludes, Map.class);
    }

    public <T> List<T> mget(String index, List<String> ids, Class<T> clazz) {
        return mget(index, ids, null, null, clazz);
    }

    @SneakyThrows
    public <T> List<T> mget(
            String index, List<String> ids,
            @Nullable List<String> includes, @Nullable List<String> excludes,
            Class<T> clazz) {
        MgetRequest request = MgetRequest.of(b -> {
            b.index(index).ids(ids);
            if (ObjectUtil.isNotEmpty(includes)) {
                b.sourceIncludes(includes);
            }
            if (ObjectUtil.isNotEmpty(excludes)) {
                b.sourceExcludes(excludes);
            }
            return b;
        });
        MgetResponse<T> response = client.mget(request, clazz);

        List<T> list = new ArrayList<>();
        List<MultiGetResponseItem<T>> docs = response.docs();
        for (MultiGetResponseItem<T> doc : docs) {
            GetResult<T> result = doc.result();
            list.add(result.source());
        }
        return list;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public long count(String index) {
        return count(index, ElasticsearchUtil.matchAllQuery());
    }

    @SneakyThrows
    public long count(String index, Query query) {
        CountRequest request = CountRequest.of(b -> b.index(index).query(query));
        CountResponse countResponse = client.count(request);
        return countResponse.count();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @SneakyThrows
    public Triple<List<Map<String, Object>>, Long, String> search(EsSearchParam search) {
        return (Triple) search(search, Map.class);
    }

    @SneakyThrows
    public <T> Triple<List<T>, Long, String> search(
            EsSearchParam search, Class<T> clazz) {
        SearchRequest request = search.searchRequest();
        SearchResponse<T> response = client.search(request, clazz);
        return parseSearchResponse(response.hits(), response.scrollId());
    }

    public Triple<List<Map<String, Object>>, Long, String> scroll(
            String scrollId, int keepAliveSec) {
        return (Triple) scroll(scrollId, keepAliveSec, Map.class);
    }

    @SneakyThrows
    public Triple<List<Map<String, Object>>, Long, String> scroll(
            String scrollId, Time keepAlive) {
        return (Triple) scroll(scrollId, keepAlive, Map.class);
    }

    public <T> Triple<List<T>, Long, String> scroll(
            String scrollId, int keepAliveSec, Class<T> clazz) {
        return scroll(scrollId, timeSec(keepAliveSec), clazz);
    }

    @SneakyThrows
    public <T> Triple<List<T>, Long, String> scroll(
            String scrollId, Time keepAlive, Class<T> clazz) {
        ScrollRequest request = ScrollRequest.of(b -> {
            b.scrollId(scrollId);
            if (keepAlive != null) b.scroll(keepAlive);
            return b;
        });
        ScrollResponse<T> response = client.scroll(request, clazz);
        return parseSearchResponse(response.hits(), response.scrollId());
    }

    private <T> Triple<List<T>, Long, String> parseSearchResponse(
            HitsMetadata<T> searchHits, String scrollId) {
        if (searchHits == null) {
            return Triple.of(Collections.emptyList(), 0L, scrollId);
        }

        TotalHits totalHits = searchHits.total();
        Long total = totalHits != null ? totalHits.value() : null;

        List<Hit<T>> hits = searchHits.hits();
        List<T> items = new ArrayList<>(hits.size());
        for (Hit<T> hit : hits) {
            T item = hit.source();
            items.add(item);
        }
        return Triple.of(items, total, scrollId);
    }

    @SneakyThrows
    public boolean clearScroll(List<String> scrollIds) {
        ClearScrollRequest request = ClearScrollRequest.of(b -> b.scrollId(scrollIds));
        ClearScrollResponse response = client.clearScroll(request);
        return response.succeeded();
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public List<Map<String, Object>> searchAll(
            String index, int size, int keepAliveSec) {
        return (List) searchAll(index, size, keepAliveSec, Map.class);
    }

    public List<Map<String, Object>> searchAll(
            String index, int size, Time keepAlive) {
        return (List) searchAll(index, size, keepAlive, Map.class);
    }

    public <T> List<T> searchAll(
            String index, int size, int keepAliveSec, Class<T> clazz) {
        return searchAll(index, size, timeSec(keepAliveSec), clazz);
    }

    public <T> List<T> searchAll(
            String index, int size, Time keepAlive, Class<T> clazz) {
        try (ScrollIter<T> scrollIter = scrollIter(index, size, keepAlive, clazz)) {
            List<T> result = null;
            while (scrollIter.hasNext()) {
                List<T> list = scrollIter.next();
                if (result == null) {
                    result = new ArrayList<>(scrollIter.total.intValue());
                }
                result.addAll(list);
            }
            return result;
        }
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public ScrollIter<Map<String, Object>> scrollIter(
            String index, int size, int keepAliveSec) {
        return (ScrollIter) scrollIter(index, size, keepAliveSec, Map.class);
    }

    public ScrollIter<Map<String, Object>> scrollIter(
            String index, int size, Time keepAlive) {
        return (ScrollIter) scrollIter(index, size, keepAlive, Map.class);
    }

    public <T> ScrollIter<T> scrollIter(
            String index, int size, int keepAliveSec, Class<T> clazz) {
        return scrollIter(index, size, timeSec(keepAliveSec), clazz);
    }

    public <T> ScrollIter<T> scrollIter(
            String index, int size, Time keepAlive, Class<T> clazz) {
        return new ScrollIter<>(index, size, keepAlive, clazz);
    }

    @ToString
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class ScrollIter<T> implements Iterator<List<T>>, Closeable {

        final String index;
        final int size;
        final Time keepAlive;
        final Class<T> clazz;

        String scrollId;
        List<String> scrollIds;
        @Getter
        Long total;
        @Getter
        Long remaining;

        @Override
        public boolean hasNext() {
            return remaining == null || remaining <= 0;
        }

        @Override
        public List<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Triple<List<T>, Long, String> chunk;
            if (remaining != null) {
                scrollIds.add(scrollId);
                chunk = EsQueryClient.this.scroll(scrollId, keepAlive, clazz);
            } else {
                EsSearchParam esSearchParam = EsSearchParam.builder()
                        .index(index)
                        .size(size)
                        .fetchSource(true)
                        .keepAlive(keepAlive)
                        .build();
                chunk = EsQueryClient.this.search(esSearchParam, clazz);
                remaining = chunk.second();
                // only record it
                total = remaining;
                // need scroll
                if (remaining > size) {
                    scrollIds = new ArrayList<>((int) (remaining / size));
                }
            }

            List<T> list = chunk.first();
            scrollId = chunk.third();
            remaining -= size;
            return list;
        }

        @Override
        public void close() {
            if (ObjectUtil.isNotEmpty(scrollIds)) {
                // release the resources
                boolean cleared = EsQueryClient.this.clearScroll(scrollIds);
                if (cleared && log.isDebugEnabled()) {
                    log.debug("success to clear scroll {}", scrollIds);
                }
            }
        }
    }

    // https://www.elastic.co/guide/en/elasticsearch/reference/current/api-conventions.html#time-units
    private static Time timeSec(int second) {
        return Time.of(b -> b.time(second + "s"));
    }
}
