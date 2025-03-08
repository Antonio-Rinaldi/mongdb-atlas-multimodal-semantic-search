package com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.outbound;

import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MultimodalSearch;

public interface EmbeddingPort {
    float[] generateEmbedding(MultimodalSearch multimodalSearch);
    <E> float[] generateEmbedding(E commercialActivity);
}
