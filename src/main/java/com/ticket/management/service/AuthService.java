package com.ticket.management.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticket.management.entity.Role;
import com.ticket.management.entity.User;

import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import com.ticket.management.dto.UserRequestDto;
import com.ticket.management.dto.UserResponseDto;
import com.ticket.management.exception.ResourceConflictException;
import com.ticket.management.repository.RoleRepository;
import com.ticket.management.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional 
    public UserResponseDto registerUser(UserRequestDto userRequestDto, String roleName) {
        userRepository.findByEmail(userRequestDto.getEmail()).ifPresent(user -> {
            throw new ResourceConflictException("User already exists");
        });
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(() -> new ResourceConflictException("Role not found"));
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setRoles(Collections.singleton(role));
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return convertToDto(user);
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
}
