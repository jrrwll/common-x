package org.dreamcat.common.spring.security.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Create by tuke on 2020/3/19
 */
@RequiredArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class JwtReactiveSecurityConfig {

    private final JwtReactiveFilter jwtReactiveFilter;

    @ConditionalOnMissingBean
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {
        return http
                // no csrf
                .csrf().disable()
                // deny xframe
                .headers().frameOptions().disable()
                // enable cors
                //.and().cors()
                .and()
                .exceptionHandling().authenticationEntryPoint(
                        (exchange, authException) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return response.writeWith(Mono.empty());
                        })
                .and()
                // no cookie
                .anonymous().and()
                .authorizeExchange().anyExchange().permitAll()
                .and()
                .addFilterBefore(jwtReactiveFilter, SecurityWebFiltersOrder.ANONYMOUS_AUTHENTICATION)
                .build();
    }

    //@Bean
    //public MapReactiveUserDetailsService userDetailsService() {
    //    UserDetails user = User.withDefaultPasswordEncoder()
    //            .username("user")
    //            .password("user")
    //            .roles("USER")
    //            .build();
    //    return new MapReactiveUserDetailsService(user);
    //}
}
