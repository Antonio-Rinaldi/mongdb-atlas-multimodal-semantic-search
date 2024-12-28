package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.embedding;

import com.bk201.mongodbatlas.semanticsearch.multimodal.core.media.MediaProcessor;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.CommercialActivity;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MultimodalSearch;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.outbound.CommercialActivityEmbeddingPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommercialActivityEmbeddingAdapter implements CommercialActivityEmbeddingPort {

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
    public float[] generateEmbedding(CommercialActivity commercialActivity) {
        try {
            String json = jsonMapper.writeValueAsString(commercialActivity);
            return embeddingModel.embed(json);

        } catch (JsonProcessingException exception) {
            String errorMessage = String.format(
                    "Failed to serialize commercial activity to json %s while calculating embeddings.",
                    commercialActivity.getTaxCode()
            );
            throw new RuntimeException(errorMessage, exception);
        }
    }
}
