package com.ticket.management.auth;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.stream.Collectors;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import com.ticket.management.dto.JwtDto;

@Service 
public class JwtService {

    private static final String SECRET_KEY = "kjefkjwbfnkjwnlwnflkwvjkwfkljwen";

    public JwtDto generateToken(UserDetails userDetails) {
        Date date = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + 60 * 60 * 1000L);  // 1 hour
        
        String token = Jwts.builder()
            .subject(userDetails.getUsername()) // username is email, used to load the user from the database and check the token is valid
            .claim("email", userDetails.getUsername())
            .claim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            .issuedAt(date)
            .expiration(expiration)
            .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
            .compact();
        return new JwtDto(token, "Bearer", expiration.getTime() - date.getTime());
    }

    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.get("email", String.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
