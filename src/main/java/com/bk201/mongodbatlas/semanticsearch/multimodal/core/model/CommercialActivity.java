package com.bk201.mongodbatlas.semanticsearch.multimodal.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CommercialActivity {
    private String id;
    private String taxCode;
    private String name;
    private String town;
    private String address;
    private List<String> categories;
    private String description;
    private double score;
}
