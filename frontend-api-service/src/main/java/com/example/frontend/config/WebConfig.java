package com.example.frontend.config;

import com.example.frontend.common.logging.AccessLogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.Filter;

@Configuration
public class WebConfig {
    @Bean
    public Filter accessLogFilter() {
        return new AccessLogFilter();
    }
}
