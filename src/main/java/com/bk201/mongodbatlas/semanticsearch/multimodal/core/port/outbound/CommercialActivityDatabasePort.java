package com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.outbound;

import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.CommercialActivity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface CommercialActivityDatabasePort {

    CommercialActivity saveCommercialActivity(CommercialActivity commercialActivity, float[] embeddings);

    List<CommercialActivity> saveCommercialActivities(List<Pair<CommercialActivity, float[]>> commercialActivities);

    List<CommercialActivity> findCommercialActivitiesSimilarByTown(
            float[] embeddings,
            int numberOfResults,
            String town
    );
}
