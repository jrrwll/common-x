package org.dreamcat.common.spring.i18n;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.spring.exception.MessageCode;
import org.dreamcat.common.spring.result.RestResult;
import org.dreamcat.common.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2020/10/31
 *
 * @see AbstractApplicationContext the initMessageSource method
 */
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties({I18nProperties.class})
@Component(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
public class MessageSourceComponent extends ReloadableResourceBundleMessageSource {

    private final I18nProperties i18nProperties;

    /**
     * basename for parentMessageSource, default is 'messages'
     *
     * @see MessageSourceProperties#getBasename()
     */
    @Value("${spring.messages.basename}")
    private String basename;

    public String getMessage(String code, Locale locale) {
        return getMessage(code, null, locale);
    }

    public RestResult<?> getMessage(MessageCode messageCode, Locale locale) {
        return getMessage(messageCode, null, locale);
    }

    public RestResult<?> getMessage(MessageCode messageCode, Object[] args, Locale locale) {
        String message = getMessage(messageCode.getValue(), args, locale);
        return RestResult.error(messageCode.getCode(), message);
    }

    @PostConstruct
    public void init() {
        String path = i18nProperties.getPath();
        if (ObjectUtil.isNotBlank(path)) {
            try {
                this.setBasenames(getAllBasenames(path).toArray(new String[0]));
            } catch (IOException e) {
                // use the logger of the super class
                logger.error(e.getMessage());
            }
        }
        ResourceBundleMessageSource parent = new ResourceBundleMessageSource();
        parent.setBasename(basename);
        this.setParentMessageSource(parent);
    }

    private Set<String> getAllBasenames(String path) throws IOException {
        File dir = new ClassPathResource(path).getFile();
        if (!dir.exists() || !dir.isDirectory()) {
            logger.error("path '" + path + "' doesn't exist or is not a directory");
        }

        Set<String> baseNames = new HashSet<>();
        getAllBasenames(baseNames, dir, "");
        return baseNames;
    }

    private void getAllBasenames(Set<String> basenames, File dir, String basePath) {
        if (dir.isFile()) {
            String name = extractBaseName(i18nProperties.getPath() + dir.getName());
            basenames.add(name);
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            getAllBasenames(basenames, file, basePath + dir.getName() + File.separator);
        }
    }

    // extract ${1} from ${1}_${2}.properties
    private String extractBaseName(String filename) {
        filename = filename.replace(".properties", "");
        for (int i = 0; i < 2; i++) {
            int index = filename.lastIndexOf("_");
            if (index != -1) {
                filename = filename.substring(0, index);
            }
        }
        return filename;
    }
}
