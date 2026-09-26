package com.ticket.management.messaging.event;

import com.ticket.management.entity.enums.TicketEntityType;
import com.ticket.management.entity.enums.TicketEventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketEvent {

    private Long ticketId;
    private Long userId;
    private TicketEntityType entityType;
    private TicketEventType eventType;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;
}
