package com.insper.prova.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthorizationConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // libera preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                        // APIs públicas sem autenticação
                        .requestMatchers("/public/**").permitAll()

                        // GETs exigem apenas token válido
                        .requestMatchers(HttpMethod.GET,
                                "/api/cursos/**",
                                "/api/avaliacoes/**",
                                "/api/ferramentas/**"
                        ).authenticated()

                        // POSTs reservados a ADMIN
                        .requestMatchers(HttpMethod.POST,
                                "/api/cursos/**",
                                "/api/avaliacoes/**",
                                "/api/ferramentas/**"
                        ).hasRole("ADMIN")

                        // PUTs reservados a ADMIN
                        .requestMatchers(HttpMethod.PUT,
                                "/api/cursos/**",
                                "/api/avaliacoes/**",
                                "/api/ferramentas/**"
                        ).hasRole("ADMIN")

                        // DELETEs reservados a ADMIN
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/cursos/**",
                                "/api/avaliacoes/**",
                                "/api/ferramentas/**"
                        ).hasRole("ADMIN")

                        // qualquer outra requisição exige autenticação
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.addAllowedOriginPattern("*");
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter gal = new JwtGrantedAuthoritiesConverter();
        // claim customizado onde vêm suas roles
        gal.setAuthoritiesClaimName("https://musica-insper.com/roles");
        gal.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(gal);
        return conv;
    }
}
