package org.dreamcat.common.elasticsearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.Pair;
import org.dreamcat.common.Triple;
import org.dreamcat.common.elasticsearch.param.EsMappingParam;
import org.dreamcat.common.elasticsearch.param.EsSearchParam;
import org.dreamcat.common.json.JsonUtil;
import org.dreamcat.common.util.ClassPathUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by tuke on 2021/1/15
 */
@Slf4j
@RestController
@RequestMapping(value = "/data")
public class DataController {

    @Resource
    private EsIndexClient esIndexClient;
    @Resource
    private EsDocClient esDocClient;
    @Resource
    private EsQueryClient esQueryClient;

    @RequestMapping(value = "/{index}", method = RequestMethod.POST)
    public Object createIndex(
            @PathVariable("index") String index,
            @RequestBody List<EsMappingParam> json) throws IOException {
        if (esIndexClient.existsIndex(index)) {
            return false;
        }
        String settings = ClassPathUtil.getResourceAsString("settings.json");
        Map<String, Object> mappings = EsMappingParam.mappings(json);
        log.info("mappings: {}", JsonUtil.toJson(mappings));
        return esIndexClient.createIndex(index, mappings, settings);
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.GET)
    public Object getIndex(
            @PathVariable("index") String index) {
        return esIndexClient.getIndex(index);
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.DELETE)
    public Object deleteIndex(
            @PathVariable("index") String index) {
        return esIndexClient.deleteIndex(index);
    }

    @RequestMapping(value = "/{index}/{id}", method = RequestMethod.POST)
    public Object insert(
            @PathVariable("index") String index,
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> json) {
        return esDocClient.insert(index, id, JsonUtil.toJson(json));
    }

    @RequestMapping(value = "/{index}/{id}", method = RequestMethod.PUT)
    public Object update(
            @PathVariable("index") String index,
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> json) {
        if (!esIndexClient.existsIndex(index)) {
            return false;
        }
        return esDocClient.update(index, id, JsonUtil.toJson(json));
    }

    @RequestMapping(value = "/{index}/{id}", method = RequestMethod.GET)
    public Object get(
            @PathVariable("index") String index,
            @PathVariable("id") String id) {
        Object cls = esQueryClient.get(index, id, NameColor.class);
        Object map = esQueryClient.get(index, id);
        return Pair.of(cls, map);
    }

    @RequestMapping(value = "/{index}/{id}", method = RequestMethod.DELETE)
    public Object delete(
            @PathVariable("index") String index,
            @PathVariable("id") String id) {
        return esDocClient.delete(index, id);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Object search(
            @RequestBody EsSearchParam search) {
        Triple<List<NameColor>, Long, String> cls = esQueryClient.search(search, NameColor.class);
        Triple<List<Map<String, Object>>, Long, String> map = esQueryClient.search(search);
        return Pair.of(cls, map);
    }

    @RequestMapping(value = "/query/{index}", method = RequestMethod.POST)
    public Object query(
            @PathVariable String index,
            @RequestBody List<String> ids) {
        Object cls = esQueryClient.mget(index, ids,
                Arrays.asList("name", "color"), null, NameColor.class);
        Object map = esQueryClient.mget(index, ids,
                null, Arrays.asList("name", "color"));
        return Pair.of(cls, map);
    }

    @Data
    static class NameColor {

        private String name;
        private List<String> color;
    }
}
