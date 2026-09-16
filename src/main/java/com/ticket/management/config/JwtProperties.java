package com.ticket.management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;
import jakarta.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private String secret;

    @PostConstruct
    void validate() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("JWT_SECRET environment variable must be set");
        }
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 32 bytes long");
        }
    }
}
