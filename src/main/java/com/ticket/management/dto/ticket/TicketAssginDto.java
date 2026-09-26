package com.ticket.management.dto.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketAssginDto {

    @NotNull(message = "Assigned agent ID is required")
    @Positive(message = "Assigned agent ID must be positive")
    private Long assignedAgentId;
}
