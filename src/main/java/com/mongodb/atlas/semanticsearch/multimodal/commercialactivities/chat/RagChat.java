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

    public Flux<String> exchange(String userPrompt, List<?> objects) {
        return ragChatClient.prompt()
                .system(buildSystemPrompt(objects))
                .user(userPrompt)
                .stream()
                .content();
    }

    @SneakyThrows
    private String buildSystemPrompt(List<?> objects) {
        StringBuilder systemPrompt = new StringBuilder("You are a helpful chatbot.\n");
        systemPrompt.append("You always give detailed and professional answers.\n");
        if (objects.isEmpty()) {
            systemPrompt.append("You don't have any additional context on the subject requested by the user.\n");
            systemPrompt.append("You can answer to user input only using what you already know.\n");
            systemPrompt.append("Don't make up any new information if you don't know what to answer.\n");
            return systemPrompt.toString();
        }
        systemPrompt.append("Use the following pieces of information along with your own knowledge to elaborate and answer the question.\n");
        for (Object object: objects) {
            String json = jsonMapper.writeValueAsString(object);
            systemPrompt.append(json);
            systemPrompt.append("\n");
        }
        systemPrompt.append("Don't make up any new information if you don't know what to answer.\n");
        return systemPrompt.toString();
    }
}
