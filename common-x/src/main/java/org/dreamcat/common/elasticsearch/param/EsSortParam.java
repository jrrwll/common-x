package org.dreamcat.common.elasticsearch.param;

import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.NestedSortValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.util.ListUtil;

/**
 * Create by tuke on 2021/1/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsSortParam {

    private String field;
    private boolean desc;
    private boolean nested;

    public SortOptions sortOption() {
        SortOrder sortOrder = desc ? SortOrder.Desc : SortOrder.Asc;
        if (!nested) {
            return SortOptions.of(b -> b.field(FieldSort.of(b1 -> b1
                    .field(field)
                    .order(sortOrder))));
        }

        List<String> paths = spiltPath(field);
        NestedSortValue nestedSortValue = null;
        int n = paths.size();
        for (int i = n - 2; i >= 0; i--) {
            String path = paths.get(i);
            if (nestedSortValue != null) {
                nestedSortValue = new NestedSortValue.Builder()
                        .path(path)
                        .nested(nestedSortValue)
                        .build();
            } else {
                nestedSortValue = NestedSortValue.of(b4 -> b4.path(path));
            }
        }
        FieldSort fieldSort = new FieldSort.Builder().field(field)
                .order(sortOrder)
                .nested(nestedSortValue)
                .build();
        return SortOptions.of(b -> b.field(fieldSort));
    }

    public static List<SortOptions> sortOptions(List<EsSortParam> sort) {
        return sort.stream()
                .map(EsSortParam::sortOption)
                .collect(Collectors.toList());
    }

    private static List<String> spiltPath(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        return ListUtil.map(Arrays.asList(s.split("[.]")), (str, i) -> {
            if (i > 0) sb.append('.');
            return sb.append(str).toString();
        });
    }
}
