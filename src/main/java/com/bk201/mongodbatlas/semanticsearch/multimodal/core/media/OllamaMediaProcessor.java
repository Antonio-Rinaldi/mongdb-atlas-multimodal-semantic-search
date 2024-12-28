package com.bk201.mongodbatlas.semanticsearch.multimodal.core.media;

import com.bk201.mongodbatlas.semanticsearch.multimodal.configuration.media.OllamaMediaProcessorProperties;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.autoconfigure.ollama.OllamaChatProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.ai.ollama.chat.enabled", havingValue = "true")
public class OllamaMediaProcessor implements MediaProcessor{

    private final OllamaMediaProcessorProperties properties;
    private final ChatClient chatClient;

    public String processMedia(MediaFile mediaFile) {
        ChatClient specificMediaChatClient = getSpecificMediaChatClient(mediaFile.getType());
        return specificMediaChatClient.prompt()
                .user(user -> user
                        .text(getPromptText(mediaFile.getType()))
                        .media(
                            MimeType.valueOf(mediaFile.getType()),
                            new InputStreamResource(mediaFile.getContent())
                        )
                )
                .call()
                .content();
    }

    private ChatClient getSpecificMediaChatClient(String contentType) {
        Optional<OllamaOptions> options = getChatOptions(contentType);
        return properties.isEnabled() && options.isPresent()
                ? chatClient.mutate().defaultOptions(options.get()).build()
                : chatClient;
    }

    private String getPromptText(String contentType) {
        return MediaType.fromContentType(contentType)
                .map(mediaType -> switch (mediaType) {
                    case TEXT -> "Summarize this text trying to keep as much information as possible";
                    case IMAGE -> "Explain accurately what do you see in this image";
                    case AUDIO -> "Transcribe accurately the content of this audio";
                    case VIDEO -> "Describe accurately what do you see in this video";
                })
                .orElse("Describe accurately the content of this file");
    }

    private Optional<OllamaOptions> getChatOptions(String contentType) {
        return MediaType.fromContentType(contentType)
                .map(properties.getMedia()::get)
                .filter(OllamaChatProperties::isEnabled)
                .map(OllamaChatProperties::getOptions);
    }
}
