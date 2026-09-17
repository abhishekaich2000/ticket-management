package com.ticket.management.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ticket.management.entity.User;
import com.ticket.management.security.AppUserDetails;

public class SecurityUtil {

    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserDetails appUserDetails)) {
            throw new IllegalStateException("User is not authenticated");
        }
        return appUserDetails.getUser();
    }
}
