package org.dreamcat.common.elasticsearch;

import lombok.Data;
import org.dreamcat.common.elasticsearch.param.EsMappingParam;

import java.util.List;
import java.util.Map;

/**
 * Create by tuke on 2021/1/22
 */
@Data
public class DataCreateParam {

    private Map<String, Object> value;
    private List<EsMappingParam> mapping;
}
