package com.ticket.management.dto;

import com.ticket.management.entity.TicketEventType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TicketHistoryResponseDto {

    private Long id;
    private Long ticketId;
    private Long actorId;
    private String actorEmail;
    private String entityType;
    private TicketEventType eventType;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
}
