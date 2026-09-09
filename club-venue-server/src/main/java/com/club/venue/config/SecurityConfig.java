package com.club.venue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // SPA routes + static resources
            .antMatchers("/", "/index.html", "/test.html", "/favicon.ico",
                    "/css/**", "/js/**", "/fonts/**", "/uploads/**",
                    "/login", "/dashboard", "/club", "/venue", "/activity",
                    "/facility", "/user", "/profile", "/register").permitAll()
            // Public API endpoints
            .antMatchers("/api/user/login", "/api/user/register").permitAll()
            // Activity registration: authenticated users can register/list/cancel
            .antMatchers("/api/activity-registration/**").authenticated()
            // Admin-only endpoints
            .antMatchers("/api/user/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER", "ROLE_CLUB_LEADER")
            .antMatchers("/api/role/**").hasAuthority("ROLE_ADMIN")
            // All other API endpoints require authentication
            .antMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
