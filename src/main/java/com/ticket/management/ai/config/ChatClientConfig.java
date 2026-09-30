package com.ticket.management.ai.config;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@Configuration
public class ChatClientConfig {

    @Value("classpath:promptTemplate/ticketClassificationPrompt.st")
    Resource ticketClassifierPrompt;

    @Bean("ticketClassifierChatClient")
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {

        return ChatClient.builder(ollamaChatModel)
            .defaultSystem(ticketClassifierPrompt)
            .defaultAdvisors(List.of(new SimpleLoggerAdvisor()))
            .build();
    }
}
