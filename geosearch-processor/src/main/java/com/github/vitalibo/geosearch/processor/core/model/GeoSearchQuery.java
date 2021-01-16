package com.github.vitalibo.geosearch.processor.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class GeoSearchQuery {

    private String id;
    private boolean subscribe;
    private BoundingBox boundingBox;

}
