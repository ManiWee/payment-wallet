package com.mejustmocha.payment_wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            //disable Cross-Site Security Forgery (CSRF) because we are using stateless JWTs, not browser cookies
                .csrf(csrf -> csrf.disable())

                //define access rules for endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/wallet/balance/**").authenticated() //require login
                        .requestMatchers("/api/v1/wallet/transfer").hasAuthority("SCOPE_write:transfers") //require specific permission
                        .anyRequest().permitAll() //allow health checks and swagger UI
                )
                //tell spring security to expect an OAuth2 JWT tocken in the header
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }
}
