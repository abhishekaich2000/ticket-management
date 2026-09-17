package com.ticket.management.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.ticket.management.dto.TicketAssginDto;
import com.ticket.management.dto.TicketResponseDto;
import com.ticket.management.dto.TicketStatusDto;
import com.ticket.management.dto.TicketPriorityDto;
import com.ticket.management.dto.TicketCategoryDto;
import com.ticket.management.dto.TicketSlaDueDateDto;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import com.ticket.management.dto.TicketUpdateRequestDto;
import com.ticket.management.service.TicketService;

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

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public TicketResponseDto assignTicket(@PathVariable Long id,
        @Valid @RequestBody TicketAssginDto ticketAssginDto
    ) {
        return ticketService.assignTicket(id, ticketAssginDto.getAssignedAgentId());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public TicketResponseDto updateTicketStatus(@PathVariable Long id,
        @Valid @RequestBody TicketStatusDto ticketStatusDto
    ) {
        return ticketService.updateTicketStatus(id, ticketStatusDto);
    }

    @PutMapping("/{id}/priority")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public TicketResponseDto updateTicketPriority(@PathVariable Long id,
        @Valid @RequestBody TicketPriorityDto ticketPriorityDto
    ) {
        return ticketService.updateTicketPriority(id, ticketPriorityDto.getPriority());
    }

    @PutMapping("/{id}/category")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public TicketResponseDto updateTicketCategory(@PathVariable Long id,
        @Valid @RequestBody TicketCategoryDto ticketCategoryDto
    ) {
        return ticketService.updateTicketCategory(id, ticketCategoryDto.getCategory());
    }

    @PutMapping("/{id}/sla")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public TicketResponseDto updateSlaDueAt(@PathVariable Long id,
        @Valid @RequestBody TicketSlaDueDateDto ticketSlaDueDateDto
    ) {
        return ticketService.updateSlaDueAt(id, ticketSlaDueDateDto.getSlaDueAt());
    }
}
