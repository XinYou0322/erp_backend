package com.example.demo.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Value("${front.end.host}")
    private String fronthost;

    @Bean
    CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允許的來源
        config.setAllowedOrigins(Arrays.asList(fronthost));
        // 允許的方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH"));
        // 允許的請求 header
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // 是否允許 cookie 驗證
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
}
