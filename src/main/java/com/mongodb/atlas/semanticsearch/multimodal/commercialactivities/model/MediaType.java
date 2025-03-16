package com.mongodb.atlas.semanticsearch.multimodal.commercialactivities.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum MediaType {
    TEXT("text"),
    IMAGE("image"),
    AUDIO("audio"),
    VIDEO("video");

    @JsonValue
    private final String value;

    @Override
    public String toString() {
        return value;
    }

    public static Optional<MediaType> fromContentType(String contentType) {
        return switch (contentType) {
            case String type when type.startsWith("text/") -> Optional.of(TEXT);
            case String type when type.startsWith("image/") -> Optional.of(IMAGE);
            case String type when type.startsWith("audio/") -> Optional.of(AUDIO);
            case String type when type.startsWith("video/") -> Optional.of(VIDEO);
            default -> Optional.empty();
        };
    }
}
