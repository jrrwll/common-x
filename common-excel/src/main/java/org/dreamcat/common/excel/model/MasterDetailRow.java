package org.dreamcat.common.excel.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2021/2/22
 * <p>
 * -----.-----.-----  -----.-----.-----  -----.-----.-----  -----.-----.-----
 * master          masterExtra          detail[]         detailExtra[]
 * -----.-----.-----  -----.-----.-----  -----.-----.-----  -----.-----.-----
 * value value value  value value value
 * value value value  value value value  value value value  value value value
 * value value value  value value value
 * -----.-----.-----  -----.-----.-----  -----.-----.-----  -----.-----.-----
 */
@Getter
@Setter
public class MasterDetailRow<M, D> {

    private M master;
    private Map<String, Object> masterExtra;
    private List<DetailRow<D>> details;

    public static <M, D> MasterDetailRow<M, D> fromEntities(
            M master, List<D> details) {
        MasterDetailRow<M, D> row = new MasterDetailRow<>();
        row.setMaster(master);
        row.setDetails(details.stream().map(DetailRow::new).collect(Collectors.toList()));
        return row;
    }
}
