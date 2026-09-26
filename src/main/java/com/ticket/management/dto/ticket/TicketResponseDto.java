package com.ticket.management.dto.ticket;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.ticket.management.entity.enums.TicketStatus;
import com.ticket.management.entity.enums.TicketPriority;
import com.ticket.management.entity.enums.TicketCategory;
import java.time.LocalDateTime;

@Getter 
@Setter 
@NoArgsConstructor 
public class TicketResponseDto {

    Long id;
    String ticketNumber;
    String title;
    String description;
    TicketStatus status;
    TicketPriority priority;
    TicketCategory category;

    Long assignedAgentId;
    String assignedAgentName;

    Long customerId;
    String customerName;
    String customerEmail;

    LocalDateTime resolvedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime closedAt;
    LocalDateTime slaDueAt;
}
