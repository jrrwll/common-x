package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch._types.analysis.Analyzer;
import co.elastic.clients.elasticsearch._types.analysis.TokenChar;
import co.elastic.clients.elasticsearch._types.analysis.Tokenizer;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.json.JsonData;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * @author Jerry Will
 * @version 2022-04-03
 */
class ElasticsearchUtilTest {

    @Test
    void deserialize() throws Exception {
        Tokenizer tokenizer = Tokenizer.of(b3 -> b3
                .definition(b4 -> b4.ngram(b5 -> b5.maxGram(1)
                        .minGram(1)
                        .tokenChars(TokenChar.Letter,
                                TokenChar.Digit,
                                TokenChar.Symbol,
                                TokenChar.Punctuation))));

        Analyzer analyzer = Analyzer.of(b3 -> b3
                .custom(b4 -> b4.tokenizer("ngram_tokenizer")));

        IndexSettings indexSettings = IndexSettings.of(b1 -> b1.analysis(
                b2 -> b2.analyzer("ngram_analyzer", analyzer)
                        .tokenizer("ngram_tokenizer", tokenizer)));

        String settings = ElasticsearchUtil.serialize(indexSettings);
        System.out.println(settings);

        IndexSettings indexSettings2 = ElasticsearchUtil.deserialize(
                settings, IndexSettings._DESERIALIZER);
        System.out.println(ElasticsearchUtil.serialize(indexSettings2));
    }

    @Test
    void jsonData() throws Exception {
        for (Object o : Arrays.asList("str", 1, 3.14, true, new Date(),
                new Object(), new Object() {
                    String a = "abc";
                    public int[] b = new int[2];
                })) {
            JsonData jsonData = JsonData.of(o);
            System.out.println(ElasticsearchUtil.serialize(jsonData));
        }
    }
}
