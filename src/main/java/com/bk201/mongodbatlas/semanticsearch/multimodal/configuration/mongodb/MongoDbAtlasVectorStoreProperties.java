package com.bk201.mongodbatlas.semanticsearch.multimodal.configuration.mongodb;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.autoconfigure.vectorstore.mongo.MongoDBAtlasVectorStoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(MongoDBAtlasVectorStoreProperties.CONFIG_PREFIX)
@EqualsAndHashCode(callSuper = true)
public class MongoDbAtlasVectorStoreProperties extends MongoDBAtlasVectorStoreProperties {

    private Integer numDimensions;
    private Similarity similarity;
    private Quantization quantization;
    private List<String> fieldsToFilter;

    @Getter
    @RequiredArgsConstructor
    public enum Similarity {
        EUCLIDEAN("euclidean"),
        COSINE("cosine"),
        DOT_PRODUCT("dotProduct");

        @JsonValue
        private final String value;

        @Override
        public String toString() {
            return this.value;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum Quantization {
        BINARY("binary"),
        NONE("none"),
        SCALAR("scalar");

        @JsonValue
        private final String value;

        @Override
        public String toString() {
            return this.value;
        }
    }
}
