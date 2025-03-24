package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.service;

import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.chat.MultimodalChat;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.chat.RagChat;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.embedding.EmbeddingCalculator;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivity;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivityWithEmbeddings;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.MultimodalSearch;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.repository.CommercialActivityDatabase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommercialActivityService {

    private final MultimodalChat multimodalChat;
    private final EmbeddingCalculator embeddingCalculator;
    private final RagChat ragChat;
    private final CommercialActivityDatabase commercialActivityDatabase;

    public CommercialActivity saveCommercialActivity(CommercialActivity commercialActivity) {
        float[] embeddings = embeddingCalculator.generateEmbedding(commercialActivity);
        return commercialActivityDatabase.saveCommercialActivity(commercialActivity, embeddings);
    }

    public List<CommercialActivity> saveCommercialActivities(List<CommercialActivity> commercialActivities) {
        List<CommercialActivityWithEmbeddings> commercialActivitiesWithEmbeddings = commercialActivities.stream()
                .map(commercialActivity -> new CommercialActivityWithEmbeddings(
                        commercialActivity,
                        embeddingCalculator.generateEmbedding(commercialActivity)
                ))
                .toList();
        return commercialActivityDatabase.saveCommercialActivities(commercialActivitiesWithEmbeddings);
    }

    public List<CommercialActivity> findSimilarCommercialActivitiesByTown(
            String town,
            MultimodalSearch multimodalSearch,
            int numberOfResults
    ) {
        String multimodalUserInput = getMultimodalUserInput(multimodalSearch);
        float[] embeddings = embeddingCalculator.generateEmbedding(multimodalUserInput);
        return commercialActivityDatabase.findSimilarCommercialActivitiesByTown(town, embeddings, numberOfResults);
    }

    public Flux<String> answerUsingSimilarCommercialActivitiesByTown(String town, MultimodalSearch multimodalSearch) {
        String multimodalUserInput = getMultimodalUserInput(multimodalSearch);
        float[] embeddings = embeddingCalculator.generateEmbedding(multimodalUserInput);
        List<CommercialActivity> commercialActivities =
                commercialActivityDatabase.findSimilarCommercialActivitiesByTown(town, embeddings, 20);
        return ragChat.exchange(multimodalUserInput, commercialActivities);
    }

    private String getMultimodalUserInput(MultimodalSearch multimodalSearch) {
        StringBuilder multimodalUserInput = new StringBuilder();
        // Handle text input if present
        multimodalUserInput.append(multimodalSearch.getText());
        multimodalUserInput.append("\n\n");
        // Handle media content
        Flux.fromStream(multimodalSearch.getMedia()::stream)
            .concatMap(media -> multimodalChat.exchange(multimodalSearch.getText(), media))
            .toStream()
            .forEach(multimodalUserInput::append);
        return multimodalUserInput.toString();
    }
}
