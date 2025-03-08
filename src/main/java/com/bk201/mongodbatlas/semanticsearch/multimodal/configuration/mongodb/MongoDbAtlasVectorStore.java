package com.bk201.mongodbatlas.semanticsearch.multimodal.configuration.mongodb;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.SearchIndexModel;
import com.mongodb.client.model.SearchIndexType;
import io.micrometer.observation.ObservationRegistry;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class MongoDbAtlasVectorStore extends MongoDBAtlasVectorStore {

    private static final int INDEX_ALREADY_EXISTS_ERROR_CODE = 68;
    private static final String INDEX_ALREADY_EXISTS_ERROR_CODE_NAME = "IndexAlreadyExists";

    public static final String VECTOR_SEARCH_INDEX_DEFINITION_FIELDS_KEY = "fields";
    public static final String VECTOR_SEARCH_INDEX_DEFINITION_TYPE_KEY = "type";
    public static final String VECTOR_SEARCH_INDEX_DEFINITION_TYPE_VALUE_VECTOR = "vector";
    public static final String VECTOR_SEARCH_INDEX_DEFINITION_TYPE_VALUE_FILTER = "filter";
    public static final String VECTOR_SEARCH_INDEX_DEFINITION_PATH_KEY = "path";
    public static final String VECTOR_SEARCH_INDEX_DEFINITION_NUM_DIMENSIONS_KEY = "numDimensions";
    public static final String VECTOR_SEARCH_INDEX_DEFINITION_SIMILARITY_KEY = "similarity";
    public static final String VECTOR_SEARCH_INDEX_DEFINITION_QUANTIZATION_KEY = "quantization";

    private final MongoTemplate mongoTemplate;
    private final boolean initializeSchema;
    private final String collectionName;
    private final String indexName;
    private final String pathName;
    private final Integer numDimensions;
    private final MongoDbAtlasVectorStoreProperties.Similarity similarity;
    private final MongoDbAtlasVectorStoreProperties.Quantization quantization;
    private final List<String> fieldsToFilter;
    private final List<String> metadataFieldsToFilter;


    @lombok.Builder
    protected MongoDbAtlasVectorStore(
            MongoTemplate mongoTemplate,
            EmbeddingModel embeddingModel,
            MongoDbAtlasVectorStoreProperties properties,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<VectorStoreObservationConvention> customObservationConvention,
            BatchingStrategy batchingStrategy) {

        super(createBuilder(
                mongoTemplate,
                embeddingModel,
                observationRegistry,
                customObservationConvention,
                batchingStrategy
        ));
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        Assert.notNull(properties.getCollectionName(), "Collection name must not be null");

        this.mongoTemplate = mongoTemplate;
        this.initializeSchema = properties.isInitializeSchema();
        this.collectionName = properties.getCollectionName();
        this.indexName = Objects.nonNull(properties.getIndexName())
                ? properties.getIndexName()
                : "vector_index";
        this.pathName = Objects.nonNull(properties.getPathName())
                ? properties.getPathName()
                : "embeddings";
        this.numDimensions = Objects.nonNull(properties.getNumDimensions())
                ? properties.getNumDimensions()
                : 1536;
        this.similarity = Objects.nonNull(properties.getSimilarity())
                ? properties.getSimilarity()
                : MongoDbAtlasVectorStoreProperties.Similarity.EUCLIDEAN;
        this.quantization = Objects.nonNull(properties.getQuantization())
                ? properties.getQuantization()
                : MongoDbAtlasVectorStoreProperties.Quantization.NONE;
        this.fieldsToFilter = Objects.nonNull(properties.getFieldsToFilter())
                ? properties.getFieldsToFilter()
                : Collections.emptyList();
        this.metadataFieldsToFilter = Objects.nonNull(properties.getMetadataFieldsToFilter())
                ? properties.getMetadataFieldsToFilter()
                : Collections.emptyList();
    }

    @Override
    public void afterPropertiesSet() {
        if (!this.initializeSchema) {
            return;
        }

        // Create the collection if it does not exist
        if (!this.mongoTemplate.collectionExists(this.collectionName)) {
            this.mongoTemplate.createCollection(this.collectionName);
        }
        // Create search index
        createSearchIndex();
    }

    private void createSearchIndex() {
        try {
            SearchIndexModel vectorSearchIndex = createVectorSearchIndex();
            MongoCollection<Document> collection = this.mongoTemplate.getCollection(this.collectionName);
            // TODO: understand
            // if(StreamSupport.stream(collection.listSearchIndexes(SearchIndexModel.class).spliterator(), false)
            //   .anyMatch(index -> index.getName().equals(vectorSearchIndex.getName()))) {
            //     collection.updateSearchIndex(vectorSearchIndex);
            // }
            collection.createSearchIndexes(List.of(vectorSearchIndex));

        } catch (UncategorizedMongoDbException exception) {
            Throwable cause = exception.getCause();
            if (
                    cause instanceof MongoCommandException commandException
                    && (INDEX_ALREADY_EXISTS_ERROR_CODE == commandException.getCode()
                    || INDEX_ALREADY_EXISTS_ERROR_CODE_NAME.equals(commandException.getErrorCodeName()))
            ) {
                    return;
            }

            throw exception;
        }
    }

    private SearchIndexModel createVectorSearchIndex() {
        Document vectorIndexDefinition = new Document()
                .append(VECTOR_SEARCH_INDEX_DEFINITION_TYPE_KEY, VECTOR_SEARCH_INDEX_DEFINITION_TYPE_VALUE_VECTOR)
                .append(VECTOR_SEARCH_INDEX_DEFINITION_PATH_KEY, this.pathName)
                .append(VECTOR_SEARCH_INDEX_DEFINITION_NUM_DIMENSIONS_KEY, this.numDimensions)
                .append(VECTOR_SEARCH_INDEX_DEFINITION_SIMILARITY_KEY, this.similarity.getValue())
                .append(VECTOR_SEARCH_INDEX_DEFINITION_QUANTIZATION_KEY, this.quantization.getValue());

        Stream<Document> filterIndexDefinitions = this.fieldsToFilter.stream()
                .map(fieldName -> new Document()
                        .append(VECTOR_SEARCH_INDEX_DEFINITION_TYPE_KEY, VECTOR_SEARCH_INDEX_DEFINITION_TYPE_VALUE_FILTER)
                        .append(VECTOR_SEARCH_INDEX_DEFINITION_PATH_KEY, fieldName));

        Stream<Document> metadataFilterIndexDefinitions = this.metadataFieldsToFilter.stream()
                .map(fieldName -> new Document()
                        .append(VECTOR_SEARCH_INDEX_DEFINITION_TYPE_KEY, VECTOR_SEARCH_INDEX_DEFINITION_TYPE_VALUE_FILTER)
                        .append(VECTOR_SEARCH_INDEX_DEFINITION_PATH_KEY, "metadata." + fieldName));

        Bson vectorSearchIndexDefinition = new Document(
                VECTOR_SEARCH_INDEX_DEFINITION_FIELDS_KEY,
                Stream.of(
                        Stream.of(vectorIndexDefinition),
                        filterIndexDefinitions,
                        metadataFilterIndexDefinitions
                )
                        .flatMap(Function.identity())
                        .toList()
        );
        return new SearchIndexModel(
                this.indexName,
                vectorSearchIndexDefinition,
                SearchIndexType.vectorSearch()
        );
    }

    private static MongoDBAtlasVectorStore.Builder createBuilder(
            MongoTemplate mongoTemplate,
            EmbeddingModel embeddingModel,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<VectorStoreObservationConvention> customObservationConvention,
            BatchingStrategy batchingStrategy
    ) {
       return MongoDBAtlasVectorStore.builder(mongoTemplate, embeddingModel)
                .observationRegistry(observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP))
                .customObservationConvention(customObservationConvention.getIfAvailable(() -> null))
                .batchingStrategy(batchingStrategy);
    }
}
