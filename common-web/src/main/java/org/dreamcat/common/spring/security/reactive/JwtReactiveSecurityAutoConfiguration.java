package org.dreamcat.common.spring.security.reactive;

import org.dreamcat.common.spring.security.JwtProperties;
import org.dreamcat.common.spring.security.PermissionProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create by tuke on 2020/3/19
 */
@Configuration
@EnableConfigurationProperties({PermissionProperties.class, JwtProperties.class})
public class JwtReactiveSecurityAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public JwtReactiveFactory jwtReactiveFactory(
            JwtProperties jwtProperties) {
        return new JwtReactiveFactory(jwtProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public JwtReactiveFilter jwtReactiveFilter(
            JwtReactiveFactory jwtReactiveFactory,
            PermissionProperties permissionProperties) {
        return new JwtReactiveFilter(jwtReactiveFactory, permissionProperties);
    }
}
