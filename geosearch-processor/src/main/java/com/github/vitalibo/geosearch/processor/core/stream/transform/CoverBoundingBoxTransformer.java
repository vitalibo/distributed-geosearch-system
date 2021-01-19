package com.github.vitalibo.geosearch.processor.core.stream.transform;

import com.github.vitalibo.geosearch.processor.core.math.Geo;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchCommand;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.locationtech.jts.geom.Polygon;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CoverBoundingBoxTransformer implements Transformer<String, GeoSearchCommand, Iterable<KeyValue<String, GeoSearchCommand>>> {

    private final int geohashLength;
    private final String geohashesStore;

    private KeyValueStore<String, Set<String>> store;

    @Override
    public void init(ProcessorContext context) {
        store = context.getStateStore(geohashesStore);
    }

    @Override
    public Iterable<KeyValue<String, GeoSearchCommand>> transform(String ignored, GeoSearchCommand command) {
        final Set<String> hashes;
        if (command.isSubscribe()) {
            Polygon polygon = Geo.createPolygon(command.getBoundingBox());
            hashes = Geo.coverBoundingBox(polygon, geohashLength);
            store.put(command.getId(), hashes);
        } else {
            hashes = store.delete(command.getId());
        }

        return Optional.ofNullable(hashes)
            .map(hs -> hs.stream()
                .map(geohash -> new KeyValue<>(geohash, command))
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
    }

    @Override
    public void close() {
    }

}
