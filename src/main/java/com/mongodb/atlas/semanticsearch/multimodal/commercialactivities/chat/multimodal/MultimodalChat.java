package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.chat.multimodal;

import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.MediaFile;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class MultimodalChat {

    private final ChatClient multimodalChatClient;

    public Flux<String> exchange(String userPrompt, MediaFile mediaFile) {
        Flux<String> response = multimodalChatClient.prompt()
                .system(getSystemPrompt(mediaFile.getType()))
                .user(user -> user
                        .text(userPrompt)
                        .media(
                            MimeType.valueOf(mediaFile.getType()),
                            new InputStreamResource(mediaFile.getContent())
                        ))
                .stream()
                .content();
        return Flux.concat(response, Flux.just("\n\n"));
    }

    private String getSystemPrompt(String contentType) {
        return MediaType.fromContentType(contentType)
                .map(mediaType -> switch (mediaType) {
                    case TEXT -> "Summarize this text trying to keep as much information as possible:";
                    case IMAGE -> "Explain accurately what do you see in this image:";
                    case AUDIO -> "Transcribe accurately the content of this audio:";
                    case VIDEO -> "Describe accurately what do you see in this video:";
                })
                .orElse("Describe accurately the content of this file:");
    }
}
