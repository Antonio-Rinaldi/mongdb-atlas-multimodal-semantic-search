package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "commercial-activities")
public class CommercialActivityEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String taxCode;
    private String name;
    private String town;
    private String address;
    private List<String> categories;
    private String description;
    private double score;
    private float[] embeddings;
}
