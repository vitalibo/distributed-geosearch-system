package com.github.vitalibo.geosearch.api.infrastructure.kafka.model.transform;

import com.github.vitalibo.geosearch.api.core.model.GeoEvent;
import com.github.vitalibo.geosearch.api.core.model.GeoSearchResult;
import com.github.vitalibo.geosearch.shared.GeoSearchResultShared;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.stream.Collectors;

public final class GeoSearchResultTranslator {

    private GeoSearchResultTranslator() {
    }

    public static GeoSearchResult from(ConsumerRecord<String, GeoSearchResultShared> record) {
        final GeoSearchResultShared value = record.value();
        return new GeoSearchResult()
            .withId(value.getId())
            .withEvents(value.getEvents().stream()
                .map(o -> new GeoEvent()
                    .withId(o.getId())
                    .withTimestamp(String.valueOf(o.getEventTime()))
                    .withLatitude(o.getLatitude())
                    .withLongitude(o.getLongitude()))
                .collect(Collectors.toList()));
    }

}
