//package com.example.demo.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import com.example.demo.auth.CustomUserDetailsService;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService)
//            throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/api/users/login",
//                                "/api/users/register",
//                                "/api/users/check-username",
//                                "/api/users/check-email",
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/error")
//                        .permitAll()
//                        .requestMatchers("/api/roles/**").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
//                        .anyRequest().authenticated())
//                .userDetailsService(userDetailsService)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable);
//
//        return http.build();
//    }
//}
package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserDetailsService userDetailsService) throws Exception {

        http

                // 【我新增】啟用 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 關閉 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // 原本就允許的 API
                        .requestMatchers(
                                "/api/users/login",
                                "/api/users/register",
                                "/api/users/check-username",
                                "/api/users/check-email",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error")
                        .permitAll()


                        // 原本的角色限制
                        .requestMatchers("/api/roles/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/api/users/**")
                        .hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")


                        // 【我新增】
                        // 目前 ERP 一般 API 先允許通過
                        .requestMatchers("/api/Supplier/**").permitAll()

                        // 【我新增】
                        // 通知 API
                        .requestMatchers("/api/notifications/**").permitAll()

                        // 【我新增】
                        // WebSocket
                        .requestMatchers("/ws/**").permitAll()


                        // 其他沒有設定的路徑
                        .anyRequest().permitAll()
                )

                .userDetailsService(userDetailsService)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable);


        return http.build();
    }


    // =========================================================
    // 【我新增】CORS 設定
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();


        // Vue 前端網址
        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );


        // 允許的 HTTP Method
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );


        // 允許所有 Header
        configuration.setAllowedHeaders(
                List.of("*")
        );


        // 如果使用 HttpSession / Cookie，需要開啟
        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        // 所有路徑套用這份 CORS
        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}
