package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchResult;
import com.github.vitalibo.geosearch.shared.GeoEventShared;
import com.github.vitalibo.geosearch.shared.GeoSearchResultShared;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

public final class GeoSearchResultSharedTranslator {

    private GeoSearchResultSharedTranslator() {
    }

    public static Iterable<GeoSearchResultShared> from(GeoSearchResult item) {
        return Collections.singleton(
            fromGeoSearchResult(item));
    }

    private static GeoSearchResultShared fromGeoSearchResult(GeoSearchResult item) {
        final GeoEvent event = item.getEvent();

        return GeoSearchResultShared.newBuilder()
            .setId(UUID.fromString(item.getId()))
            .setEvents(Collections.singletonList(
                GeoEventShared.newBuilder()
                    .setId(UUID.fromString(event.getId()))
                    .setEventTime(Instant.ofEpochMilli(event.getTimestamp()))
                    .setLatitude(event.getLatitude())
                    .setLongitude(event.getLongitude())
                    .build()))
            .build();
    }

}
