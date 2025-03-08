package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.embedding;

import com.bk201.mongodbatlas.semanticsearch.multimodal.core.media.MediaProcessor;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MultimodalSearch;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.outbound.EmbeddingPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmbeddingAdapter implements EmbeddingPort {

    private final ObjectMapper jsonMapper;
    private final EmbeddingModel embeddingModel;
    private final MediaProcessor mediaProcessor;

    public float[] generateEmbedding(MultimodalSearch multimodalSearch) {
        StringBuilder combinedInput = new StringBuilder();
    
        // Handle text input if present
        if (StringUtils.isNotBlank(multimodalSearch.getText())) {
            combinedInput.append(multimodalSearch.getText());
            combinedInput.append("\n");
        }
    
        // Handle media content
        multimodalSearch.getMedia().stream()
                .map(mediaProcessor::processMedia)
                .forEach(media -> {
                    combinedInput.append(media);
                    combinedInput.append("\n");
                });
    
        // Generate embedding from combined text representation
        return embeddingModel.embed(combinedInput.toString());
    }

    @Override
    @SneakyThrows
    public <E> float[] generateEmbedding(E entity) {
        String json = jsonMapper.writeValueAsString(entity);
        return embeddingModel.embed(json);
    }
}
