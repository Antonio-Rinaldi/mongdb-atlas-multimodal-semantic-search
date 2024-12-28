package com.bk201.mongodbatlas.semanticsearch.multimodal.core.port.inbound;

import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.model.CommercialActivityRequest;
import com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.model.CommercialActivityResponse;
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

    @Operation(summary = "Search similar commercial activities by town and categories in")
    @PostMapping(
            value = "/search",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<CommercialActivityResponse>> searchSimilarCommercialActivitiesByTownAndCategoriesIn(
            @RequestPart(value = "text") String text,
            @RequestPart(value = "media", required = false) List<MultipartFile> mediaFiles,
            @RequestParam(value = "numberOfResults", defaultValue = "10") int numberOfResults,
            @RequestParam(value = "town") String town,
            @RequestParam(value = "categories") List<String> categories
    );
}
