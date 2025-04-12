package com.example.pumpit.global.config;

import com.example.pumpit.global.filter.LoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingFilterConfig {
    @Bean
    public FilterRegistrationBean<LoggingFilter> logFilter(LoggingFilter filter) {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(-200); // security는 -100 언저리
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}