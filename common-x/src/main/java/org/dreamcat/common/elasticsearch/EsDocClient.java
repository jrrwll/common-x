package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch._types.OpType;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest;
import co.elastic.clients.elasticsearch.core.UpdateByQueryResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation;
import co.elastic.clients.elasticsearch.core.bulk.DeleteOperation;
import co.elastic.clients.elasticsearch.core.bulk.UpdateAction;
import co.elastic.clients.elasticsearch.core.bulk.UpdateOperation;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.json.JsonUtil;

/**
 * Create by tuke on 2021/1/15
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class EsDocClient {

    private final ElasticsearchClient client;

    public boolean insert(String index, String id, String json) {
        return insert(index, id, (Object) json);
    }

    public boolean insert(String index, String id, Object doc) {
        return insert(IndexRequest.of(b -> {
            b.index(index)
                    .id(id)
                    .opType(OpType.Create)
                    .refresh(Refresh.True);
            if (doc instanceof String) {
                return b.withJson(new StringReader((String) doc));
            } else {
                return b.document(doc);
            }
        }));
    }

    @SneakyThrows
    private boolean insert(IndexRequest<?> request) {
        IndexResponse response = client.index(request);
        if (log.isDebugEnabled()) {
            log.debug("insert into index {}, result: {}",
                    request.index(), ElasticsearchUtil.serialize(response));
        }
        return response.result() == Result.Created;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public boolean update(String index, String id, String json) {
        return doSave(index, id, json, false);
    }

    public boolean update(String index, String id, Object doc) {
        return doSave(index, id, doc, false);
    }

    public boolean save(String index, String id, String json) {
        return doSave(index, id, json, true);
    }

    public boolean save(String index, String id, Object doc) {
        return doSave(index, id, doc, true);
    }

    private boolean doSave(String index, String id, Object docOrJson, boolean upsert) {
        Object doc;
        if (docOrJson instanceof String) {
            doc = JsonUtil.fromJsonObject((String) docOrJson);
        } else {
            doc = docOrJson;
        }
        return doSave(UpdateRequest.of(b -> b
                .index(index)
                .id(id)
                .docAsUpsert(upsert)
                .refresh(Refresh.True)
                .doc(doc)), upsert);
    }

    @SneakyThrows
    private boolean doSave(UpdateRequest request, boolean upsert) {
        UpdateResponse response = client.update(request, Void.class);
        if (log.isDebugEnabled()) {
            log.debug("save(upsert={}) for index {}, result: {}",
                    upsert, request.index(), ElasticsearchUtil.serialize(response));
        }

        if (upsert) {
            return response.result() == Result.Updated ||
                    response.result() == Result.Created;
        } else {
            return response.result() == Result.Updated;
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @SneakyThrows
    public boolean delete(String index, String id) {
        DeleteRequest request = DeleteRequest.of(b -> b
                .index(index)
                .id(id)
                .refresh(Refresh.True));

        DeleteResponse response = client.delete(request);
        if (log.isDebugEnabled()) {
            log.debug("delete from index {}, result: {}",
                    index, ElasticsearchUtil.serialize(response));
        }
        return response.result() == Result.Deleted;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @SneakyThrows
    public Long updateByQuery(String index, Script script, Query query) {
        UpdateByQueryRequest request = UpdateByQueryRequest.of(b -> b
                .index(index)
                .script(script)
                .query(query));
        UpdateByQueryResponse response = client.updateByQuery(request);
        if (log.isDebugEnabled()) {
            log.debug("updateByQuery for index {}, result: {}",
                    index, ElasticsearchUtil.serialize(response));
        }
        return response.batches();
    }

    @SneakyThrows
    public Long deleteByQuery(String index, Query query) {
        DeleteByQueryRequest request = DeleteByQueryRequest.of(b -> b
                .index(index)
                .query(query));
        DeleteByQueryResponse response = client.deleteByQuery(request);
        if (log.isDebugEnabled()) {
            log.debug("deleteByQuery from index {}, result: {}",
                    index, ElasticsearchUtil.serialize(response));
        }
        return response.batches();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public boolean bulkInsert(String index, Map<String, String> idJsonMap) {
        List<BulkOperation> bulkOperations = idJsonMap.entrySet().stream()
                .map(entry -> new BulkOperation(CreateOperation.of(b -> b
                        .index(index)
                        .id(entry.getKey())
                        .document(entry.getValue()))))
                .collect(Collectors.toList());
        return bulk(bulkOperations);
    }

    public boolean bulkUpdate(String index, Map<String, String> idJsonMap) {
        return bulkSave(index, idJsonMap, false);
    }

    public boolean bulkSave(String index, Map<String, String> idJsonMap) {
        return bulkSave(index, idJsonMap, true);
    }

    public boolean bulkSave(String index, Map<String, String> idJsonMap, boolean upsert) {
        List<BulkOperation> bulkOperations = idJsonMap.entrySet().stream()
                .map(entry -> new BulkOperation(UpdateOperation.of(b -> b
                        .index(index)
                        .id(entry.getKey())
                        .action(UpdateAction.of(b1 -> b1
                                .doc(entry.getValue())
                                .docAsUpsert(upsert))))))
                .collect(Collectors.toList());
        return bulk(bulkOperations);
    }

    public boolean bulkDelete(String index, Collection<String> ids) {
        List<BulkOperation> bulkOperations = ids.stream()
                .map(id -> new BulkOperation(DeleteOperation.of(b -> b
                        .index(index)
                        .id(id))))
                .collect(Collectors.toList());

        return bulk(bulkOperations);
    }

    @SneakyThrows
    public boolean bulk(List<BulkOperation> bulkOperations) {
        BulkRequest.Builder builder = new BulkRequest.Builder();
        builder.operations(bulkOperations);

        BulkResponse response = client.bulk(builder.build());
        if (log.isDebugEnabled()) {
            log.debug("bulk by {}, result: {}",
                    ElasticsearchUtil.serialize(bulkOperations),
                    ElasticsearchUtil.serialize(response));
        }
        if (!response.errors()) return true;
        if (log.isDebugEnabled()) {
            List<BulkResponseItem> items = response.items();
            List<Map<String, Object>> errors = items.stream().map(item -> {
                Map<String, Object> err = new HashMap<>();
                err.put("index", item.index());
                err.put("id", item.id());
                ErrorCause errorCause = item.error();
                if (errorCause != null) {
                    err.put("errorType", errorCause.type());
                    err.put("errorReason", errorCause.reason());
                }
                return err;
            }).collect(Collectors.toList());
            log.error("bulk errors: {}", JsonUtil.toJson(errors));
        }
        return false;
    }
}
