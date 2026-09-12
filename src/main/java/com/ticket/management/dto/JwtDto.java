package com.ticket.management.dto;

public record JwtDto(String token, String type, Long expiresIn) {

}
