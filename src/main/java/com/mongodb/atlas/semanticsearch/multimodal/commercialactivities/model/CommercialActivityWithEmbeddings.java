package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model;

public record CommercialActivityWithEmbeddings(CommercialActivity commercialActivity, float[] embeddings) {}
