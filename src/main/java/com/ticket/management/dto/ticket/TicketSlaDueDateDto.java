package com.ticket.management.dto.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TicketSlaDueDateDto {

    @NotNull(message = "SLA due date is required")
    @FutureOrPresent(message = "SLA due date must be in the present or future")
    private LocalDateTime slaDueAt;
}
