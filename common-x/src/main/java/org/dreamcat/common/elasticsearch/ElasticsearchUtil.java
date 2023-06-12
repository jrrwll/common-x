package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.JsonpDeserializer;
import co.elastic.clients.json.JsonpSerializable;
import co.elastic.clients.json.jackson.JacksonJsonpGenerator;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jerry Will
 * @version 2022-04-01
 */
@Slf4j
public class ElasticsearchUtil {

    private ElasticsearchUtil() {
    }

    public static Query matchAllQuery() {
        return Query.of(b -> b.matchAll(MatchAllQuery.of(b1 -> b1)));
    }

    public static Query termQuery(String field, Object value) {
        TermQuery termQuery = TermQuery.of(b1 -> b1
                .field(field)
                .value(mapFieldValue(value)));
        return Query.of(b -> b.term(termQuery));
    }

    public static <T> Query termsQuery(String field, Collection<T> values) {
        List<FieldValue> fieldValues = new ArrayList<>(values.size());
        for (T value : values) {
            fieldValues.add(mapFieldValue(value));
        }
        TermsQuery termsQuery = TermsQuery.of(b1 -> b1
                .field(field)
                .terms(TermsQueryField.of(b2 -> b2.value(fieldValues))));
        return Query.of(b -> b.terms(termsQuery));
    }

    private static FieldValue mapFieldValue(Object value) {
        FieldValue.Builder builder = new FieldValue.Builder();
        if (value instanceof String) {
            builder.stringValue((String) value);
        } else if (value instanceof Long || value instanceof Integer ||
                value instanceof BigInteger) {
            builder.longValue(((Number) value).longValue());
        } else if (value instanceof Double || value instanceof BigDecimal ||
                value instanceof Float) {
            builder.doubleValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            builder.booleanValue((Boolean) value);
        } else if (value == null) {
            builder.nullValue();
        } else {
            if (log.isDebugEnabled()) {
                log.warn("unsupported type for terms query, type={}, value={}",
                        value.getClass(), value);
            }
            builder.nullValue(); // cast to null
        }
        return builder.build();
    }

    public static Query rangeQuery(String field, Object lowerBound, Object upperBound) {
        RangeQuery rangeQuery = RangeQuery.of(b1 -> b1
                .field(field)
                .gte(JsonData.of(lowerBound))
                .lte(JsonData.of(upperBound)));
        return Query.of(b -> b.range(rangeQuery));
    }

    public static Query matchPhraseQuery(String field, String value) {
        MatchPhraseQuery matchPhraseQuery = MatchPhraseQuery.of(b1 -> b1.field(field)
                .query(value));
        return Query.of(b -> b.matchPhrase(matchPhraseQuery));
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

    static {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static <T> T deserialize(String json, JsonpDeserializer<T> deserializer)
            throws IOException {

        try (JsonParser jsonParser = new JacksonJsonpParser(
                objectMapper.getFactory().createParser(json), jsonpMapper)) {
            return deserializer.deserialize(jsonParser, jsonpMapper);
        }
    }

    public static String serialize(JsonpSerializable serializable) throws IOException {
        return serialize(serializable, (s, jsonGenerator) ->
                s.serialize(jsonGenerator, jsonpMapper));
    }

    public static <T extends JsonpSerializable> String serialize(
            Collection<T> serializableCollection) throws IOException {
        return serialize(serializableCollection, (list, jsonGenerator) -> {
            jsonGenerator.writeStartArray();
            for (T serializable : list) {
                serializable.serialize(jsonGenerator, jsonpMapper);
            }
            jsonGenerator.writeEnd();
        });
    }

    public static <T extends JsonpSerializable> String serialize(
            Map<String, T> serializableMap) throws IOException {
        return serialize(serializableMap, (map, jsonGenerator) -> {
            jsonGenerator.writeStartObject();
            map.forEach((k, v) -> {
                jsonGenerator.writeKey(k);
                v.serialize(jsonGenerator, jsonpMapper);
            });
            jsonGenerator.writeEnd();
        });
    }

    private static <T> String serialize(
            T serializableLike, BiConsumer<T, JsonGenerator> serializer) throws IOException {
        try (StringWriter s = new StringWriter()) {
            try (JsonGenerator jsonGenerator = new JacksonJsonpGenerator(
                    objectMapper.getFactory().createGenerator(s))) {
                serializer.accept(serializableLike, jsonGenerator);
            }
            return s.toString();
        }
    }
}
