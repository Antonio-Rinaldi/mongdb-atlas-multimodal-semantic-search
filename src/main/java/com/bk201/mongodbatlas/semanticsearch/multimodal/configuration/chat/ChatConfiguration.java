package com.bk201.mongodbatlas.semanticsearch.multimodal.configuration.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatConfiguration {

    private final ChatModel chatModel;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.create(chatModel);
    }
}
