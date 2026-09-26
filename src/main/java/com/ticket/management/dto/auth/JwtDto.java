package com.ticket.management.dto.auth;

public record JwtDto(String token, String type, Long expiresIn) {

}
