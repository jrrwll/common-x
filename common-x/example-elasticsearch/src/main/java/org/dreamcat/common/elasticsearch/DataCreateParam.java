package org.dreamcat.common.elasticsearch;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.dreamcat.common.elasticsearch.param.EsMappingParam;

/**
 * Create by tuke on 2021/1/22
 */
@Data
public class DataCreateParam {

    private Map<String, Object> value;
    private List<EsMappingParam> mapping;
}
