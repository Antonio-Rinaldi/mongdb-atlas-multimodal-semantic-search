package com.bk201.mongodbatlas.semanticsearch.multimodal.core.media;

import com.bk201.mongodbatlas.semanticsearch.multimodal.core.model.MediaFile;

public interface MediaProcessor {

    String processMedia(MediaFile mediaFile);
}
