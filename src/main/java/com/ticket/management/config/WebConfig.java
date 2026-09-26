package com.ticket.management.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.bind.annotation.RestController;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", c ->
                c.isAnnotationPresent(RestController.class)
                        && !c.getPackageName().startsWith("org.springdoc"));
    }
}
