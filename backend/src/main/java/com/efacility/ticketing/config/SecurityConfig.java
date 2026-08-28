package com.efacility.ticketing.config;

import com.efacility.ticketing.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${application.cors.allowed-origin}")
    private String allowedOrigin;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/buildings/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/buildings/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/buildings/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/buildings/**").hasRole("MANAGER")

                        .requestMatchers(HttpMethod.GET, "/apartments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/apartments/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/apartments/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/apartments/**").hasRole("MANAGER")

                        .requestMatchers(HttpMethod.GET, "/tickets/export/**").hasAnyRole("MANAGER", "TECHNICIAN")

                        .requestMatchers(HttpMethod.POST, "/tickets/create").hasRole("TENANT")
                        .requestMatchers(HttpMethod.GET, "/tickets/my").hasRole("TENANT")
                        .requestMatchers(HttpMethod.GET, "/tickets/all").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/tickets/assign").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/tickets/updatePriority").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/tickets/assigned").hasRole("TECHNICIAN")
                        .requestMatchers(HttpMethod.PUT, "/tickets/updateStatus").hasAnyRole("MANAGER", "TECHNICIAN")
                        .requestMatchers(HttpMethod.GET, "/tickets/{id}").authenticated()

                        .requestMatchers("/comments/**").authenticated()
                        .requestMatchers("/ticket-history/**").authenticated()

                        .requestMatchers("/dashboard/**").hasRole("MANAGER")

                        .requestMatchers(HttpMethod.GET, "/users/technicians").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
