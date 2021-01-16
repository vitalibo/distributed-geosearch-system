package com.github.vitalibo.geosearch.subject.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class GeoEvent {

    private UUID id;
    private Instant timestamp;
    private Double latitude;
    private Double longitude;

}
