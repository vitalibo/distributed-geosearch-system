package com.github.vitalibo.geosearch.api.infrastructure.kafka.model.transform;

import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandBoundingBoxShared;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;

import static com.github.vitalibo.geosearch.shared.GeoSearchCommandActionShared.SUBSCRIBE;
import static com.github.vitalibo.geosearch.shared.GeoSearchCommandActionShared.UNSUBSCRIBE;

@RequiredArgsConstructor
public class ProducerRecordTranslator {

    private final String topic;

    public ProducerRecord<String, GeoSearchCommandShared> from(GeoSearchCommand command) {
        return new ProducerRecord<>(
            topic,
            String.valueOf(command.getId()),
            fromGeoSearchCommand(command));
    }

    private static GeoSearchCommandShared fromGeoSearchCommand(GeoSearchCommand command) {
        return GeoSearchCommandShared.newBuilder()
            .setId(command.getId())
            .setAction(command.isDisconnect() ? UNSUBSCRIBE : SUBSCRIBE)
            .setBoundingBox(command.isDisconnect() ? null :
                GeoSearchCommandBoundingBoxShared.newBuilder()
                    .setType("GeoJSON")
                    .setGeometry(command.getBoundingBox())
                    .build())
            .build();
    }

}
