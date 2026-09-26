package com.example.demo.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${front.end.host}")
    private String fronthost;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }

    @Bean
    CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允許的來源
        config.setAllowedOrigins(Arrays.asList(fronthost));
        // 允許的方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        // 允許的請求 header
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-User-Id"));
        // 是否允許 cookie 驗證
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // 【本次修改：修正 ECPay 回傳 Invalid CORS request】
        // ECPay callback 是外部金流的表單／伺服器 POST，不是前端 AJAX。
        // 這兩個端點改由 CheckMacValue 驗證真偽，因此略過瀏覽器 CORS Filter。
        return new CorsFilter(source) {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) {
                return isEcpayCallback(request.getRequestURI());
            }
        };
    }

    private boolean isEcpayCallback(String requestUri) {
        return "/api/ecpay/payment-notify".equals(requestUri)
                || "/api/ecpay/order-result".equals(requestUri);
    }
}

