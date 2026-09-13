package com.ticket.management.core.ticket;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.ticket.management.dto.ticket.TicketResponseDto;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import com.ticket.management.dto.ticket.TicketUpdateRequestDto;

@RestController 
@RequestMapping("/agents/tickets")
@RequiredArgsConstructor 
public class AgentTicketController {

    private final TicketService ticketService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public TicketResponseDto getTicket(@PathVariable Long id) {
        return ticketService.getTicket(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public List<TicketResponseDto> getMyTickets() {
        return ticketService.getMyTickets();
    }

    @GetMapping 
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public List<TicketResponseDto> getTickets() {
        return ticketService.getTickets();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public TicketResponseDto updateTicket(@PathVariable Long id, @Valid @RequestBody TicketUpdateRequestDto ticketUpdateRequestDto) {
        return ticketService.updateTicket(id, ticketUpdateRequestDto);
    }
}
