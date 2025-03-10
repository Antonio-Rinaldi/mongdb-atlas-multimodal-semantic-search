// Create vector search index for MongoDB Atlas
db.getCollection('commercial-activities').createSearchIndex(
  {
    name: "vector_index",
    definition: {
      fields: [
        {
          type: "vector",
          path: "embeddings",
          numDimensions: 1024,
          similarity: "euclidean | cosine | dotProduct",
          quantization: "none | scalar | binary"
        },
        {
          type: "filter",
          path: "town"
        }
      ]
    }
  }
);