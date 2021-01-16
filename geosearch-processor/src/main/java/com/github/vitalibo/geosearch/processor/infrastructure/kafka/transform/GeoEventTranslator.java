package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.shared.GeoEventShared;

import java.util.Collections;

public final class GeoEventTranslator {

    private GeoEventTranslator() {
    }

    public static Iterable<GeoEvent> from(GeoEventShared item) {
        if (item == null) {
            return Collections.emptyList();
        }

        return Collections.singleton(
            fromGeoEventShared(item));
    }

    private static GeoEvent fromGeoEventShared(GeoEventShared item) {
        return new GeoEvent()
            .withId(String.valueOf(item.getId()))
            .withTimestamp(item.getEventTime().toEpochMilli())
            .withLatitude(item.getLatitude())
            .withLongitude(item.getLongitude());
    }

}
