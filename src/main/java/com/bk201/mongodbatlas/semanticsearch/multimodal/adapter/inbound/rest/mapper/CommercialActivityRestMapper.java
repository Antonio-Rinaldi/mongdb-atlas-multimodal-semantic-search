package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.mapper;

import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.model.CommercialActivityRequest;
import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.model.CommercialActivityResponse;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.CommercialActivity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommercialActivityRestMapper {

    CommercialActivityResponse toRest(CommercialActivity model);
    List<CommercialActivityResponse> toRests(List<CommercialActivity> models);

    CommercialActivity toModel(CommercialActivityRequest rest);
    List<CommercialActivity> toModels(List<CommercialActivityRequest> rest);

}
