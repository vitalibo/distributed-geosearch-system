package com.github.vitalibo.geosearch.api.core.model;

import lombok.*;

import java.util.UUID;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class GeoSearchCommand {

    private String sessionId;
    @Getter(lazy = true)
    private final UUID id = UUID.nameUUIDFromBytes(sessionId.getBytes());
    private boolean disconnect;
    private String boundingBox;

}
