package com.ticket.management.dto.ticket;

import com.ticket.management.entity.enums.TicketCategory;
import com.ticket.management.entity.enums.TicketPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketRequestDto {
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 255, message = "Description must be less than 255 characters and greater than 10 characters")
    private String description;

    private TicketPriority priority;
    private TicketCategory category;
}
