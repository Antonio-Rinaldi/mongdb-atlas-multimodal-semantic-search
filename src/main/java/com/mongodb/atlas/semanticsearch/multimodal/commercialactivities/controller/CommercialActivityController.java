package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.controller;

import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.mapper.CommercialActivityRestMapper;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.CommercialActivity;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.MediaFile;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.MultimodalSearch;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.rest.CommercialActivityRequest;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.rest.CommercialActivityResponse;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.service.CommercialActivityService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@RestController
public class CommercialActivityController implements CommercialActivityApi {

    private final CommercialActivityRestMapper commercialActivityRestMapper;
    private final CommercialActivityService commercialActivityService;

    @Override
    public ResponseEntity<Void> createCommercialActivity(CommercialActivityRequest request) {
         CommercialActivity commercialActivity = commercialActivityRestMapper.toModel(request);
         commercialActivityService.saveCommercialActivity(commercialActivity);
         return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> createCommercialActivities(List<CommercialActivityRequest> request) {
        List<CommercialActivity> commercialActivities = commercialActivityRestMapper.toModels(request);
        commercialActivityService.saveCommercialActivities(commercialActivities);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<CommercialActivityResponse>> searchSimilarCommercialActivitiesByTown(
            String town,
            String text,
            List<MultipartFile> mediaFiles,
            int numberOfResults
    ) {
        MultimodalSearch multimodalSearch = buildMultimodalSearch(text, mediaFiles);
        List<CommercialActivity> commercialActivities =
                commercialActivityService.findSimilarCommercialActivitiesByTown(
                        town, multimodalSearch, numberOfResults
        );
        List<CommercialActivityResponse> commercialActivityResponses =
                commercialActivityRestMapper.toRests(commercialActivities);
        return ResponseEntity.ok(commercialActivityResponses);
    }

    @Override
    public ResponseEntity<String> answerUsingSimilarCommercialActivitiesByTown(String town, String text, List<MultipartFile> mediaFiles) {
        MultimodalSearch multimodalSearch = buildMultimodalSearch(text, mediaFiles);
        String response = commercialActivityService.answerUsingSimilarCommercialActivitiesByTown(town, multimodalSearch);
        return ResponseEntity.ok(response);
    }

    private MultimodalSearch buildMultimodalSearch(String text, List<MultipartFile> mediaFiles) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("User prompt cannot be blank!");
        }
        List<MediaFile> media = extractMediaFiles(mediaFiles);
        return MultimodalSearch.builder()
                .text(text)
                .media(media)
                .build();
    }

    private List<MediaFile> extractMediaFiles(List<MultipartFile> mediaFiles) {
        if(Objects.isNull(mediaFiles)) {
            return Collections.emptyList();
        }
        return mediaFiles.stream()
                .map(this::extractMediaFile)
                .toList();
    }

    @SneakyThrows
    private MediaFile extractMediaFile(MultipartFile file) {
        return MediaFile.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .content(file.getInputStream())
                .size(file.getSize())
                .build();
    }
}

