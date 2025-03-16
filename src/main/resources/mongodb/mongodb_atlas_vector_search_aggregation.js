db.getCollection('commercial-activities').aggregate([
    {
        $vectorSearch: {
            index: "embeddings_town_vector_index",
            path: "embeddings",
            queryVector: [],
            numCandidates: 4096,
            limit: 100,
            filter: {
                $and: [
                    {
                        town:  "<town>"
                    }
                ]
            }
        }
    },
    {
        $project: {
            _id: 0,
            embeddings: 0,
            score: {
                $meta: "vectorSearchScore"
            }
        }
    }
])
