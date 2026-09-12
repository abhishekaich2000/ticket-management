package com.ticket.management.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDto{
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    String email;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")    
    String name;

    @NotBlank(message = "Password is required")
    @Size(min = 8,  max = 15, message = "Password must be between 8 and 15 characters")
    String password;
}
