package com.ticket.management.dto.ticket;

import com.ticket.management.entity.enums.TicketEntityType;
import com.ticket.management.entity.enums.TicketEventType;
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
    private TicketEntityType entityType;
    private TicketEventType eventType;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
}
