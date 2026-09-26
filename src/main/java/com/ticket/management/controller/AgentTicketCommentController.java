package com.ticket.management.controller;

import com.ticket.management.service.TicketCommentService;
import com.ticket.management.dto.ticket.PagedResponse;
import com.ticket.management.dto.ticket.TicketCommentRequestDto;
import com.ticket.management.dto.ticket.TicketCommentResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agents/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class AgentTicketCommentController {

    private final TicketCommentService commentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<TicketCommentResponseDto> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketCommentRequestDto requestDto) {
        TicketCommentResponseDto comment = commentService.addComment(ticketId, requestDto.getContent(),
            requestDto.getIsInternal() != null ? requestDto.getIsInternal() : false);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<PagedResponse<TicketCommentResponseDto>> getTicketComments(
        @PathVariable Long ticketId,
        @RequestParam (name="page", required = false, defaultValue = "0") Integer pageNumber,
        @RequestParam (name="size", required = false, defaultValue = "10") Integer pageSize
    ) {
        PagedResponse<TicketCommentResponseDto> comments = commentService.getTicketComments(ticketId, pageNumber, pageSize);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
