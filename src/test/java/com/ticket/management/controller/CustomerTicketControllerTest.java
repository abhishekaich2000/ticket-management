package com.ticket.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ticket.management.dto.ticket.PagedResponse;
import com.ticket.management.dto.ticket.TicketResponseDto;
import com.ticket.management.exception.GlobalExceptionHandler;
import com.ticket.management.service.TicketService;

@ExtendWith(MockitoExtension.class)
class CustomerTicketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private CustomerTicketController customerTicketController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerTicketController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTicket_returns200() throws Exception {
        TicketResponseDto dto = new TicketResponseDto();
        dto.setId(1L);
        dto.setTicketNumber("TCK-1");
        dto.setTitle("Login issue");

        when(ticketService.getCustomerTicket(1L)).thenReturn(dto);

        mockMvc.perform(get("/customers/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticketNumber").value("TCK-1"));
    }

    @Test
    void getTickets_returnsPagedResponse() throws Exception {
        PagedResponse<TicketResponseDto> page = new PagedResponse<>(
                List.of(),
                0,
                10,
                0,
                0,
                true
        );

        when(ticketService.getCustomerTickets(
                isNull(), isNull(), isNull(), any(), any(), anyInt(), anyInt()
        )).thenReturn(page);

        mockMvc.perform(get("/customers/tickets")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10));
    }
}
