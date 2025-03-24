package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RagChat {

    private final ObjectMapper jsonMapper;
    private final ChatClient ragChatClient;

    @SneakyThrows
    public Flux<String> exchange(String userPrompt, List<?> objects) {
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("You are a helpful chatbot.\n");
        systemPrompt.append("Use only the following pieces of context to answer the question.\n");
        systemPrompt.append("Don't make up any new information:\n");

        for (Object object: objects) {
            String json = jsonMapper.writeValueAsString(object);
            systemPrompt.append(json);
            systemPrompt.append("\n");
        }
        return ragChatClient.prompt()
                .system(systemPrompt.toString())
                .user(userPrompt)
                .stream()
                .content();
    }
}
