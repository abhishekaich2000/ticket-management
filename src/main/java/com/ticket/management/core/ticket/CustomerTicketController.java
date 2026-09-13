package com.ticket.management.core.ticket;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ticket.management.dto.ticket.TicketResponseDto;
import com.ticket.management.dto.ticket.TicketRequestDto;
import jakarta.validation.Valid;


@RestController 
@RequestMapping("/customers/tickets")
@RequiredArgsConstructor 
public class CustomerTicketController {

    private final TicketService ticketService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public TicketResponseDto createTicket(@Valid  @RequestBody TicketRequestDto ticketRequestDto) {
        return ticketService.createTicket(ticketRequestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<TicketResponseDto> getTickets() {
        return ticketService.getCustomerTickets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public TicketResponseDto getTicket(@PathVariable Long id) {
        return ticketService.getCustomerTicket(id);
    }
}
