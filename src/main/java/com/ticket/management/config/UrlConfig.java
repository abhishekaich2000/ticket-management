package com.ticket.management.config;

public class UrlConfig {
    public static final String[] PUBLIC_URLS = {
        "/api/agents/login",
        "/api/customers/login",
        "/api/customers/register",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs",
        "/v3/api-docs/**"
    };

    public static final String[] AUTHENTICATED_URLS = {
        "/api/agents/**",
        "/api/customers/**",
    };
}
