package org.dreamcat.common.spring.exception;

import java.io.Serializable;
import java.util.Locale;

/**
 * Create by tuke on 2020/11/2
 */
public interface MessageCode extends Serializable {

    /**
     * @return code in {@link org.dreamcat.common.spring.result.RestResult}
     */
    int getCode();

    /**
     * @return the message code to look up
     * @see org.springframework.context.MessageSource#getMessage(String, Object[], Locale)
     */
    String getValue();
}
