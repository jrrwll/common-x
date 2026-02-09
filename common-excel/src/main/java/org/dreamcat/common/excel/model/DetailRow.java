package org.dreamcat.common.excel.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Create by tuke on 2021/2/22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailRow<D> {

    private D detail;
    private Map<String, Object> detailExtra;

    public DetailRow(D detail) {
        this.detail = detail;
    }
}
