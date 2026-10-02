package com.ticket.management.ai.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.TicketComment;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j
public class TickerSummarizer {

    @Qualifier("defaultChatClient")
    private final ChatClient chatClient;

    @Value("classpath:promptTemplate/ticketSummarizerPrompt.st")
    Resource ticketSummarizerPrompt;

    public String summarizeTicket(Ticket ticket, List<TicketComment> ticketComments){
        Assert.notNull(ticket, "Ticket cannot be null");
        Assert.notNull(ticketComments, "Ticket comments cannot be null");
    
        String thread = buildThread(ticket, ticketComments);

        return chatClient.prompt()
            .system(ticketSummarizerPrompt)
            .user(thread)
            .options(ChatOptions.builder().temperature(0.5))
            .call()
            .content();
    }

    private String buildThread(Ticket ticket, List<TicketComment> comments) {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(ticket.getTitle()).append("\n");
        sb.append("Description: ").append(ticket.getDescription()).append("\n\n");
        sb.append("Conversation:\n");
        if (comments.isEmpty()) {
            sb.append("(no comments)\n");
            return sb.toString();
        }
        for (TicketComment c : comments) {
            String who = c.getRole() != null ? c.getRole() : "UNKNOWN";
            String visibility = Boolean.TRUE.equals(c.getIsInternal()) ? "internal" : "public";
            sb.append("- [")
              .append(who)
              .append(", ")
              .append(visibility)
              .append("] ")
              .append(c.getContent())
              .append("\n");
        }
        return sb.toString();
    }
}
