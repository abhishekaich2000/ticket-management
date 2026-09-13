package com.ticket.management.core.ticket;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.ticket.management.dto.ticket.TicketRequestDto;
import com.ticket.management.dto.ticket.TicketResponseDto;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.ticket.management.dto.ticket.TicketUpdateRequestDto;

@RestController 
@RequestMapping("/tickets")
@RequiredArgsConstructor 
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public TicketResponseDto createTicket(@Valid  @RequestBody TicketRequestDto ticketRequestDto) {
        return ticketService.createTicket(ticketRequestDto);
    }

    @GetMapping("/{id}")
    public TicketResponseDto getTicket(@PathVariable Long id) {
        return ticketService.getTicket(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public TicketResponseDto updateTicket(@PathVariable Long id, @Valid @RequestBody TicketUpdateRequestDto ticketUpdateRequestDto) {
        return ticketService.updateTicket(id, ticketUpdateRequestDto);
    }
}
