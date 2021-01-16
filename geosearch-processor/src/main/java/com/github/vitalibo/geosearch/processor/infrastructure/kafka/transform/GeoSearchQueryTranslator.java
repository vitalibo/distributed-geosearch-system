package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchQuery;
import com.github.vitalibo.geosearch.shared.GeoSearchQueryActionShared;
import com.github.vitalibo.geosearch.shared.GeoSearchQueryBoundingBoxShared;
import com.github.vitalibo.geosearch.shared.GeoSearchQueryShared;

import java.util.Collections;

public final class GeoSearchQueryTranslator {

    private GeoSearchQueryTranslator() {
    }

    public static Iterable<GeoSearchQuery> from(GeoSearchQueryShared item) {
        if (item == null) {
            return Collections.emptyList();
        }

        return Collections.singleton(
            fromGeoSearchQueryShared(item));
    }

    private static GeoSearchQuery fromGeoSearchQueryShared(GeoSearchQueryShared item) {
        final GeoSearchQueryBoundingBoxShared bb = item.getBoundingBox();

        return new GeoSearchQuery()
            .withId(String.valueOf(item.getId()))
            .withSubscribe(item.getAction() == GeoSearchQueryActionShared.SUBSCRIBE)
            .withBoundingBox(new BoundingBox()
                .withType(bb.getType())
                .withGeometry(bb.getGeometry()));
    }

}
