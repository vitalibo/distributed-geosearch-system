package com.github.vitalibo.geosearch.api.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.UUID;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class GeoEvent {

    private UUID id;
    private String timestamp;
    private Double latitude;
    private Double longitude;

}
