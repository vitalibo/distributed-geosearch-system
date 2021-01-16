package com.github.vitalibo.geosearch.subject.infrastructure.kafka.model.transform;

import com.github.vitalibo.geosearch.shared.GeoEventShared;
import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;

@RequiredArgsConstructor
public class ProducerRecordTranslator {

    private final String topic;

    public ProducerRecord<Integer, GeoEventShared> from(GeoEvent event) {
        return new ProducerRecord<>(
            topic,
            (int) (((Math.round(event.getLatitude()) + 90) << 16) + Math.round(event.getLongitude()) + 180),
            GeoEventShared.newBuilder()
                .setId(event.getId())
                .setEventTime(event.getTimestamp())
                .setLatitude(event.getLatitude())
                .setLongitude(event.getLongitude())
                .build());
    }

}
