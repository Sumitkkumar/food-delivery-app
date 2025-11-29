package com.example.crud.config;

import com.example.crud.common.logging.AccessLogFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public Filter accessLogFilter() {
        return new AccessLogFilter();
    }
}
