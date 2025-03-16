package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommercialActivity {
    @JsonIgnore
    private String id;
    private String taxCode;
    private String name;
    private String town;
    private String address;
    private List<String> categories;
    private String description;
    private double score;
}
