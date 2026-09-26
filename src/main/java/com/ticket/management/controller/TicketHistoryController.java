package com.ticket.management.controller;

import com.ticket.management.entity.TicketHistory;
import com.ticket.management.service.TicketHistoryService;
import com.ticket.management.dto.ticket.TicketHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tickets/{ticketId}/history")
@RequiredArgsConstructor
public class TicketHistoryController {

    private final TicketHistoryService historyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<TicketHistoryResponseDto>> getTicketHistory(@PathVariable Long ticketId) {
        List<TicketHistory> history = historyService.getTicketHistory(ticketId);
        return ResponseEntity.ok(history.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList()));
    }

    private TicketHistoryResponseDto convertToDto(TicketHistory history) {
        TicketHistoryResponseDto dto = new TicketHistoryResponseDto();
        dto.setId(history.getId());
        dto.setTicketId(history.getTicket().getId());
        dto.setActorId(history.getActor() != null ? history.getActor().getId() : null);
        dto.setActorEmail(history.getActor() != null ? history.getActor().getEmail() : "Deleted User");
        dto.setEntityType(history.getEntityType());
        dto.setEventType(history.getEventType());
        dto.setOldValue(history.getOldValue());
        dto.setNewValue(history.getNewValue());
        dto.setCreatedAt(history.getCreatedAt());
        return dto;
    }
}
