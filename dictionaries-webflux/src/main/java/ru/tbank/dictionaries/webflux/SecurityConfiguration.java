package ru.tbank.dictionaries.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HeaderSpec.FrameOptionsSpec;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) throws Exception {
        return http
                .csrf(CsrfSpec::disable)
                .headers(headerSpec -> headerSpec.frameOptions(FrameOptionsSpec::disable))
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers("/api/**").authenticated()
                                .pathMatchers("/metrics").permitAll()
                                .pathMatchers("/management/metrics").permitAll()
                                .pathMatchers("/management/health").permitAll())
                .oauth2ResourceServer(resourceServerSpec -> resourceServerSpec.jwt(
                        jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder)))
                .build();
    }
}
