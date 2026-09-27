package com.ticket.management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.User;
import com.ticket.management.entity.enums.TicketCategory;
import com.ticket.management.entity.enums.TicketPriority;
import com.ticket.management.entity.enums.TicketStatus;
import com.ticket.management.exception.ResourceNotFoundException;
import com.ticket.management.messaging.producer.TicketEventsProducer;
import com.ticket.management.repository.TicketRepository;
import com.ticket.management.repository.UserRepository;
import com.ticket.management.util.SecurityUtil;


@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketEventsProducer ticketEventsProducer;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void getTicket_whenTicketExists_returnsDto() {
        User customer = new User();
        customer.setId(10L);
        customer.setName("Customer One");
        customer.setEmail("customer1@gmail.com");

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNumber("TCK-1");
        ticket.setTitle("Login issue");
        ticket.setDescription("Cannot login to the portal");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.LOW);
        ticket.setCategory(TicketCategory.OTHER);
        ticket.setCustomer(customer);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        var result = ticketService.getTicket(1L);

        assertEquals(1L, result.getId());
        assertEquals("TCK-1", result.getTicketNumber());
        assertEquals("Customer One", result.getCustomerName());
        verify(ticketRepository).findById(1L);
    }

    @Test
    void getTicket_whenMissing_throwsResourceNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicket(99L));
    }

    @Test
    void getCustomerTicket_whenOwnedByUser_returnsDto() {
        User customer = new User();
        customer.setId(10L);
        customer.setName("Customer One");
        customer.setEmail("customer1@gmail.com");

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNumber("TCK-1");
        ticket.setTitle("Login issue");
        ticket.setDescription("Cannot login to the portal");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.LOW);
        ticket.setCategory(TicketCategory.OTHER);
        ticket.setCustomer(customer);

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getAuthenticatedUser).thenReturn(customer);
            when(ticketRepository.findByIdAndCustomerId(1L, 10L)).thenReturn(Optional.of(ticket));

            var result = ticketService.getCustomerTicket(1L);

            assertEquals(1L, result.getId());
            assertEquals(10L, result.getCustomerId());
        }
    }
}
