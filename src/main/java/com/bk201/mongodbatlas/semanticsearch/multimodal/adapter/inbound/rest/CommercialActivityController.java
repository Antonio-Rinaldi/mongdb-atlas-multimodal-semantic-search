package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest;

import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.mapper.CommercialActivityRestMapper;
import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.model.CommercialActivityRequest;
import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.model.CommercialActivityResponse;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.CommercialActivity;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MediaFile;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MultimodalSearch;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.inbound.CommercialActivityApi;
import com.bk201.mongodbatlas.semanticsearch.multimodal.core.service.CommercialActivityService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
           String text,
           List<MultipartFile> mediaFiles,
           int numberOfResults,
           String town
    ) {
        List<MediaFile> media = extractMediaFiles(mediaFiles);
        MultimodalSearch multimodalSearch = MultimodalSearch.builder()
                .text(text)
                .media(media)
                .build();
        List<CommercialActivity> commercialActivities =
                commercialActivityService.findSimilarCommercialActivitiesByTown(
                    multimodalSearch, numberOfResults, town
        );
        List<CommercialActivityResponse> commercialActivityResponses =
                commercialActivityRestMapper.toRests(commercialActivities);
        return ResponseEntity.ok(commercialActivityResponses);
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

