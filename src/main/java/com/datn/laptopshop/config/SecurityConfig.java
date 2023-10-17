package com.datn.laptopshop.config;

import com.datn.laptopshop.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().disable();
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/login","/api/register","/api/register/**").permitAll()
//                        .requestMatchers("/api/register").hasRole("ADMIN")
                        .requestMatchers("").hasAnyRole("ADMIN","USER")
                        .anyRequest().authenticated()
                        .and()
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // Add exception handler
        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthEntryPoint);
        http.oauth2Login();

        // chính sách cors
//        http.cors().configurationSource(request -> {
//            CorsConfiguration corsConfig = new CorsConfiguration();
//            corsConfig.setAllowedOrigins(Arrays.asList("https://localhost:3000"));
//            corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//            corsConfig.setAllowedHeaders(Arrays.asList("*"));
//            corsConfig.setAllowCredentials(true);
//            return corsConfig;
//        });

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
