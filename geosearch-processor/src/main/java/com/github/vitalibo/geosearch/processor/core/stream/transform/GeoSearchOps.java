package com.github.vitalibo.geosearch.processor.core.stream.transform;

import com.github.vitalibo.geosearch.processor.core.math.Geo;
import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchResult;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public final class GeoSearchOps {

    private GeoSearchOps() {
    }

    public static TransformerSupplier<String, GeoSearchCommand, Iterable<KeyValue<String, GeoSearchCommand>>> defineCoverBoundingBox(int geohashLength, String geohashesStore) {
        return () -> new CoverBoundingBoxTransformer(geohashLength, geohashesStore);
    }

    public static Map<String, GeoSearchCommand> pack(String ignored, GeoSearchCommand command, Map<String, GeoSearchCommand> aggregate) {
        if (command.isSubscribe()) {
            aggregate.put(command.getId(), command);
        } else {
            aggregate.remove(command.getId());
        }

        return aggregate;
    }

    public static KeyValueMapper<Integer, GeoEvent, String> encodeGeoHash(int geohashLength) {
        return (k, v) -> Geo.encodeGeoHash(v.getLatitude(), v.getLongitude(), geohashLength);
    }

    public static Iterable<KeyValue<GeoEvent, GeoSearchCommand>> unpack(KeyValue<GeoEvent, Collection<GeoSearchCommand>> pair) {
        final GeoEvent event = pair.key;
        final Collection<GeoSearchCommand> commands = pair.value;

        return commands.stream()
            .map(command -> new KeyValue<>(event, command))
            .collect(Collectors.toList());
    }

    public static Iterable<KeyValue<String, GeoSearchResult>> accurateGeoSearch(String ignored, KeyValue<GeoEvent, GeoSearchCommand> pair) {
        final GeoEvent event = pair.key;
        final GeoSearchCommand command = pair.value;

        Polygon polygon = Geo.createPolygon(command.getBoundingBox());
        Point point = Geo.createPoint(event.getLatitude(), event.getLongitude());
        if (!polygon.contains(point)) {
            return Collections.emptyList();
        }

        return Collections.singleton(
            new KeyValue<>(
                command.getId(),
                new GeoSearchResult()
                    .withId(command.getId())
                    .withEvent(event)));
    }

}
