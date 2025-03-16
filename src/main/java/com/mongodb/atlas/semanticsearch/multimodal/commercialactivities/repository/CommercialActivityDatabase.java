package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.repository;

import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivity;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivityWithEmbeddings;

import java.util.List;

public interface CommercialActivityDatabase {

    CommercialActivity saveCommercialActivity(CommercialActivity commercialActivity, float[] embeddings);

    List<CommercialActivity> saveCommercialActivities(List<CommercialActivityWithEmbeddings> commercialActivities);

    List<CommercialActivity> findSimilarCommercialActivitiesByTown(String town, float[] embeddings, int numberOfResults);
}
