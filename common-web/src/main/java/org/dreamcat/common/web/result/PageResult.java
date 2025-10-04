package org.dreamcat.common.web.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * Create by tuke on 2020/3/2
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> {

    private long total;
    private long totalPage;
    private int pageNo;
    private int pageSize;
    private List<T> items;

    public PageResult(List<T> items, long total, int pageNo, int pageSize) {
        this.items = items;
        this.total = total;
        if (pageSize == 0) {
            this.totalPage = 0;
        } else {
            this.totalPage = total / pageSize + (total % pageSize == 0 ? 0 : 1);
        }

        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public boolean isFirst() {
        return pageNo == 1;
    }

    public boolean isLast() {
        return pageNo == totalPage;
    }

    @JsonProperty
    public Integer prevPage() {
        return isFirst() ? null : pageNo - 1;
    }

    @JsonProperty
    public Integer nextPage() {
        return isLast() ? null : pageNo + 1;
    }

    // maxRadius=2, then return [1, prev,current,next,last]
    public int[] pageSlice(int maxRadius) {
        int rawSize = 2 * maxRadius + 1;
        int[] rawSlice = new int[rawSize];
        rawSlice[maxRadius + 1 - 1] = pageNo;
        for (int i = 1; i <= maxRadius; i++) {
            rawSlice[maxRadius + 1 - 1 - i] = pageNo - i;
            rawSlice[maxRadius + 1 - 1 + i] = pageNo + i;
        }
        return Arrays.stream(rawSlice).distinct().sorted().toArray();
    }
}
