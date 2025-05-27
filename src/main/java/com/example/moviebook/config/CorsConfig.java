package com.example.moviebook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로 허용
                .allowedOrigins("http://localhost:3000", "http://localhost:5173", "http://54.180.117.246") // React 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용 메서드
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}