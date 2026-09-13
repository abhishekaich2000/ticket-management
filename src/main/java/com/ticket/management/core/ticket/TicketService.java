package com.ticket.management.core.ticket;

import org.springframework.stereotype.Service;

import com.ticket.management.dto.ticket.TicketRequestDto;
import com.ticket.management.dto.ticket.TicketResponseDto;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.TicketStatus;
import com.ticket.management.entity.TicketPriority;
import com.ticket.management.entity.TicketCategory;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

import lombok.RequiredArgsConstructor;
import com.ticket.management.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import com.ticket.management.dto.ticket.TicketUpdateRequestDto;
import com.ticket.management.core.user.UserRepository;
import com.ticket.management.entity.User;
import com.ticket.management.exception.GeneralErrorException;
import org.springframework.http.HttpStatus;
import com.ticket.management.util.SecurityUtil;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketResponseDto createTicket(TicketRequestDto ticketRequestDto) {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setTitle(generateTitle(ticketRequestDto.getDescription()));
        ticket.setDescription(ticketRequestDto.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCustomer(getUser());
        ticket.setAssignedAgent(null);
        ticket.setCategory(
            ticketRequestDto.getCategory() != null ? ticketRequestDto.getCategory() : TicketCategory.OTHER
        );
        TicketPriority priority =
            ticketRequestDto.getPriority() != null ? ticketRequestDto.getPriority() : TicketPriority.LOW;
        updateTicketPriority(ticket, priority);

        Ticket savedTicket = ticketRepository.save(ticket);
        return convertToDto(savedTicket);
    }

    public TicketResponseDto getTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return convertToDto(ticket);
    }

    @Transactional
    public TicketResponseDto updateTicket(Long id, TicketUpdateRequestDto ticketUpdateRequestDto) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (ticketUpdateRequestDto.getTitle() != null) {
            ticket.setTitle(ticketUpdateRequestDto.getTitle());
        }
        if (ticketUpdateRequestDto.getDescription() != null) {
            ticket.setDescription(ticketUpdateRequestDto.getDescription());
        }
        if (ticketUpdateRequestDto.getPriority() != null) {
            updateTicketPriority(ticket, ticketUpdateRequestDto.getPriority());
        }
        if (ticketUpdateRequestDto.getCategory() != null) {
            ticket.setCategory(ticketUpdateRequestDto.getCategory());
        }
        if (ticketUpdateRequestDto.getSlaDueAt() != null) {
            updateSlaDueAt(ticket, ticketUpdateRequestDto.getSlaDueAt());
        }

        // Assign agent before status so ASSIGNED validation sees the agent
        if (ticketUpdateRequestDto.getAssignedAgentId() != null) {
            assignAgent(ticket, ticketUpdateRequestDto.getAssignedAgentId());
        }

        if (ticketUpdateRequestDto.getStatus() != null) {
            updateTicketStatus(ticket, ticketUpdateRequestDto.getStatus());
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return convertToDto(updatedTicket);
    }

    public List<TicketResponseDto> getCustomerTickets() {
        return ticketRepository.findByCustomerId(getUser().getId())
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public TicketResponseDto getCustomerTicket(Long id) {
        return convertToDto(ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found")));
    }

    public List<TicketResponseDto> getMyTickets() {
        return ticketRepository.findByAssignedAgentId(getUser().getId())
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<TicketResponseDto> getTickets() {
        return ticketRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    private void assignAgent(Ticket ticket, Long agentId) {
        User assignedAgent = userRepository
            .findByIdAndIsActiveTrueAndRoles_RoleName(agentId, "AGENT")
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
        ticket.setAssignedAgent(assignedAgent);

        if (ticket.getStatus() == TicketStatus.OPEN) {
            updateTicketStatus(ticket, TicketStatus.ASSIGNED);
        }
    }

    private User getUser() {
        return SecurityUtil.getAuthenticatedUser();
    }

    private String generateTicketNumber() {
        String currTime = String.valueOf(System.currentTimeMillis());
        return "#" + "TICKET-" + currTime.substring(currTime.length() - 5);
    }

    private String generateTitle(String description) {
        return description.substring(0, Math.min(description.length(), 100));
    }

    private TicketResponseDto convertToDto(Ticket ticket) {
        TicketResponseDto ticketResponseDto = new TicketResponseDto();
        ticketResponseDto.setId(ticket.getId());
        ticketResponseDto.setTicketNumber(ticket.getTicketNumber());
        ticketResponseDto.setTitle(ticket.getTitle());
        ticketResponseDto.setDescription(ticket.getDescription());
        ticketResponseDto.setStatus(ticket.getStatus());
        ticketResponseDto.setPriority(ticket.getPriority());
        ticketResponseDto.setCategory(ticket.getCategory());

        ticketResponseDto.setCustomerId(ticket.getCustomer().getId());
        ticketResponseDto.setCustomerName(ticket.getCustomer().getName());
        ticketResponseDto.setCustomerEmail(ticket.getCustomer().getEmail());

        if (ticket.getAssignedAgent() != null) {
            ticketResponseDto.setAssignedAgentId(ticket.getAssignedAgent().getId());
            ticketResponseDto.setAssignedAgentName(ticket.getAssignedAgent().getName());
        }

        ticketResponseDto.setResolvedAt(ticket.getResolvedAt());
        ticketResponseDto.setClosedAt(ticket.getClosedAt());
        ticketResponseDto.setSlaDueAt(ticket.getSlaDueAt());
        ticketResponseDto.setCreatedAt(ticket.getCreatedAt());
        ticketResponseDto.setUpdatedAt(ticket.getUpdatedAt());
        return ticketResponseDto;
    }

    private void updateTicketStatus(Ticket ticket, TicketStatus newStatus) {
        TicketStatus currentStatus = ticket.getStatus();
        if (currentStatus == newStatus) {
            return;
        }
        Set<TicketStatus> allowedStatusTransitions = new HashSet<>();
        switch (currentStatus) {
            case OPEN:
                allowedStatusTransitions.add(TicketStatus.CLOSED);
                allowedStatusTransitions.add(TicketStatus.ASSIGNED);
                break;
            case ASSIGNED:
                allowedStatusTransitions.add(TicketStatus.OPEN);
                allowedStatusTransitions.add(TicketStatus.IN_PROGRESS);
                break;
            case IN_PROGRESS:
                allowedStatusTransitions.add(TicketStatus.WAITING_FOR_CUSTOMER);
                allowedStatusTransitions.add(TicketStatus.RESOLVED);
                break;
            case WAITING_FOR_CUSTOMER:
                allowedStatusTransitions.add(TicketStatus.IN_PROGRESS);
                allowedStatusTransitions.add(TicketStatus.RESOLVED);
                break;
            case RESOLVED:
                allowedStatusTransitions.add(TicketStatus.CLOSED);
                allowedStatusTransitions.add(TicketStatus.IN_PROGRESS);
                break;
            case CLOSED:
                allowedStatusTransitions.add(TicketStatus.OPEN);
                break;
            default:
                throw new GeneralErrorException(HttpStatus.BAD_REQUEST, "Invalid status transition: " + currentStatus + " -> " + newStatus +
                    "Invalid status transition: " + currentStatus + " -> " + newStatus);
        }
        if (!allowedStatusTransitions.contains(newStatus)) {
            throw new GeneralErrorException(HttpStatus.BAD_REQUEST, "Invalid status transition: " + currentStatus + " -> " + newStatus);
        }

        if (requiresAgent(newStatus) && ticket.getAssignedAgent() == null) {
            throw new GeneralErrorException(HttpStatus.BAD_REQUEST, "Agent is required for status: " + newStatus);
        }

        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.OPEN) {
            ticket.setAssignedAgent(null);
            ticket.setClosedAt(null);
            ticket.setResolvedAt(null);
        }
        if (newStatus == TicketStatus.IN_PROGRESS && currentStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(null);
        }
        if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }
        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
    }

    private boolean requiresAgent(TicketStatus status) {
        return status == TicketStatus.ASSIGNED
            || status == TicketStatus.IN_PROGRESS
            || status == TicketStatus.WAITING_FOR_CUSTOMER
            || status == TicketStatus.RESOLVED;
    }

    private void updateTicketPriority(Ticket ticket, TicketPriority newPriority) {
        switch (newPriority) {
            case LOW:
                ticket.setSlaDueAt(LocalDateTime.now().plusDays(7));
                break;
            case MEDIUM:
                ticket.setSlaDueAt(LocalDateTime.now().plusDays(3));
                break;
            case HIGH:
                ticket.setSlaDueAt(LocalDateTime.now().plusDays(1));
                break;
            case URGENT:
                ticket.setSlaDueAt(LocalDateTime.now().plusHours(4));
                break;
            default:
                throw new GeneralErrorException(HttpStatus.BAD_REQUEST, "Invalid priority: " + newPriority);
        }
        ticket.setPriority(newPriority);
    }

    private void updateSlaDueAt(Ticket ticket, LocalDateTime newSlaDueAt) {
        if (newSlaDueAt.isBefore(LocalDateTime.now())) {
            throw new GeneralErrorException(HttpStatus.BAD_REQUEST, "SLA due date cannot be in the past");
        }
        ticket.setSlaDueAt(newSlaDueAt);
        if (newSlaDueAt.isAfter(LocalDateTime.now().plusDays(7))) {
            ticket.setPriority(TicketPriority.LOW);
        } else if (newSlaDueAt.isAfter(LocalDateTime.now().plusDays(3))) {
            ticket.setPriority(TicketPriority.MEDIUM);
        } else if (newSlaDueAt.isAfter(LocalDateTime.now().plusDays(1))) {
            ticket.setPriority(TicketPriority.HIGH);
        } else {
            ticket.setPriority(TicketPriority.URGENT);
        }
    }
}
