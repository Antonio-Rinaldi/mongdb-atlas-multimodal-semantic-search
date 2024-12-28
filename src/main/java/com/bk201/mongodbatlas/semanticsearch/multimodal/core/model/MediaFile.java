package com.bk201.mongodbatlas.semanticsearch.multimodal.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {
    private String name;
    private String type;
    private InputStream content;
    private long size;
}