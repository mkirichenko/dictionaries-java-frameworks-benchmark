package ru.tbank.dictionaries.springundertow;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headersConfigurer -> headersConfigurer.frameOptions(FrameOptionsConfig::disable))
                .authorizeHttpRequests(requests ->
                                requests
                                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                                        .requestMatchers("/api/**").permitAll()
                                        .requestMatchers("/metrics").permitAll()
                                        .requestMatchers("/management/metrics").permitAll()
                                        .requestMatchers("/management/health").permitAll()
                                      )
                .oauth2ResourceServer(resourceServerConfigurer ->
                                resourceServerConfigurer.jwt(jwtConfigurer ->
                                        jwtConfigurer.decoder(jwtDecoder))
                                     );

        return http.build();
    }
}
