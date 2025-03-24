#!/bin/bash

set -e

echo "Creating vector search index..."

mongosh "mongodb://localhost:27017/mongodb-atlas-multimodal-semantic-vector-search" \
 --eval 'db.getCollection("commercial-activities").createSearchIndex(
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
)'

echo "Vector search index created successfully"
