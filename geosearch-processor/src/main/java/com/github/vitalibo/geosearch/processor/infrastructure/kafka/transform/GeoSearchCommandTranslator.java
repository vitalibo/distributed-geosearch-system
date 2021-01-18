package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandActionShared;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandBoundingBoxShared;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;

import java.util.Collections;

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
        final GeoSearchCommandBoundingBoxShared bb = item.getBoundingBox();

        return new GeoSearchCommand()
            .withId(String.valueOf(item.getId()))
            .withSubscribe(item.getAction() == GeoSearchCommandActionShared.SUBSCRIBE)
            .withBoundingBox(new BoundingBox()
                .withType(bb.getType())
                .withGeometry(bb.getGeometry()));
    }

}
