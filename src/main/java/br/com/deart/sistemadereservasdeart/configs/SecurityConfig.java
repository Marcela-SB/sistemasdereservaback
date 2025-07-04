package br.com.deart.sistemadereservasdeart.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private static final String[] WHITE_LIST_URL = {
        "/room/list",
        "/user/list",
        "/reservation/list",
        "/keydelivery/list",
        "/auth/authenticate",
        "/authorization/**"
        };
    private static final String[] ADMIN_LIST_URL = {
            "/room/**",
            "/user/**",
            "/reservation/**",
            "/authorization/**",
            "/keydelivery/**",
            "/auth/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .requestMatchers("/**")
                .permitAll()
                /* .requestMatchers(ADMIN_LIST_URL).hasRole("ADMIN")
                .anyRequest()
                .authenticated()  !Analisar isso depois */ 
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
