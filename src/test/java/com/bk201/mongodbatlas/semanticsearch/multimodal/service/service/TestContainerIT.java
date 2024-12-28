package com.bk201.mongodbatlas.semanticsearch.multimodal.service.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.SearchIndexModel;
import com.mongodb.client.model.SearchIndexType;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DirtiesContext
@Testcontainers
public abstract class TestContainerIT {

    private static final String MONGODB_IMAGE = "mongodb/mongodb-atlas-local";
    private static final String VECTOR_SEARCH_INDEX_NAME = "vector_index";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(
            DockerImageName.parse(MONGODB_IMAGE).asCompatibleSubstituteFor("mongo")
    )
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl());
    }

    protected void createVectorSearchIndex(MongoCollection<Document> documentCollection) {
        // Create vector search index
        Bson vectorIndexDefinition = new Document(
                "fields",
                List.of(
                        new Document("type", "vector")
                                .append("path", "embedding")
                                .append("numDimensions", 1536)
                                .append("similarity", "cosine")
                )
        );

        // Create vector search index
        SearchIndexModel vectorSearchIndex = new SearchIndexModel(
                VECTOR_SEARCH_INDEX_NAME, vectorIndexDefinition, SearchIndexType.vectorSearch()
        );
        List<SearchIndexModel> searchIndexes = List.of(vectorSearchIndex);
        documentCollection.createSearchIndexes(searchIndexes);


        while (!isVectorSearchIndexReady(documentCollection)) {
            log.debug("Waiting for vector search index to be ready...");
        }
        log.info("Vector search index is ready");
    }

    protected void deleteVectorSearchIndex(MongoCollection<Document> documentCollection) {
        documentCollection.dropIndex(VECTOR_SEARCH_INDEX_NAME);
    }

    private boolean isVectorSearchIndexReady(MongoCollection<Document> documentCollection) {
        return documentCollection.listIndexes().into(new ArrayList<>()).stream()
                .anyMatch(index -> VECTOR_SEARCH_INDEX_NAME.equals(index.get("name")));
    }
}
