package org.dreamcat.common.spring.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.spring.util.ServletUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Create by tuke on 2020/3/5
 *
 * @see ServletUtil#log(HttpServletRequest, HttpServletResponse)
 */
@Slf4j
@Component
public class HeaderLoggingInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
        ServletUtil.log(request, response);
    }

}
