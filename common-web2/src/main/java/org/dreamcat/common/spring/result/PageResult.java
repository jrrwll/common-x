package org.dreamcat.common.spring.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Create by tuke on 2020/3/2
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> {

    private final long total;
    private final long totalPage;
    private final int page;
    private final int size;
    private List<T> items;

    public PageResult(List<T> items, long total, int page, int size) {
        this.items = items;
        this.total = total;
        if (size == 0) {
            this.totalPage = 0;
        } else {
            this.totalPage = total / size + (total % size == 0 ? 0 : 1);
        }

        this.page = page;
        this.size = size;
    }

    public boolean isFirst() {
        return page == 1;
    }

    public boolean isLast() {
        return page == totalPage;
    }

    @JsonSerialize
    public Integer prevPage() {
        return isFirst() ? null : page - 1;
    }

    @JsonSerialize
    public Integer nextPage() {
        return isLast() ? null : page + 1;
    }

    // maxRadius=2, then return [1, prev,current,next,last]
    public int[] pageSlice(int maxRadius) {
        int rawSize = 2 * maxRadius + 1;
        int[] rawSlice = new int[rawSize];
        rawSlice[maxRadius + 1 - 1] = page;
        for (int i = 1; i <= maxRadius; i++) {
            rawSlice[maxRadius + 1 - 1 - i] = page - i;
            rawSlice[maxRadius + 1 - 1 + i] = page + i;
        }
        return Arrays.stream(rawSlice).distinct().sorted().toArray();
    }
}
