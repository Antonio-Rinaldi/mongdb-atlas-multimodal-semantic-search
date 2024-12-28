package com.bk201.mongodbatlas.semanticsearch.multimodal.configuration.mongodb;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDbAtlasVectorStoreConfiguration {

    @Bean
    MongoDBAtlasVectorStore vectorStore(
            MongoTemplate mongoTemplate,
            EmbeddingModel embeddingModel,
            MongoDbAtlasVectorStoreProperties properties,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<VectorStoreObservationConvention> customObservationConvention,
            BatchingStrategy batchingStrategy
    ) {
       return MongoDbAtlasVectorStore.builder()
                .mongoTemplate(mongoTemplate)
                .embeddingModel(embeddingModel)
                .properties(properties)
                .observationRegistry(observationRegistry)
                .customObservationConvention(customObservationConvention)
                .batchingStrategy(batchingStrategy)
                .build();
    }
}
