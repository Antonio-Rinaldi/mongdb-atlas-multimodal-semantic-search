package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.inbound.rest.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CommercialActivityResponse {

    private String taxCode;
    private String name;
    private String town;
    private String address;
    private List<String> categories;
    private String description;
    private double score;
}
