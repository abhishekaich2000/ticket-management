package com.ticket.management.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import com.ticket.management.dto.ticket.PagedResponse;
import com.ticket.management.dto.ticket.TicketAssginDto;
import com.ticket.management.dto.ticket.TicketResponseDto;
import com.ticket.management.dto.ticket.TicketStatusDto;
import com.ticket.management.dto.ticket.TicketPriorityDto;
import com.ticket.management.dto.ticket.TicketCategoryDto;
import com.ticket.management.dto.ticket.TicketSlaDueDateDto;
import com.ticket.management.dto.ticket.TicketSortField;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import com.ticket.management.dto.ticket.TicketUpdateRequestDto;
import com.ticket.management.entity.enums.TicketCategory;
import com.ticket.management.entity.enums.TicketPriority;
import com.ticket.management.entity.enums.TicketStatus;
import com.ticket.management.dto.ticket.SortOrder;
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
    public ResponseEntity<PagedResponse<TicketResponseDto>> getTickets(
        @Valid  @RequestParam (name="status", required = false) TicketStatus status,
        @Valid  @RequestParam (name="priority", required = false) TicketPriority priority,
        @Valid  @RequestParam (name="category", required = false) TicketCategory category,
        @Valid  @RequestParam (name="assignedAgentId", required = false) Long assignedAgentId,
        @Valid  @RequestParam (name="customerId", required = false) Long customerId,
        @Valid  @RequestParam (name="sortBy", required = false, defaultValue = "CREATED_AT") TicketSortField sortBy,
        @Valid  @RequestParam (name="orderBy", required = false, defaultValue = "DESC") SortOrder orderBy,
        @Valid  @RequestParam (name="page", required = false, defaultValue = "0") Integer page,
        @Valid  @RequestParam (name="size", required = false, defaultValue = "10") Integer size
    ) {
        PagedResponse<TicketResponseDto> pagedResponse = ticketService.getTickets(status, priority, category, assignedAgentId, customerId, sortBy, orderBy, page, size);
        return ResponseEntity.ok(pagedResponse);
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
