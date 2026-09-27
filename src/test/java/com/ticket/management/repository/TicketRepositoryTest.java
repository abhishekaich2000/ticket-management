package com.ticket.management.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.ticket.management.config.AuditAwareImpl;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.User;
import com.ticket.management.entity.enums.TicketCategory;
import com.ticket.management.entity.enums.TicketPriority;
import com.ticket.management.entity.enums.TicketStatus;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditAwareImpl.class)
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByIdAndCustomerId_withEntityGraph_loadsCustomerAndAgent() {
        User customer = persistUser("Customer One", "customer1@test.com");
        User agent = persistUser("Agent One", "agent1@test.com");

        Ticket ticket = new Ticket();
        ticket.setTicketNumber("TCK-100");
        ticket.setTitle("Cannot reset password");
        ticket.setDescription("Reset link is not working");
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setCategory(TicketCategory.OTHER);
        ticket.setCustomer(customer);
        ticket.setAssignedAgent(agent);
        ticket.setSlaBreached(false);

        Ticket saved = entityManager.persistFlushFind(ticket);
        entityManager.clear(); // force reload from DB not from cache

        Optional<Ticket> found = ticketRepository.findByIdAndCustomerId(saved.getId(), customer.getId());

        assertTrue(found.isPresent());
        assertEquals("Customer One", found.get().getCustomer().getName());
        assertEquals("Agent One", found.get().getAssignedAgent().getName());
    }

    @Test
    void findByCustomerId_returnsOnlyThatCustomersTickets() {
        User customerA = persistUser("A", "a@test.com");
        User customerB = persistUser("B", "b@test.com");

        persistTicket("TCK-A1", customerA, null);
        persistTicket("TCK-A2", customerA, null);
        persistTicket("TCK-B1", customerB, null);
        entityManager.flush();
        entityManager.clear();

        var tickets = ticketRepository.findByCustomerId(customerA.getId());

        assertEquals(2, tickets.size());
        assertTrue(tickets.stream().allMatch(t -> t.getCustomer().getId().equals(customerA.getId())));
    }

    private User persistUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("password");
        user.setIsActive(true);
        return entityManager.persist(user);
    }

    private Ticket persistTicket(String number, User customer, User agent) {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(number);
        ticket.setTitle("Sample title");
        ticket.setDescription("Sample description text");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.LOW);
        ticket.setCategory(TicketCategory.OTHER);
        ticket.setCustomer(customer);
        ticket.setAssignedAgent(agent);
        ticket.setSlaBreached(false);
        return entityManager.persist(ticket);
    }
}
