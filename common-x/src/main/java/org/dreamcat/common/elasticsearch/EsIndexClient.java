package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.cluster.PutClusterSettingsRequest;
import co.elastic.clients.elasticsearch.cluster.PutClusterSettingsResponse;
import co.elastic.clients.elasticsearch.core.ReindexRequest;
import co.elastic.clients.elasticsearch.core.reindex.Destination;
import co.elastic.clients.elasticsearch.core.reindex.Source;
import co.elastic.clients.elasticsearch.indices.CloseIndexRequest;
import co.elastic.clients.elasticsearch.indices.CloseIndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.elasticsearch.indices.OpenRequest;
import co.elastic.clients.elasticsearch.indices.OpenResponse;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.Pair;
import org.dreamcat.common.json.JsonUtil;

/**
 * Create by tuke on 2021/1/14
 * <p/>
 * There is a limit per shard (2billion)
 * but this is not the limit for an index as an index can have multiple shards.
 */
@Slf4j
@RequiredArgsConstructor
public class EsIndexClient {

    private final ElasticsearchClient client;

    @SneakyThrows
    public List<String> getAllIndex() {
        return getAllIndex("_all"); // *
    }

    @SneakyThrows
    public List<String> getAllIndex(String indexNameLike) {
        GetIndexRequest request = GetIndexRequest.of(b -> b.index(indexNameLike));
        GetIndexResponse response = client.indices().get(request);
        return new ArrayList<>(response.result().keySet());
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public boolean createIndex(String index) {
        return createIndex(index, (String) null, null);
    }

    public boolean createIndex(
            String index, @Nullable Map<String, Object> mapping, @Nullable String settings) {
        return createIndex(index, JsonUtil.toJson(mapping), settings);
    }

    @SneakyThrows
    public boolean createIndex(
            String index, @Nullable String mapping, @Nullable String settings) {
        CreateIndexRequest request = CreateIndexRequest.of(b -> {
            b.index(index);
            if (mapping != null) {
                TypeMapping typeMapping = TypeMapping.of(b1 -> b1
                        .withJson(new StringReader(mapping)));
                b.mappings(typeMapping);
                // b.mappings(ElasticsearchUtil.deserialize(mapping, TypeMapping._DESERIALIZER));
            }
            if (settings != null) {
                IndexSettings indexSettings = IndexSettings.of(b1 -> b1
                        .withJson(new StringReader(settings)));
                b.settings(indexSettings);
            }
            return b;
        });
        CreateIndexResponse response = client.indices().create(request);
        if (log.isDebugEnabled()) {
            log.debug("create index {}, result: {}",
                    index, ElasticsearchUtil.serialize(response));
        }
        return response.shardsAcknowledged();
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    @SneakyThrows
    public boolean deleteIndex(String index) {
        DeleteIndexRequest request = DeleteIndexRequest.of(b -> b.index(index));
        DeleteIndexResponse response = client.indices().delete(request);
        if (log.isDebugEnabled()) {
            log.debug("delete index {}, result: {}",
                    index, ElasticsearchUtil.serialize(response));
        }
        return response.acknowledged();
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    @SneakyThrows
    public boolean existsIndex(String index) {
        ExistsRequest request = ExistsRequest.of(b -> b.index(index));
        BooleanResponse response = client.indices().exists(request);
        return response.value();
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    /**
     * @param index index name
     * @return mapping and settings
     */
    @SneakyThrows
    public Pair<String, String> getIndex(String index) {
        GetIndexRequest request = GetIndexRequest.of(b -> b.index(index));
        GetIndexResponse response = client.indices().get(request);
        IndexState indexState = response.result().get(index);

        TypeMapping mapping = indexState.mappings();
        IndexSettings settings = indexState.settings();

        String mappingJson = mapping != null ? ElasticsearchUtil.serialize(mapping) : "null";
        String settingsJson = settings != null ? ElasticsearchUtil.serialize(settings) : "null";
        return Pair.of(mappingJson, settingsJson);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public void reindex(String sourceIndex, String destIndex) {
        reindex(sourceIndex, destIndex, null);
    }

    @SneakyThrows
    public void reindex(
            String sourceIndex, String destIndex, @Nullable Script script) {
        ReindexRequest request = ReindexRequest.of(b -> {
            b.source(Source.of(b1 -> b1.index(sourceIndex)))
                    .dest(Destination.of(b1 -> b1.index(destIndex)));
            if (script != null) b.script(script);
            return b;
        });
        client.reindex(request);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    @SneakyThrows
    public boolean openIndex(String index) {
        OpenRequest request = OpenRequest.of(b -> b.index(index));
        OpenResponse response = client.indices().open(request);
        return response.acknowledged();
    }

    @SneakyThrows
    public boolean closeIndex(String index) {
        CloseIndexRequest request = CloseIndexRequest.of(b -> b.index(index));
        CloseIndexResponse response = client.indices().close(request);
        return response.acknowledged();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public boolean putMapping(String index, Map<String, Object> mapping) {
        return putMapping(index, JsonUtil.toJson(mapping));
    }

    public boolean putMapping(String index, String mapping) {
        return putMapping(PutMappingRequest.of(b -> b
                .index(index)
                .withJson(new StringReader(mapping))));
    }

    @SneakyThrows
    private boolean putMapping(PutMappingRequest request) {
        PutMappingResponse response = client.indices().putMapping(request);
        return response.acknowledged();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    /**
     * see following link for more information
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html
     *
     * @param autoCreateIndex my-index-000001,index10,-index1*,+ind*
     *                        false
     *                        true
     * @return success or not
     */
    public boolean updatePersistentActionAutoCreateIndex(String autoCreateIndex) {
        return updatePersistentSettings(Collections.singletonMap(
                AUTO_CREATE_INDEX, autoCreateIndex));
    }

    @SneakyThrows
    public boolean updatePersistentSettings(Map<String, ?> source) {
        PutClusterSettingsRequest request = PutClusterSettingsRequest.of(b -> {
            source.forEach((k, v) ->
                    b.persistent(k, JsonData.of(v)));
            return b;
        });
        PutClusterSettingsResponse response = client.cluster().putSettings(request);
        return response.acknowledged();
    }

    private static final String AUTO_CREATE_INDEX = "action.auto_create_index";

}
