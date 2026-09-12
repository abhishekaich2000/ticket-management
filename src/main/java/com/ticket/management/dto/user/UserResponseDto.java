package com.ticket.management.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
    private Boolean isActive;
}
