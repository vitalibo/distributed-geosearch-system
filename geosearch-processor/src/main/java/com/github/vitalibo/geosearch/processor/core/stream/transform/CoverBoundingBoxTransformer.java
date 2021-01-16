package com.github.vitalibo.geosearch.processor.core.stream.transform;

import com.github.vitalibo.geosearch.processor.core.math.Geo;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchQuery;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.locationtech.jts.geom.Polygon;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CoverBoundingBoxTransformer implements Transformer<String, GeoSearchQuery, Iterable<KeyValue<String, GeoSearchQuery>>> {

    private final int geohashLength;

    @Override
    public void init(ProcessorContext context) {
    }

    @Override
    public Iterable<KeyValue<String, GeoSearchQuery>> transform(String ignored, GeoSearchQuery query) {
        Polygon polygon = Geo.createPolygon(query.getBoundingBox());
        Set<String> hashes = Geo.coverBoundingBox(polygon, geohashLength);

        return hashes.stream()
            .map(geohash -> new KeyValue<>(geohash, query))
            .collect(Collectors.toList());
    }

    @Override
    public void close() {
    }

}
