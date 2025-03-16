package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.controller;

import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.rest.CommercialActivityRequest;
import com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.rest.CommercialActivityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@Tag(name = "Commercial Activity API V1")
@RequestMapping("/api/v1/commercial-activities")
public interface CommercialActivityApi {

    @Operation(summary = "Create a new commercial activity")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> createCommercialActivity(@RequestBody CommercialActivityRequest request);

    @Operation(summary = "Create multiple commercial activity")
    @PostMapping(value = "/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> createCommercialActivities(@RequestBody List<CommercialActivityRequest> request);

    @Operation(summary = "Search similar commercial activities by town")
    @PostMapping(
            value = "/search",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<CommercialActivityResponse>> searchSimilarCommercialActivitiesByTown(
            @RequestParam(value = "town") String town,
            @RequestPart(value = "text") String text,
            @RequestPart(value = "media", required = false) List<MultipartFile> mediaFiles,
            @RequestParam(value = "numberOfResults", defaultValue = "10") int numberOfResults
    );

    @Operation(summary = "Ask for information about commercial activities")
    @PostMapping(
            value = "/rag",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<String> answerUsingSimilarCommercialActivitiesByTown(
            @RequestParam(value = "town") String town,
            @RequestPart(value = "text") String text,
            @RequestPart(value = "media", required = false) List<MultipartFile> mediaFiles
    );
}
