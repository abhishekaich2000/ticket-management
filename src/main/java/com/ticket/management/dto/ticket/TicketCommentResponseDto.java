package com.ticket.management.dto.ticket;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TicketCommentResponseDto {

    private Long id;
    private Long ticketId;
    private Long authorId;
    private String authorEmail;
    private String authorRole;
    private String content;
    private Boolean isInternal;
    private LocalDateTime createdAt;
}
