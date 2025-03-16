package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.rest;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CommercialActivityRequest {

    private String taxCode;
    private String name;
    private String town;
    private String address;
    private List<String> categories;
    private String description;
}
