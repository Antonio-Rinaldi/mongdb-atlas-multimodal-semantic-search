package com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.outbound;

import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.CommercialActivity;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MultimodalSearch;

public interface CommercialActivityEmbeddingPort {
    float[] generateEmbedding(MultimodalSearch multimodalSearch);
    float[] generateEmbedding(CommercialActivity commercialActivity);
}
