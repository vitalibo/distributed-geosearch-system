package com.github.vitalibo.geosearch.processor.core.stream.transform;

import com.github.vitalibo.geosearch.processor.core.math.Geo;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchCommand;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.locationtech.jts.geom.Polygon;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CoverBoundingBoxTransformer implements Transformer<String, GeoSearchCommand, Iterable<KeyValue<String, GeoSearchCommand>>> {

    private final int geohashLength;

    @Override
    public void init(ProcessorContext context) {
    }

    @Override
    public Iterable<KeyValue<String, GeoSearchCommand>> transform(String ignored, GeoSearchCommand command) {
        Polygon polygon = Geo.createPolygon(command.getBoundingBox());
        Set<String> hashes = Geo.coverBoundingBox(polygon, geohashLength);

        return hashes.stream()
            .map(geohash -> new KeyValue<>(geohash, command))
            .collect(Collectors.toList());
    }

    @Override
    public void close() {
    }

}
