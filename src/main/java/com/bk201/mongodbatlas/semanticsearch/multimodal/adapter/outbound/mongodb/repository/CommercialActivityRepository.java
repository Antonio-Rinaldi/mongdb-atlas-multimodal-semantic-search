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
                    "path": "embedding",
                    "queryVector": ?0,
                    "numCandidates": 4096,
                    "limit": ?1,
                    "filter": {
                        $and: [
                            {
                                town:  ?2
                            },
                            {
                                categories: { $in: ?3 }
                            }
                        ]
                    }
                }
            }""",
            """
            {
                $project: {
                    _id: 0,
                    taxCode: 1,
                    name: 1,
                    town: 1,
                    address: 1,
                    categories: 1,
                    description: 1,
                    score: { $meta: "vectorSearchScore" }
                }
            }
            """
    })
    List<CommercialActivityEntity> findSimilarByTownAndCategoryIn(
            float[] embedding,
            int limit,
            String town,
            List<String> category
    );
}
