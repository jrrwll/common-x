package org.dreamcat.common.spring.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Create by tuke on 2020/5/27
 */
public final class PathMatcherUtil {

    private PathMatcherUtil() {
    }

    private static final PathMatcher pathMatcher = new AntPathMatcher();

    public static boolean isPattern(String path) {
        return pathMatcher.isPattern(path);
    }

    public static boolean match(String pattern, String path) {
        return pathMatcher.match(pattern, path);
    }
}
