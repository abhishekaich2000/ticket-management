package com.ticket.management.dto.customer;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponseDto{
    Long id;
    String email;
    String name;
    Boolean isActive;
    Set<String> roles;
}
