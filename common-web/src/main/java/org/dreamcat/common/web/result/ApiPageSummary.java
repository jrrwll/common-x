package org.dreamcat.common.web.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jerry Will
 * @version 2021-12-09
 */
@Getter
@Setter
public class ApiPageSummary<T, R> extends ApiPage<T> {

    private R summary; // the summary of all pages
}
