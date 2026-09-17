package com.ticket.management.config;

public class UrlConfig {
    public static final String[] PUBLIC_URLS = {
        "/api/agents/login",
        "/api/customers/login",
        "/api/customers/register",
    };

    public static final String[] AUTHENTICATED_URLS = {
        "/api/agents/**",
        "/api/customers/**",
    };
}
