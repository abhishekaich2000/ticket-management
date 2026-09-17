package com.ticket.management.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketUpdateRequestDto {

    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters")
    private String title;

    @Size(min = 10, max = 255, message = "Description must be between 10 and 255 characters")
    private String description;
}
