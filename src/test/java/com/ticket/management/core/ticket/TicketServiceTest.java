package com.ticket.management.core.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ticket.management.core.customer.CustomerRepository;
import com.ticket.management.core.user.UserRepository;
import com.ticket.management.dto.ticket.TicketRequestDto;
import com.ticket.management.dto.ticket.TicketResponseDto;
import com.ticket.management.dto.ticket.TicketUpdateRequestDto;
import com.ticket.management.entity.Customer;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.TicketCategory;
import com.ticket.management.entity.TicketPriority;
import com.ticket.management.entity.TicketStatus;
import com.ticket.management.entity.User;
import com.ticket.management.exception.GeneralErrorException;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    private Customer customer;
    private User agent;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Customer 1");
        customer.setEmail("customer1@gmail.com");

        agent = new User();
        agent.setId(10L);
        agent.setName("Agent 1");
        agent.setEmail("agent1@gmail.com");
        agent.setPassword("secret");
        agent.setIsActive(true);

        SecurityContextHolder.clearContext();
    }

    @Test
    void createTicket_setsOpenStatusAndSlaFromPriority() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("customer1@gmail.com", null)
        );
        when(customerRepository.findByEmail("customer1@gmail.com")).thenReturn(Optional.of(customer));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            t.setId(100L);
            return t;
        });

        TicketRequestDto request = new TicketRequestDto();
        request.setDescription("VPN is not connecting since this morning");
        request.setPriority(TicketPriority.URGENT);
        request.setCategory(TicketCategory.NETWORK);

        TicketResponseDto response = ticketService.createTicket(request);

        assertEquals(TicketStatus.OPEN, response.getStatus());
        assertEquals(TicketPriority.URGENT, response.getPriority());
        assertEquals(TicketCategory.NETWORK, response.getCategory());
        assertNotNull(response.getSlaDueAt());
        assertEquals(1L, response.getCustomerId());
        assertNull(response.getAssignedAgentId());
    }

    @Test
    void updateTicket_openToAssigned_withAgent_succeeds() {
        Ticket ticket = baseTicket(TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(userRepository.findByIdAndIsActiveTrueAndRoles_RoleName(10L, "AGENT"))
            .thenReturn(Optional.of(agent));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketUpdateRequestDto request = new TicketUpdateRequestDto();
        request.setAssignedAgentId(10L);
        request.setStatus(TicketStatus.ASSIGNED);

        TicketResponseDto response = ticketService.updateTicket(1L, request);

        assertEquals(TicketStatus.ASSIGNED, response.getStatus());
        assertEquals(10L, response.getAssignedAgentId());
    }

    @Test
    void updateTicket_assignedToInProgress_succeeds() {
        Ticket ticket = baseTicket(TicketStatus.ASSIGNED, agent);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketUpdateRequestDto request = new TicketUpdateRequestDto();
        request.setStatus(TicketStatus.IN_PROGRESS);

        TicketResponseDto response = ticketService.updateTicket(1L, request);

        assertEquals(TicketStatus.IN_PROGRESS, response.getStatus());
    }

    @Test
    void updateTicket_openToResolved_isRejected() {
        Ticket ticket = baseTicket(TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        TicketUpdateRequestDto request = new TicketUpdateRequestDto();
        request.setStatus(TicketStatus.RESOLVED);

        GeneralErrorException ex = assertThrows(
            GeneralErrorException.class,
            () -> ticketService.updateTicket(1L, request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    }

    @Test
    void updateTicket_assignedWithoutAgent_isRejected() {
        Ticket ticket = baseTicket(TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        TicketUpdateRequestDto request = new TicketUpdateRequestDto();
        request.setStatus(TicketStatus.ASSIGNED);

        GeneralErrorException ex = assertThrows(
            GeneralErrorException.class,
            () -> ticketService.updateTicket(1L, request)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    }

    @Test
    void updateTicket_assignedToOpen_clearsAgent() {
        Ticket ticket = baseTicket(TicketStatus.ASSIGNED, agent);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketUpdateRequestDto request = new TicketUpdateRequestDto();
        request.setStatus(TicketStatus.OPEN);

        TicketResponseDto response = ticketService.updateTicket(1L, request);

        assertEquals(TicketStatus.OPEN, response.getStatus());
        assertNull(response.getAssignedAgentId());
        assertNull(ticket.getAssignedAgent());
    }

    @Test
    void updateTicket_resolvedToInProgress_clearsResolvedAt() {
        Ticket ticket = baseTicket(TicketStatus.RESOLVED, agent);
        ticket.setResolvedAt(java.time.LocalDateTime.now().minusHours(1));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketUpdateRequestDto request = new TicketUpdateRequestDto();
        request.setStatus(TicketStatus.IN_PROGRESS);

        TicketResponseDto response = ticketService.updateTicket(1L, request);

        assertEquals(TicketStatus.IN_PROGRESS, response.getStatus());
        assertNull(response.getResolvedAt());
    }

    @Test
    void updateTicket_inProgressToResolved_setsResolvedAt() {
        Ticket ticket = baseTicket(TicketStatus.IN_PROGRESS, agent);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketUpdateRequestDto request = new TicketUpdateRequestDto();
        request.setStatus(TicketStatus.RESOLVED);

        TicketResponseDto response = ticketService.updateTicket(1L, request);

        assertEquals(TicketStatus.RESOLVED, response.getStatus());
        assertNotNull(response.getResolvedAt());
    }

    @Test
    void updateTicket_priorityUrgent_setsFourHourSla() {
        Ticket ticket = baseTicket(TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketUpdateRequestDto request = new TicketUpdateRequestDto();
        request.setPriority(TicketPriority.URGENT);

        ticketService.updateTicket(1L, request);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());
        Ticket saved = captor.getValue();
        assertEquals(TicketPriority.URGENT, saved.getPriority());
        assertNotNull(saved.getSlaDueAt());
    }

    private Ticket baseTicket(TicketStatus status, User assignedAgent) {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNumber("#TICKET-12345");
        ticket.setTitle("Sample title");
        ticket.setDescription("Sample description long enough");
        ticket.setStatus(status);
        ticket.setPriority(TicketPriority.LOW);
        ticket.setCategory(TicketCategory.OTHER);
        ticket.setCustomer(customer);
        ticket.setAssignedAgent(assignedAgent);
        return ticket;
    }
}
