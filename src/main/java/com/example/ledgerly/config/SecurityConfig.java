package com.example.ledgerly.config;

import com.example.ledgerly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(@Lazy UserService userService,
                         JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         @Lazy JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userService = userService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider bean
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/health").permitAll()
                .requestMatchers("/api/v1/health/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // Protected endpoints - Auth
                .requestMatchers("/api/v1/auth/users/**").hasAnyRole("ADMIN", "OWNER")
                .requestMatchers("/api/v1/auth/stats").hasRole("ADMIN")
                .requestMatchers("/api/v1/auth/me").authenticated()
                                    // Protected endpoints - Profile
                    .requestMatchers("/api/v1/profile").authenticated()
                    .requestMatchers("/api/v1/profile/contact").authenticated()
                    .requestMatchers("/api/v1/profile/business").hasAnyRole("OWNER", "ADMIN")
                    .requestMatchers("/api/v1/profile/**").hasAnyRole("ADMIN", "OWNER")
                    // Protected endpoints - Customers
                    .requestMatchers("/api/v1/customers/**").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    // Protected endpoints - Ledger
                    .requestMatchers("/api/v1/ledger/**").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    // Protected endpoints - Files
                    .requestMatchers("/api/v1/files/**").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    // Protected endpoints - Business Rules
                    .requestMatchers("/api/v1/business-rules/**").hasAnyRole("ADMIN", "OWNER")
                    // Protected endpoints - Audit
                    .requestMatchers("/api/v1/audit/**").hasAnyRole("ADMIN", "OWNER")
                    // All other requests require authentication
                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
