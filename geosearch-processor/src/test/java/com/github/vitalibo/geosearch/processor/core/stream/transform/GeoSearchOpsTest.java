package com.github.vitalibo.geosearch.processor.core.stream.transform;

import com.github.vitalibo.geosearch.processor.TestHelper;
import com.github.vitalibo.geosearch.processor.core.model.BoundingBox;
import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchQuery;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchResult;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class GeoSearchOpsTest {

    @Test
    public void testDefineCoverBoundingBox() {
        TransformerSupplier<String, GeoSearchQuery, Iterable<KeyValue<String, GeoSearchQuery>>> actual =
            GeoSearchOps.defineCoverBoundingBox(5);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.get() instanceof CoverBoundingBoxTransformer);
    }

    @Test
    public void testPackAdd() {
        GeoSearchQuery request = new GeoSearchQuery()
            .withId("3063664f-cd4d-4818-b5d3-8fb72c019bf5")
            .withSubscribe(true);
        Map<String, GeoSearchQuery> state = new HashMap<>();

        Map<String, GeoSearchQuery> actual = GeoSearchOps.pack("foo", request, state);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("3063664f-cd4d-4818-b5d3-8fb72c019bf5"), request);
    }

    @Test
    public void testPackRemove() {
        GeoSearchQuery request = new GeoSearchQuery()
            .withId("3063664f-cd4d-4818-b5d3-8fb72c019bf5")
            .withSubscribe(false);
        Map<String, GeoSearchQuery> state = new HashMap<>();
        state.put("3063664f-cd4d-4818-b5d3-8fb72c019bf5", request);

        Map<String, GeoSearchQuery> actual = GeoSearchOps.pack("foo", request, state);

        Assert.assertNotNull(actual);
        Assert.assertNull(actual.get("3063664f-cd4d-4818-b5d3-8fb72c019bf5"));
    }

    @Test
    public void testEncodeGeoHash() {
        GeoEvent event = new GeoEvent(null, null, 49.80, 24.00);

        KeyValueMapper<Integer, GeoEvent, String> f = GeoSearchOps.encodeGeoHash(5);
        String actual = f.apply(1, event);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "u8c56");
    }

    @Test
    public void testUnpack() {
        GeoEvent event = new GeoEvent(null, null, 49.80, 24.00);
        List<GeoSearchQuery> queries = Arrays.asList(
            new GeoSearchQuery()
                .withId("3063664f-cd4d-4818-b5d3-8fb72c019bf5"),
            new GeoSearchQuery()
                .withId("5e6e317d-4657-4937-ad4e-ae015e129a94"));

        Iterable<KeyValue<GeoEvent, GeoSearchQuery>> iterable =
            GeoSearchOps.unpack(new KeyValue<>(event, queries));

        Assert.assertNotNull(iterable);
        List<KeyValue<GeoEvent, GeoSearchQuery>> objects = new ArrayList<>();
        iterable.forEach(objects::add);
        Assert.assertEquals(objects.get(0).key, event);
        Assert.assertEquals(objects.get(0).value, queries.get(0));
        Assert.assertEquals(objects.get(1).key, event);
        Assert.assertEquals(objects.get(1).value, queries.get(1));
    }

    @Test
    public void testAccurateGeoSearchInside() {
        GeoEvent event = new GeoEvent()
            .withId("06f303cc-ac78-4ded-8761-ee0f16706e4e")
            .withTimestamp(1610492302000L)
            .withLatitude(49.82702060271987)
            .withLongitude(24.03533935546875);
        GeoSearchQuery request = new GeoSearchQuery()
            .withId("74382c9d-b771-48af-a0df-4040d2a42f49")
            .withBoundingBox(new BoundingBox()
                .withType("geojson")
                .withGeometry(TestHelper.resourceAsString(TestHelper.resourcePath("Stryiskyi_Park-Zelena.geojson"))));
        GeoSearchResult expected = new GeoSearchResult()
            .withId("74382c9d-b771-48af-a0df-4040d2a42f49")
            .withEvent(event);

        Iterable<KeyValue<String, GeoSearchResult>> iterable =
            GeoSearchOps.accurateGeoSearch("foo", new KeyValue<>(event, request));

        Assert.assertNotNull(iterable);
        List<KeyValue<String, GeoSearchResult>> objects = new ArrayList<>();
        iterable.forEach(objects::add);
        Assert.assertEquals(objects.size(), 1);
        KeyValue<String, GeoSearchResult> actual = objects.get(0);
        Assert.assertEquals(actual.key, "74382c9d-b771-48af-a0df-4040d2a42f49");
        Assert.assertEquals(actual.value, expected);
    }

    @Test
    public void testAccurateGeoSearchOutside() {
        GeoEvent event = new GeoEvent()
            .withId("06f303cc-ac78-4ded-8761-ee0f16706e4e")
            .withTimestamp(1610492302000L)
            .withLatitude(49.82153874579642)
            .withLongitude(24.031648635864258);
        GeoSearchQuery request = new GeoSearchQuery()
            .withId("74382c9d-b771-48af-a0df-4040d2a42f49")
            .withBoundingBox(new BoundingBox()
                .withType("geojson")
                .withGeometry(TestHelper.resourceAsString(TestHelper.resourcePath("Stryiskyi_Park-Zelena.geojson"))));

        Iterable<KeyValue<String, GeoSearchResult>> iterable =
            GeoSearchOps.accurateGeoSearch("foo", new KeyValue<>(event, request));

        Assert.assertNotNull(iterable);
        List<KeyValue<String, GeoSearchResult>> objects = new ArrayList<>();
        iterable.forEach(objects::add);
        Assert.assertTrue(objects.isEmpty());
    }

}
