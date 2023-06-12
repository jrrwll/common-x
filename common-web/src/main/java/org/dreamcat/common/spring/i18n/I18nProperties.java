package org.dreamcat.common.spring.i18n;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Create by tuke on 2020/10/31
 */
@Data
@ConfigurationProperties(prefix = "spring.messages.i18n")
public class I18nProperties {

    /**
     * the directory path for i18n files
     */
    private String path = "i18n";
}
