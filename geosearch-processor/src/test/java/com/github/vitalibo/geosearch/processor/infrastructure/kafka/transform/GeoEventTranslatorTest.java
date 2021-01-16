package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.shared.GeoEventShared;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeoEventTranslatorTest {

    @Test
    public void testFrom() {
        GeoEventShared geoEventShared = GeoEventShared.newBuilder()
            .setId(UUID.fromString("08468655-c942-4121-99e6-efc64fb2de0d"))
            .setEventTime(Instant.ofEpochMilli(1610439756000L))
            .setLatitude(12.3)
            .setLongitude(3.21)
            .build();

        Iterable<GeoEvent> iterable = GeoEventTranslator.from(geoEventShared);

        Assert.assertNotNull(iterable);
        List<GeoEvent> list = new ArrayList<>();
        iterable.forEach(list::add);
        Assert.assertEquals(list.size(), 1);
        GeoEvent actual = list.get(0);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getId(), "08468655-c942-4121-99e6-efc64fb2de0d");
        Assert.assertEquals(actual.getTimestamp(), Long.valueOf(1610439756000L));
        Assert.assertEquals(actual.getLatitude(), Double.valueOf(12.3));
        Assert.assertEquals(actual.getLongitude(), Double.valueOf(3.21));
    }

    @Test
    public void testFromNull() {
        Iterable<GeoEvent> iterable = GeoEventTranslator.from(null);

        Assert.assertNotNull(iterable);
        List<GeoEvent> list = new ArrayList<>();
        iterable.forEach(list::add);
        Assert.assertTrue(list.isEmpty());
    }

}
