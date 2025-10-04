package org.dreamcat.common.web;

import org.dreamcat.common.spring.security.JwtServletSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Create by tuke on 2021/5/6
 */
@Import({JwtServletSecurityConfig.class})
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
