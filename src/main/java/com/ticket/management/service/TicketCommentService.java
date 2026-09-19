package com.ticket.management.service;

import com.ticket.management.entity.TicketComment;
import com.ticket.management.entity.TicketEntityType;
import com.ticket.management.entity.TicketEventType;
import com.ticket.management.entity.User;
import com.ticket.management.events.TicketEvent;
import com.ticket.management.repository.TicketCommentRepository;
import com.ticket.management.repository.TicketRepository;
import com.ticket.management.exception.ResourceNotFoundException;
import com.ticket.management.mq.producer.TicketEventsProducer;
import com.ticket.management.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final TicketEventsProducer ticketEventsProducer;

    @Transactional
    public TicketComment addComment(Long ticketId, String content, Boolean isInternal) {
        var ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User user = SecurityUtil.getAuthenticatedUser();

        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setAuthor(user);
        comment.setEmail(user.getEmail());
        comment.setRole(user.getRoles().stream()
            .map(r -> r.getRoleName())
            .findFirst()
            .orElse("UNKNOWN"));
        comment.setContent(content);
        comment.setIsInternal(isInternal);
        TicketEvent ticketCommentEvent = TicketEvent.builder()
            .ticketId(ticket.getId())
            .userId(user.getId())
            .entityType(TicketEntityType.COMMENT)
            .eventType(TicketEventType.CREATED)
            .oldValue(null)
            .newValue(content)
            .timestamp(java.time.LocalDateTime.now())
            .build();
        TicketComment savedComment = commentRepository.save(comment);
        ticketEventsProducer.sendTicketCreatedEvent(ticketCommentEvent);
        return savedComment;
    }

    public List<TicketComment> getTicketComments(Long ticketId) {
        ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return commentRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }

    public List<TicketComment> getPublicTicketComments(Long ticketId) {
        ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return commentRepository.findByTicketIdAndIsInternalFalseOrderByCreatedAtDesc(ticketId);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        TicketComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = SecurityUtil.getAuthenticatedUser();
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cannot delete other users' comments");
        }

        commentRepository.delete(comment);
    }
}
