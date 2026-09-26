package com.ticket.management.dto.ticket;

import com.ticket.management.entity.enums.TicketPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketPriorityDto {

    @NotNull(message = "Priority is required")
    private TicketPriority priority;
}
