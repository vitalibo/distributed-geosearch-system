package com.github.vitalibo.geosearch.api.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.UUID;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class GeoSearchResult {

    private UUID id;
    private List<GeoEvent> events;

}
