package org.dreamcat.common.spring.security;

import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.spring.util.PathMatcherUtil;
import org.dreamcat.common.util.ObjectUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Create by tuke on 2020/2/26
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "spring.security.permission")
public class PermissionProperties {

    private List<String> permitted;
    private List<String> authenticated;
    private List<Inspection> inspections;

    /**
     * check the permission of requests
     *
     * @param path request URI
     * @return true if current request doesn't need token authorization
     */
    public boolean isPermitted(String path) {
        if (ObjectUtil.isNotEmpty(permitted)) {
            if (ObjectUtil.isNotEmpty(authenticated) && log.isDebugEnabled()) {
                log.warn("You already set 'permitted', "
                        + "so 'authenticated' will be ignored");
            }
            if (ObjectUtil.isNotEmpty(inspections) && log.isDebugEnabled()) {
                log.warn("You already set 'permitted', "
                        + "so 'inspections' will be ignored");
            }

            for (String pattern : permitted) {
                if (PathMatcherUtil.match(pattern, path)) return true;
            }
            return true;
        }

        if (ObjectUtil.isNotEmpty(authenticated)) {
            if (ObjectUtil.isNotEmpty(inspections) && log.isDebugEnabled()) {
                log.warn("You already set 'authenticated', "
                        + "so 'inspections' will be ignored");
            }

            for (String pattern : authenticated) {
                if (PathMatcherUtil.match(pattern, path)) return false;
            }
            return true;
        }

        if (ObjectUtil.isNotEmpty(inspections)) {
            return isOrderlyPermitted(path);
        }

        if (log.isDebugEnabled()) {
            log.warn("None of 'permitted', 'authenticated' or 'inspections'"
                    + " were set, so all requests will be anonymous");
        }
        return true;
    }

    private boolean isOrderlyPermitted(String path) {
        // warn if debug enabled
        boolean warning = !log.isDebugEnabled();
        boolean permitted = true;
        for (Inspection inspection : inspections) {
            List<String> permUrls = inspection.getPermitted();
            List<String> authUrls = inspection.getAuthenticated();
            if (ObjectUtil.isNotEmpty(permUrls)) {
                if (ObjectUtil.isNotEmpty(authUrls)) {
                    if (warning) {
                        log.warn(
                                "You already set 'permitted' in 'inspections', "
                                        + "so the another 'authenticated' will be ignored");
                        warning = false;
                    }
                }

                for (String pattern : permUrls) {
                    if (PathMatcherUtil.match(pattern, path)) permitted = true;
                }
            }

            if (ObjectUtil.isNotEmpty(authUrls)) {
                for (String pattern : authUrls) {
                    if (PathMatcherUtil.match(pattern, path)) permitted = false;
                }
            }
        }
        return permitted;
    }

    // inspections:
    //     - permitted:
    //         - /**
    //     - authenticated:
    //         - /api/v1/**
    //     - permitted:
    //         - /*/*/auth/**
    // authenticate /api/v1/** but /api/v1/auth/**
    @Data
    public static class Inspection {

        private List<String> permitted;
        private List<String> authenticated;
    }
}
