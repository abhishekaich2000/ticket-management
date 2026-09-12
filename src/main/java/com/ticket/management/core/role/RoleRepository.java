package com.ticket.management.core.role;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.management.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
