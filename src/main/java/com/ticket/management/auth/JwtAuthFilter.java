package com.ticket.management.auth;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import io.jsonwebtoken.JwtException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!validateToken(request,response)){
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateToken(HttpServletRequest request,HttpServletResponse response) {
            try {
                String authHeader = request.getHeader("Authorization");
            if(authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")){
                LOGGER.info("Token is missing or invalid");
                return true;
            }
            final String jwtToken = authHeader.substring(7);
            
            String username = jwtService.extractUsername(jwtToken);
            if(username == null || username.isEmpty()){
                LOGGER.info("Username is missing or invalid");
                return false;
            }
            LOGGER.info("Token is valid, with username: {}", username);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null && authentication.isAuthenticated()){
                LOGGER.info("User already authenticated");
                return true;
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!userDetails.isEnabled()) {
                LOGGER.info("User is disabled: {}", username);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            LOGGER.info("User authenticated successfully");
            return true;
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
