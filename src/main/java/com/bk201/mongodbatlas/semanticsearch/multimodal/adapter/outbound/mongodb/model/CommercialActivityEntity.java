package com.bk201.mongodbatlas.semanticsearch.multimodal.adapter.outbound.mongodb.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
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
    private float[] embedding;
}
