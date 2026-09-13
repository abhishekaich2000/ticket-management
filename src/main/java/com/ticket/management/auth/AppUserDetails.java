package com.ticket.management.auth;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;

import com.ticket.management.entity.User;

public class AppUserDetails implements UserDetails{

    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
            .collect(Collectors.toList());
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override 
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getIsActive());
    }

    public Long getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }

    public boolean hasRole(String roleName){
        String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        return getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority::equals);
    }

    public boolean isCustomer(){
        return hasRole("CUSTOMER");
    }

    public boolean isAgent(){
        return hasRole("AGENT");
    }

    public boolean isAdmin(){
        return hasRole("ADMIN");
    }
}
