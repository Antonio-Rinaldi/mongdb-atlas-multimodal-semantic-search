package com.bk201.mongodbatlas.semanticsearch.multimodal.core.service;

import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.CommercialActivity;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MultimodalSearch;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.outbound.CommercialActivityDatabasePort;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.outbound.CommercialActivityEmbeddingPort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommercialActivityService {

    private final CommercialActivityEmbeddingPort commercialActivityEmbeddingPort;
    private final CommercialActivityDatabasePort commercialActivityDatabasePort;

    public CommercialActivity saveCommercialActivity(CommercialActivity commercialActivity) {
        float[] embeddings = commercialActivityEmbeddingPort.generateEmbedding(commercialActivity);
        return commercialActivityDatabasePort.saveCommercialActivity(commercialActivity, embeddings);
    }

    public List<CommercialActivity> saveCommercialActivities(List<CommercialActivity> commercialActivities) {
        List<Pair<CommercialActivity, float[]>> commercialActivitiesWithEmbeddings = commercialActivities.stream()
                .map(commercialActivity -> Pair.of(
                        commercialActivity,
                        commercialActivityEmbeddingPort.generateEmbedding(commercialActivity)
                ))
                .toList();
        return commercialActivityDatabasePort.saveCommercialActivities(commercialActivitiesWithEmbeddings);
    }

    public List<CommercialActivity> findSimilarCommercialActivitiesByTownAndCategoriesIn(
            MultimodalSearch multimodalSearch,
            int numberOfResults,
            String town,
            List<String> categories
    ) {
        float[] embeddings = commercialActivityEmbeddingPort.generateEmbedding(multimodalSearch);
        return commercialActivityDatabasePort.findCommercialActivitiesSimilarByTownAndCategoryIn(
                embeddings, numberOfResults, town, categories
        );
    }
}
