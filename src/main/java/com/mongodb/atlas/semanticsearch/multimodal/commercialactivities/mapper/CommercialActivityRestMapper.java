package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.mapper;

import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivity;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.rest.CommercialActivityRequest;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.rest.CommercialActivityResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommercialActivityRestMapper {

    CommercialActivityResponse toRest(CommercialActivity model);
    List<CommercialActivityResponse> toRests(List<CommercialActivity> models);

    CommercialActivity toModel(CommercialActivityRequest rest);
    List<CommercialActivity> toModels(List<CommercialActivityRequest> rest);

}
