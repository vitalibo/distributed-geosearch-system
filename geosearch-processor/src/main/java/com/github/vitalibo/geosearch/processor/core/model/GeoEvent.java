package com.github.vitalibo.geosearch.processor.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class GeoEvent {

    private String id;
    private Long timestamp;
    private Double latitude;
    private Double longitude;

}
