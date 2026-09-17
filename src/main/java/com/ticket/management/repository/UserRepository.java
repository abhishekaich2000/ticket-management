package com.ticket.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.management.entity.User;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    Optional<User> findByIdAndIsActiveTrueAndRoles_RoleName(Long id, String roleName);
}