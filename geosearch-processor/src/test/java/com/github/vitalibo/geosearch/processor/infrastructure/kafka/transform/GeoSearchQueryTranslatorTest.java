package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchQuery;
import com.github.vitalibo.geosearch.shared.GeoSearchQueryActionShared;
import com.github.vitalibo.geosearch.shared.GeoSearchQueryBoundingBoxShared;
import com.github.vitalibo.geosearch.shared.GeoSearchQueryShared;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeoSearchQueryTranslatorTest {

    @Test
    public void testFrom() {
        GeoSearchQueryShared geoSearchQueryShared = GeoSearchQueryShared.newBuilder()
            .setId(UUID.fromString("8b4bb543-f654-4dd6-b762-68978b2a993a"))
            .setAction(GeoSearchQueryActionShared.SUBSCRIBE)
            .setBoundingBox(GeoSearchQueryBoundingBoxShared.newBuilder()
                .setType("GeoJSON")
                .setGeometry("Polygon {}")
                .build())
            .build();

        Iterable<GeoSearchQuery> iterable = GeoSearchQueryTranslator.from(geoSearchQueryShared);

        Assert.assertNotNull(iterable);
        List<GeoSearchQuery> list = new ArrayList<>();
        iterable.forEach(list::add);
        Assert.assertEquals(list.size(), 1);
        GeoSearchQuery actual = list.get(0);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getId(), "8b4bb543-f654-4dd6-b762-68978b2a993a");
        Assert.assertTrue(actual.isSubscribe());
        BoundingBox boundingBox = actual.getBoundingBox();
        Assert.assertEquals(boundingBox.getType(), "GeoJSON");
        Assert.assertEquals(boundingBox.getGeometry(), "Polygon {}");
    }

    @Test
    public void testFromNull() {
        Iterable<GeoSearchQuery> iterable = GeoSearchQueryTranslator.from(null);

        Assert.assertNotNull(iterable);
        List<GeoSearchQuery> list = new ArrayList<>();
        iterable.forEach(list::add);
        Assert.assertTrue(list.isEmpty());
    }

}
