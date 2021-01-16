package com.github.vitalibo.geosearch.processor.core.stream.transform;

import com.github.vitalibo.geosearch.processor.core.math.Geo;
import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchQuery;
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

    public static TransformerSupplier<String, GeoSearchQuery, Iterable<KeyValue<String, GeoSearchQuery>>> defineCoverBoundingBox(int geohashLength) {
        return () -> new CoverBoundingBoxTransformer(geohashLength);
    }

    public static Map<String, GeoSearchQuery> pack(String ignored, GeoSearchQuery query, Map<String, GeoSearchQuery> aggregate) {
        if (query.isSubscribe()) {
            aggregate.put(query.getId(), query);
        } else {
            aggregate.remove(query.getId());
        }

        return aggregate;
    }

    public static KeyValueMapper<Integer, GeoEvent, String> encodeGeoHash(int geohashLength) {
        return (k, v) -> Geo.encodeGeoHash(v.getLatitude(), v.getLongitude(), geohashLength);
    }

    public static Iterable<KeyValue<GeoEvent, GeoSearchQuery>> unpack(KeyValue<GeoEvent, Collection<GeoSearchQuery>> pair) {
        final GeoEvent event = pair.key;
        final Collection<GeoSearchQuery> queries = pair.value;

        return queries.stream()
            .map(query -> new KeyValue<>(event, query))
            .collect(Collectors.toList());
    }

    public static Iterable<KeyValue<String, GeoSearchResult>> accurateGeoSearch(String ignored, KeyValue<GeoEvent, GeoSearchQuery> pair) {
        final GeoEvent event = pair.key;
        final GeoSearchQuery query = pair.value;

        Polygon polygon = Geo.createPolygon(query.getBoundingBox());
        Point point = Geo.createPoint(event.getLatitude(), event.getLongitude());
        if (!polygon.contains(point)) {
            return Collections.emptyList();
        }

        return Collections.singleton(
            new KeyValue<>(
                query.getId(),
                new GeoSearchResult()
                    .withId(query.getId())
                    .withEvent(event)));
    }

}
