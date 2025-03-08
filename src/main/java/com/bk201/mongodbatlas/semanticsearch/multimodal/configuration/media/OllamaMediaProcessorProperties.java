package com.bk201.mongodbatlas.semanticsearch.multimodal.configuration.media;

import com.bk201.mongodbatlas.semanticsearch.multimodal.core.media.MediaType;
import lombok.Data;
import org.springframework.ai.autoconfigure.ollama.OllamaChatProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.ai.ollama.chat.multimodal")
public class OllamaMediaProcessorProperties {

    private boolean enabled;
    private Map<MediaType, OllamaChatProperties> media = new EnumMap<>(MediaType.class);
}
