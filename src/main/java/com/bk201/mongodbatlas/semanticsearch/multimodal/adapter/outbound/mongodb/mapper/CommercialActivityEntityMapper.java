package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb.mapper;

import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb.model.CommercialActivityEntity;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.CommercialActivity;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommercialActivityEntityMapper {

    CommercialActivityEntity toEntity(CommercialActivity model, float[] embeddings);
    default CommercialActivityEntity toEntity(Pair<CommercialActivity, float[]> model) {
        return toEntity(model.getLeft(), model.getRight());
    }
    List<CommercialActivityEntity> toEntities(List<Pair<CommercialActivity, float[]>> models);

    CommercialActivity toModel(CommercialActivityEntity entity);

    List<CommercialActivity> toModels(List<CommercialActivityEntity> entities);
}
