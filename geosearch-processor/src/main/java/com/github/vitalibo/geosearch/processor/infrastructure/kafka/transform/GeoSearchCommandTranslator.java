package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandActionShared;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;

import java.util.Collections;
import java.util.Optional;

public final class GeoSearchCommandTranslator {

    private GeoSearchCommandTranslator() {
    }

    public static Iterable<GeoSearchCommand> from(GeoSearchCommandShared item) {
        if (item == null) {
            return Collections.emptyList();
        }

        return Collections.singleton(
            fromGeoSearchCommandShared(item));
    }

    private static GeoSearchCommand fromGeoSearchCommandShared(GeoSearchCommandShared item) {
        return new GeoSearchCommand()
            .withId(String.valueOf(item.getId()))
            .withSubscribe(item.getAction() == GeoSearchCommandActionShared.SUBSCRIBE)
            .withBoundingBox(Optional.ofNullable(item.getBoundingBox())
                .map(bb -> new BoundingBox()
                    .withType(bb.getType())
                    .withGeometry(bb.getGeometry()))
                .orElse(null));
    }

}
