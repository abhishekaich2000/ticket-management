package com.ticket.management.auth;

import org.springframework.stereotype.Service;
import com.ticket.management.dto.user.UserRequestDto;
import com.ticket.management.dto.user.UserResponseDto;
import com.ticket.management.entity.Role;
import com.ticket.management.entity.User;

import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import com.ticket.management.core.customer.CustomerRepository;
import com.ticket.management.core.role.RoleRepository;
import com.ticket.management.core.user.UserRepository;
import com.ticket.management.dto.customer.CustomerRequestDto;
import com.ticket.management.dto.customer.CustomerResponseDto;
import com.ticket.management.entity.Customer;

import org.springframework.transaction.annotation.Transactional;
import com.ticket.management.exception.ResourceConflictException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        userRepository.findByEmail(userRequestDto.getEmail()).ifPresent(user -> {
            throw new ResourceConflictException("User already exists");
        });
        Role role = roleRepository.findByRoleName("AGENT").orElseThrow(() -> new ResourceConflictException("Role not found"));
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setRoles(Collections.singleton(role));
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return convertToDto(user);
    }

    @Transactional
    public CustomerResponseDto registerCustomer(CustomerRequestDto customerRequestDto) {
        customerRepository.findByEmail(customerRequestDto.getEmail()).ifPresent(customer -> {
            throw new ResourceConflictException("Customer already exists");
        });
        userRepository.findByEmail(customerRequestDto.getEmail()).ifPresent(user -> {
            throw new ResourceConflictException("User already exists");
        });
        Role role = roleRepository.findByRoleName("CUSTOMER").orElseThrow(() -> new ResourceConflictException("Role not found"));
        User user = new User();
        user.setEmail(customerRequestDto.getEmail());
        user.setName(customerRequestDto.getName());
        user.setPassword(passwordEncoder.encode(customerRequestDto.getPassword()));
        user.setRoles(Collections.singleton(role));
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setEmail(customerRequestDto.getEmail());
        customer.setName(customerRequestDto.getName());
        customer.setUser(user);
        customerRepository.save(customer);
        return convertToDto(customer,user);
    }

    private UserResponseDto convertToDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));
        userResponseDto.setIsActive(user.getIsActive());
        return userResponseDto;
    }

    private CustomerResponseDto convertToDto(Customer customer, User user) {
        CustomerResponseDto customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(customer.getId());
        customerResponseDto.setEmail(customer.getEmail());
        customerResponseDto.setName(customer.getName());
        customerResponseDto.setIsActive(user.getIsActive());
        customerResponseDto.setRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));
        return customerResponseDto;
    }
}
