package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandActionShared;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandBoundingBoxShared;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeoSearchCommandTranslatorTest {

    @Test
    public void testFrom() {
        GeoSearchCommandShared geoSearchCommandShared = GeoSearchCommandShared.newBuilder()
            .setId(UUID.fromString("8b4bb543-f654-4dd6-b762-68978b2a993a"))
            .setAction(GeoSearchCommandActionShared.SUBSCRIBE)
            .setBoundingBox(GeoSearchCommandBoundingBoxShared.newBuilder()
                .setType("GeoJSON")
                .setGeometry("Polygon {}")
                .build())
            .build();

        Iterable<GeoSearchCommand> iterable = GeoSearchCommandTranslator.from(geoSearchCommandShared);

        Assert.assertNotNull(iterable);
        List<GeoSearchCommand> list = new ArrayList<>();
        iterable.forEach(list::add);
        Assert.assertEquals(list.size(), 1);
        GeoSearchCommand actual = list.get(0);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getId(), "8b4bb543-f654-4dd6-b762-68978b2a993a");
        Assert.assertTrue(actual.isSubscribe());
        BoundingBox boundingBox = actual.getBoundingBox();
        Assert.assertEquals(boundingBox.getType(), "GeoJSON");
        Assert.assertEquals(boundingBox.getGeometry(), "Polygon {}");
    }

    @Test
    public void testFromNull() {
        Iterable<GeoSearchCommand> iterable = GeoSearchCommandTranslator.from(null);

        Assert.assertNotNull(iterable);
        List<GeoSearchCommand> list = new ArrayList<>();
        iterable.forEach(list::add);
        Assert.assertTrue(list.isEmpty());
    }

}
