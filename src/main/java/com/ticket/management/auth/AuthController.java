package com.ticket.management.auth;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.ticket.management.dto.user.UserLoginDto;
import com.ticket.management.dto.user.UserRequestDto;
import com.ticket.management.dto.user.UserResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import com.ticket.management.dto.JwtDto;
import com.ticket.management.dto.customer.CustomerRequestDto;
import com.ticket.management.dto.customer.CustomerResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/agents/register")
    public ResponseEntity<UserResponseDto> registerAgent(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = authService.registerUser(userRequestDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/agents/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        UserDetails userDetails = getUserDetails(userLoginDto);
        boolean isStaff = userDetails.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_AGENT"));

        if (!isStaff) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // or throw AccessDeniedException
        }
        JwtDto token = jwtService.generateToken(userDetails);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/customers/register")
    public ResponseEntity<CustomerResponseDto> registerCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {
        CustomerResponseDto customerResponseDto = authService.registerCustomer(customerRequestDto);
        return new ResponseEntity<>(customerResponseDto, HttpStatus.CREATED);
    }
    @PostMapping("/customers/login")
    public ResponseEntity<?> loginCustomer(@Valid @RequestBody UserLoginDto customerLoginDto) {
        UserDetails userDetails = getUserDetails(customerLoginDto);
        boolean isCustomer = userDetails.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .anyMatch(role -> role.equals("ROLE_CUSTOMER"));
        
        if (!isCustomer) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        JwtDto token = jwtService.generateToken(userDetails);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    private UserDetails getUserDetails(UserLoginDto userLoginDto) {
        return (UserDetails) authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(), userLoginDto.getPassword())
        ).getPrincipal();
    }
}
