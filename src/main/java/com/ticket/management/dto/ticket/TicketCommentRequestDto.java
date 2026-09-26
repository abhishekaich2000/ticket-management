package com.ticket.management.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketCommentRequestDto {

    @NotBlank(message = "Comment content is required")
    private String content;

    private Boolean isInternal = false;
}
