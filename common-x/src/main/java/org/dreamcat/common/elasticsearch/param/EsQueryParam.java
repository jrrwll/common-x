package org.dreamcat.common.elasticsearch.param;

import static org.dreamcat.common.elasticsearch.ElasticsearchUtil.matchPhraseQuery;
import static org.dreamcat.common.elasticsearch.ElasticsearchUtil.rangeQuery;
import static org.dreamcat.common.elasticsearch.ElasticsearchUtil.termQuery;
import static org.dreamcat.common.elasticsearch.ElasticsearchUtil.termsQuery;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.elasticsearch.ElasticsearchUtil;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2021/1/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsQueryParam {

    /**
     * a json path joined with dot sign
     * it will be ignored when {@link #children} is not empty
     */
    private String name;
    /**
     * a json value
     * it will be ignored when {@link #children} is not empty
     * the query type depends on the java type
     * {@link List}: terms
     * {@link Map.Entry}: range from to
     * {@link String}: match phrase (exact=false) or term (exact=true)
     * other types: term
     */
    private Object value;
    /**
     * exact or fuzzy
     * it will be ignored when {@link #children} is not empty
     */
    @Builder.Default
    private boolean exact = true;

    /**
     * joined type
     */
    @Builder.Default
    private JoinType joinType = JoinType.MUST;
    /**
     * joined nodes
     */
    private List<EsQueryParam> children;

    public Query query() {
        // leaf node
        if (ObjectUtil.isEmpty(children)) {
            return directQuery();
        }

        // tree node
        BoolQuery.Builder builder = new BoolQuery.Builder();
        for (EsQueryParam node : children) {
            Query nodeQuery = node.query();
            switch (joinType) {
                case SHOULD:
                    builder.should(nodeQuery);
                    break;
                case MUST:
                    builder.must(nodeQuery);
                    break;
                case MUST_NOT:
                    builder.mustNot(nodeQuery);
                    break;
                default:
                    break;
            }
        }

        return new Query.Builder()
                .bool(builder.build())
                .build();
    }

    private Query directQuery() {
        // containing query
        if (value instanceof List) {
            return termsQuery(name, (List<?>) value);
        }
        // range query
        else if (value instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
            return rangeQuery(name, entry.getKey(), entry.getValue());
        }
        // equal query
        else {
            if (exact) {
                // A Query that matches documents containing a term.
                return termQuery(name, value);
            } else {
                // Match query is a query that analyzes the text and constructs a phrase query as the result of the analysis
                return matchPhraseQuery(name, (String) value);
            }
        }
    }

    public enum JoinType {
        SHOULD,
        MUST,
        MUST_NOT,
    }

    public static Query query(List<EsQueryParam> query) {
        if (ObjectUtil.isEmpty(query)) {
            return ElasticsearchUtil.matchAllQuery();
        }
        return EsQueryParam.builder()
                .children(query)
                .build()
                .query();
    }
}
