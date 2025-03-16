package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.mapper;

import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivity;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivityWithEmbeddings;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.entity.CommercialActivityEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommercialActivityEntityMapper {

    CommercialActivityEntity toEntity(CommercialActivity model, float[] embeddings);
    default CommercialActivityEntity toEntity(CommercialActivityWithEmbeddings model) {
        return toEntity(model.commercialActivity(), model.embeddings());
    }
    List<CommercialActivityEntity> toEntities(List<CommercialActivityWithEmbeddings> models);

    CommercialActivity toModel(CommercialActivityEntity entity);

    List<CommercialActivity> toModels(List<CommercialActivityEntity> entities);
}
