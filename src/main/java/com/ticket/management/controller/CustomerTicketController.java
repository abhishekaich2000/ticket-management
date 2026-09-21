package com.ticket.management.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ticket.management.dto.PagedResponse;
import com.ticket.management.dto.TicketRequestDto;
import com.ticket.management.dto.TicketResponseDto;
import com.ticket.management.dto.TicketSortField;
import com.ticket.management.entity.TicketCategory;
import com.ticket.management.entity.TicketPriority;
import com.ticket.management.entity.TicketStatus;
import com.ticket.management.repository.SortOrder;
import com.ticket.management.service.TicketService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<PagedResponse<TicketResponseDto>> getTickets(
        @Valid @RequestParam (name="status", required = false) TicketStatus status,
        @Valid @RequestParam (name="priority", required = false) TicketPriority priority,
        @Valid @RequestParam (name="category", required = false) TicketCategory category,
        @Valid @RequestParam (name="sortBy", required = false, defaultValue = "CREATED_AT") TicketSortField sortBy,
        @Valid @RequestParam (name="orderBy", required = false, defaultValue = "DESC") SortOrder orderBy,
        @Valid @RequestParam (name="page", required = false, defaultValue = "0") Integer page,
        @Valid @RequestParam (name="size", required = false, defaultValue = "10") Integer size
    ) {
        PagedResponse<TicketResponseDto> response = ticketService.getCustomerTickets(status, priority, category, sortBy, orderBy, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TicketResponseDto> getTicket(@PathVariable Long id) {
        TicketResponseDto ticket = ticketService.getCustomerTicket(id);
        return ResponseEntity.ok(ticket);
    }
}
