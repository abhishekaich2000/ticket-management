package com.ticket.management.customer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.ticket.management.dto.customer.CustomerResponseDto;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ticket.management.entity.Customer;
import com.ticket.management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import com.ticket.management.entity.User;
import com.ticket.management.entity.Role;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController 
@RequestMapping("/customers")
@RequiredArgsConstructor 
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseDto> getCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return new ResponseEntity<>(convertToDto(customer), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return new ResponseEntity<>(convertToDto(customer), HttpStatus.OK);
    }


    // TODO: Add pagination and sorting
    // TODO: N+1 problem need to fix
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return new ResponseEntity<>(customers.stream().map(this::convertToDto).collect(Collectors.toList()), HttpStatus.OK);
    }

    private CustomerResponseDto convertToDto(Customer customer) {
        User user = customer.getUser();
        CustomerResponseDto customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(customer.getId());
        customerResponseDto.setEmail(customer.getEmail());
        customerResponseDto.setName(customer.getName());
        customerResponseDto.setIsActive(user.getIsActive());
        customerResponseDto.setRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));
        return customerResponseDto;
    }
}
