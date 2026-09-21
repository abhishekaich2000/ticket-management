package com.ticket.management.controller;

import com.ticket.management.service.TicketCommentService;
import com.ticket.management.dto.PagedResponse;
import com.ticket.management.dto.TicketCommentRequestDto;
import com.ticket.management.dto.TicketCommentResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class CustomerTicketCommentController {

    private final TicketCommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TicketCommentResponseDto> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketCommentRequestDto requestDto) {
        TicketCommentResponseDto comment = commentService.addComment(ticketId, requestDto.getContent(), false);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PagedResponse<TicketCommentResponseDto>> getTicketComments(
        @PathVariable Long ticketId,
        @RequestParam (name="page", required = false, defaultValue = "0") Integer pageNumber,
        @RequestParam (name="size", required = false, defaultValue = "10") Integer pageSize
    ) {
        PagedResponse<TicketCommentResponseDto> comments = commentService.getPublicTicketComments(ticketId,pageNumber,pageSize);
        
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
