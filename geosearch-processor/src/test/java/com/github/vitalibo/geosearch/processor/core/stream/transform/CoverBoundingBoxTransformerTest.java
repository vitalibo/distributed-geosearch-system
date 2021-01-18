package com.github.vitalibo.geosearch.processor.core.stream.transform;

import com.github.vitalibo.geosearch.processor.TestHelper;
import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchCommand;
import org.apache.kafka.streams.KeyValue;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class CoverBoundingBoxTransformerTest {

    @Test
    public void testTransform() {
        CoverBoundingBoxTransformer transformer = new CoverBoundingBoxTransformer(5);
        GeoSearchCommand request = new GeoSearchCommand()
            .withId("74382c9d-b771-48af-a0df-4040d2a42f49")
            .withBoundingBox(new BoundingBox()
                .withType("geojson")
                .withGeometry(TestHelper.resourceAsString(TestHelper.resourcePath("Avtovokzal-Bronova.geojson"))));

        Iterable<KeyValue<String, GeoSearchCommand>> iterable = transformer.transform("foo", request);

        Assert.assertNotNull(iterable);
        List<KeyValue<String, GeoSearchCommand>> objects = new ArrayList<>();
        iterable.forEach(objects::add);
        Assert.assertEquals(objects.size(), 2);
        Assert.assertEquals(objects.get(0).key, "u8c54");
        Assert.assertEquals(objects.get(0).value, request);
        Assert.assertEquals(objects.get(1).key, "u8c56");
        Assert.assertEquals(objects.get(1).value, request);
    }

}
