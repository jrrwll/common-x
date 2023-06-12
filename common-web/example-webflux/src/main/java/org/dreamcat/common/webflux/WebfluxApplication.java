package org.dreamcat.common.webflux;

import org.dreamcat.common.spring.security.reactive.JwtReactiveSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Create by tuke on 2021/5/6
 */
@Import({JwtReactiveSecurityConfig.class})
@SpringBootApplication
public class WebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxApplication.class, args);
    }
}
