package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb;

import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb.mapper.CommercialActivityEntityMapper;
import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb.model.CommercialActivityEntity;
import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb.repository.CommercialActivityRepository;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.CommercialActivity;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.outbound.CommercialActivityDatabasePort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommercialActivityMongodbAdapter implements CommercialActivityDatabasePort {

    private final CommercialActivityEntityMapper commercialActivityEntityMapper;
    private final CommercialActivityRepository commercialActivityRepository;

    @Override
    public CommercialActivity saveCommercialActivity(CommercialActivity commercialActivity, float[] embeddings) {
        CommercialActivityEntity commercialActivityEntity =
                commercialActivityEntityMapper.toEntity(commercialActivity, embeddings);
        CommercialActivityEntity savedCommercialActivityEntity =
                commercialActivityRepository.save(commercialActivityEntity);
        return commercialActivityEntityMapper.toModel(savedCommercialActivityEntity);
    }

    @Override
    public List<CommercialActivity> saveCommercialActivities(List<Pair<CommercialActivity, float[]>> commercialActivities) {
        List<CommercialActivityEntity> commercialActivityEntities =
                commercialActivityEntityMapper.toEntities(commercialActivities);
        List<CommercialActivityEntity> savedCommercialActivityEntity =
                commercialActivityRepository.saveAll(commercialActivityEntities);
        return commercialActivityEntityMapper.toModels(savedCommercialActivityEntity);
    }


    @Override
    public List<CommercialActivity> findCommercialActivitiesSimilarByTown(
            float[] embeddings,
            int numberOfResults,
            String town
    ) {
        List<CommercialActivityEntity> commercialActivityEntities =
                commercialActivityRepository.findSimilarByTown(embeddings, numberOfResults, town);
        return commercialActivityEntityMapper.toModels(commercialActivityEntities);
    }
}
