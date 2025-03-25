#!/bin/bash

set -e

mongosh "mongodb://localhost:27017/multimodal-semantic-vector-search" \
  --eval '
  print("Checking if collection commercial-activities exists...");
  const collections = db.getCollectionNames();
  if (!collections.includes("commercial-activities")) {
    print("Collection commercial-activities does not exist. Creating it...");
    db.createCollection("commercial-activities");
    print("Collection commercial-activities created successfully");
  } else {
    print("Collection commercial-activities already exists");
  }
  
  print("Checking if vector search index exists...");
  const indexes = db.getCollection("commercial-activities").getSearchIndexes("embeddings_town_vector_index");
  
  if (indexes.length > 0) {
    print("Vector search index embeddings_town_vector_index already exists");
  } else {
    print("Creating vector search index...");
    db.getCollection("commercial-activities").createSearchIndex(
      "embeddings_town_vector_index",
      "vectorSearch",
      {
        "fields": [
          {
            "type": "vector",
            "path": "embeddings",
            "numDimensions": 1024,
            "similarity": "cosine",
            "quantization": "none"
          },
          {
            "type": "filter",
            "path": "town"
          }
        ]
      }
    );
    print("Vector search index created successfully");
  }'
