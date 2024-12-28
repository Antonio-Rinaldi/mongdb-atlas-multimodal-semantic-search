package com.bk201.mongodbatlas.semanticsearch.multimodal.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultimodalSearch {

    private String text = Strings.EMPTY;
    private List<MediaFile> media = new ArrayList<>();
}
