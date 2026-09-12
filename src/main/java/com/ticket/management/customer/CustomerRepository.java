package com.ticket.management.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ticket.management.entity.Customer;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
