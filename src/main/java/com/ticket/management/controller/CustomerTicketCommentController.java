package com.ticket.management.controller;

import com.ticket.management.entity.TicketComment;
import com.ticket.management.service.TicketCommentService;
import com.ticket.management.dto.TicketCommentRequestDto;
import com.ticket.management.dto.TicketCommentResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

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
        TicketComment comment = commentService.addComment(ticketId, requestDto.getContent(), false);
        return new ResponseEntity<>(convertToDto(comment), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TicketCommentResponseDto>> getTicketComments(@PathVariable Long ticketId) {
        List<TicketComment> comments = commentService.getPublicTicketComments(ticketId);
        return ResponseEntity.ok(comments.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList()));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    private TicketCommentResponseDto convertToDto(TicketComment comment) {
        TicketCommentResponseDto dto = new TicketCommentResponseDto();
        dto.setId(comment.getId());
        dto.setTicketId(comment.getTicket().getId());
        dto.setAuthorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null);
        dto.setAuthorEmail(comment.getEmail());
        dto.setAuthorRole(comment.getRole());
        dto.setContent(comment.getContent());
        dto.setIsInternal(comment.getIsInternal());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
