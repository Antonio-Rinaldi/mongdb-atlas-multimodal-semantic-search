package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.repository;


import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.mapper.CommercialActivityEntityMapper;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivity;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivityWithEmbeddings;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.entity.CommercialActivityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommercialActivityMongodb implements CommercialActivityDatabase {

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
    public List<CommercialActivity> saveCommercialActivities(List<CommercialActivityWithEmbeddings> commercialActivities) {
        List<CommercialActivityEntity> commercialActivityEntities =
                commercialActivityEntityMapper.toEntities(commercialActivities);
        List<CommercialActivityEntity> savedCommercialActivityEntity =
                commercialActivityRepository.saveAll(commercialActivityEntities);
        return commercialActivityEntityMapper.toModels(savedCommercialActivityEntity);
    }


    @Override
    public List<CommercialActivity> findSimilarCommercialActivitiesByTown(
            String town,
            float[] embeddings,
            int numberOfResults
    ) {
        List<CommercialActivityEntity> commercialActivityEntities =
                commercialActivityRepository.findSimilarByTown(embeddings, numberOfResults, town);
        return commercialActivityEntityMapper.toModels(commercialActivityEntities);
    }
}
