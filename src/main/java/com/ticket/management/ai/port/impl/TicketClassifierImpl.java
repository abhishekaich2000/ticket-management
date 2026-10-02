package com.ticket.management.ai.port.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.ticket.management.ai.dto.TicketClassificationResultDto;
import com.ticket.management.ai.port.TicketClassifier;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.enums.TicketCategory;
import com.ticket.management.repository.TicketRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketClassifierImpl implements TicketClassifier{

    @Qualifier("defaultChatClient")
    private final ChatClient chatClient;

    @Value("classpath:promptTemplate/ticketClassificationPrompt.st")
    Resource ticketClassifierPrompt;

    private final TicketRepository ticketRepository;
    
    @Override
    @Transactional
    public void classifyTicket(Ticket ticket) {
        try{
            String categories = Arrays.stream(TicketCategory.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

            TicketClassificationResultDto classificationResultDto = chatClient.prompt()
                .system(s -> s.text(ticketClassifierPrompt).param("categories", categories))
                .user(ticket.getDescription())
                .options(ChatOptions.builder().temperature(0.2))
                .call()
                .entity(TicketClassificationResultDto.class);
            log.info("Ticket category: {}", classificationResultDto);
            
            if(classificationResultDto.confidence() > 50) {
                ticket.setCategory(classificationResultDto.resolvTicketCategory());
                ticketRepository.save(ticket);
                log.info("Ticket category updated: {}", ticket.getCategory());
            }
        } catch (Exception e) {
            log.error("Error while classifying ticket: {}", e);
        }
    }
}
