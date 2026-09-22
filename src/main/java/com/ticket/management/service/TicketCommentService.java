package com.ticket.management.service;

import com.ticket.management.dto.PagedResponse;
import com.ticket.management.dto.TicketCommentResponseDto;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final TicketEventsProducer ticketEventsProducer;

    @Transactional
    public TicketCommentResponseDto addComment(Long ticketId, String content, Boolean isInternal) {
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
        return convertToDto(savedComment);
    }

    public PagedResponse<TicketCommentResponseDto> getTicketComments(Long ticketId,int pageNumber,int pageSize) {
        ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if(pageNumber < 0){
            pageNumber = 0;
        }
        if(pageSize < 1){
            pageSize = 10;
        }
        if(pageSize > 100){
            pageSize = 100;
        }
        Sort.Direction sortDirection = Sort.Direction.DESC; // Default sort direction
        Sort sort = Sort.by(sortDirection, "createdAt");
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sort);
    
        Page<TicketComment> ticketCommentPage = commentRepository.findByTicketId(ticketId, pageable);
        
        List<TicketCommentResponseDto> commentDtos = ticketCommentPage.getContent().stream()
            .map(this::convertToDto)
            .toList();
        
        return new PagedResponse<>(commentDtos, ticketCommentPage.getNumber(), ticketCommentPage.getSize(), ticketCommentPage.getTotalElements(), ticketCommentPage.getTotalPages(), ticketCommentPage.isLast());
    }

    public PagedResponse<TicketCommentResponseDto> getPublicTicketComments(Long ticketId, int pageNumber, int pageSize) {
        User user = SecurityUtil.getAuthenticatedUser();
        ticketRepository.findByIdAndCustomerId(ticketId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        
        if(pageNumber < 0){
            pageNumber = 0;
        }
        if(pageSize < 1){
            pageSize = 10;
        }
        if(pageSize > 100){
            pageSize = 100;
        }
        Sort.Direction sortDirection = Sort.Direction.DESC; // Default sort direction
        Sort sort = Sort.by(sortDirection, "createdAt");
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<TicketComment> ticketCommentPage = commentRepository.findByTicketIdAndIsInternalFalse(ticketId, pageable);
        
        List<TicketCommentResponseDto> commentDtos = ticketCommentPage.getContent().stream()
            .map(this::convertToDto)
            .toList();
        
        return new PagedResponse<>(commentDtos, ticketCommentPage.getNumber(), ticketCommentPage.getSize(), ticketCommentPage.getTotalElements(), ticketCommentPage.getTotalPages(), ticketCommentPage.isLast());
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
