package com.github.vitalibo.geosearch.processor.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.avro.reflect.Nullable;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class GeoSearchCommand {

    private String id;
    private boolean subscribe;
    @Nullable
    private BoundingBox boundingBox;

}
