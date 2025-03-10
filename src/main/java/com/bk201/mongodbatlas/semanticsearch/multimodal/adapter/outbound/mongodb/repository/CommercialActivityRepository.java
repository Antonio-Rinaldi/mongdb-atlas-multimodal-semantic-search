package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb.repository;

import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb.model.CommercialActivityEntity;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommercialActivityRepository extends MongoRepository<CommercialActivityEntity, String> {

    // TODO: Vedere se ha senso aggiungere paginazione
    // TODO: Aggiungere posizione geo spaziale per calcolare distanza
    @Aggregation(pipeline = {
            // Vector search operator must be the first operator
            """
            {
                $vectorSearch: {
                    "index": "vector_index",
                    "path": "embeddings",
                    "queryVector": ?0,
                    "numCandidates": 4096,
                    "limit": ?1,
                    "filter": {
                        $and: [
                            {
                                town:  ?2
                            }
                        ]
                    }
                }
            }""",
            """
            {
                $project: {
                    _id: 0,
                    embeddings: 0,
                    score: { $meta: "vectorSearchScore" }
                }
            }
            """
    })
    List<CommercialActivityEntity> findSimilarByTown(
            float[] embeddings,
            int limit,
            String town
    );
}
