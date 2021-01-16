package com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.transform;

import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.BlitzortungLightningStrike;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class GeoEventTranslator {

    private final Supplier<UUID> uuid;

    public GeoEventTranslator() {
        this(UUID::randomUUID);
    }

    public GeoEvent from(BlitzortungLightningStrike event) {
        return new GeoEvent()
            .withId(uuid.get())
            .withTimestamp(Instant.ofEpochMilli(event.getTimestampInNanoSecond() / 1_000_000))
            .withLatitude(event.getLatitudeInDegree())
            .withLongitude(event.getLongitudeInDegree());
    }

}
