package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.embedding;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EmbeddingCalculator {

    private final ObjectMapper jsonMapper;
    private final EmbeddingModel embeddingModel;

    public float[] generateEmbedding(String prompt) {
        return embeddingModel.embed(prompt);
    }

    @SneakyThrows
    public float[] generateEmbedding(Object object) {
        String json = jsonMapper.writeValueAsString(object);
        return embeddingModel.embed(json);
    }
}
