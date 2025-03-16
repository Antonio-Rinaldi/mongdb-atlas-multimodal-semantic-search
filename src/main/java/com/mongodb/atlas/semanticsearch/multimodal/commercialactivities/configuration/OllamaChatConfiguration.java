package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OllamaChatConfiguration {

    private final OllamaChatProperties ollamaChatProperties;
    private final OllamaChatModel ollamaChatModel;

    @Bean
    public ChatClient multimodalChatClient() {
        return ChatClient
                .builder(ollamaChatModel)
                .defaultOptions(ollamaChatProperties.getMultimodal())
                .build();
    }

    @Bean
    public ChatClient ragChatClient() {
        return ChatClient
                .builder(ollamaChatModel)
                .defaultOptions(ollamaChatProperties.getRag())
                .build();
    }
}
