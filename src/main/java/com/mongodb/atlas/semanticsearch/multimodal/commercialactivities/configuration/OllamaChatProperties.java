package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.configuration;

import lombok.Data;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("spring.ai.ollama.chat")
public class OllamaChatProperties {

    private OllamaOptions multimodal;
    private OllamaOptions rag;
}
