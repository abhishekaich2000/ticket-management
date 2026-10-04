package com.ticket.management.ai.controller;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/customer/ai")
@RequiredArgsConstructor
public class CustomerAiController {

    private final ChatClient chatClient;
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;
    
    @Value("classpath:promptTemplate/ticketRagChatPrompt.st")
    Resource ticketClassifierPrompt;

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String message) {
        
        String response = chatClient.prompt()
            .system(ticketClassifierPrompt)
            .user(message)
            .advisors(List.of(retrievalAugmentationAdvisor))
            .call()
            .content();
        
        return ResponseEntity.ok(response);
    }
}
