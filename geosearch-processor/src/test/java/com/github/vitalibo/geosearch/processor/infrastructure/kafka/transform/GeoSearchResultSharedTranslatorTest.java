package com.github.vitalibo.geosearch.processor.infrastructure.kafka.transform;

import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchResult;
import com.github.vitalibo.geosearch.shared.GeoEventShared;
import com.github.vitalibo.geosearch.shared.GeoSearchResultShared;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeoSearchResultSharedTranslatorTest {

    @Test
    public void testFrom() {
        GeoSearchResult geoSearchResult = new GeoSearchResult()
            .withId("7cf593a5-30d4-4a82-a5bb-497a6eb081ac")
            .withEvent(new GeoEvent()
                .withId("a8e5ccab-58ca-4c13-b2ea-1fab2777dd5d")
                .withTimestamp(1610438399000L)
                .withLatitude(1.23)
                .withLongitude(32.1));

        Iterable<GeoSearchResultShared> iterable = GeoSearchResultSharedTranslator.from(geoSearchResult);

        Assert.assertNotNull(iterable);
        List<GeoSearchResultShared> list = new ArrayList<>();
        iterable.forEach(list::add);
        Assert.assertEquals(list.size(), 1);
        GeoSearchResultShared actual = list.get(0);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getId(), UUID.fromString("7cf593a5-30d4-4a82-a5bb-497a6eb081ac"));
        List<GeoEventShared> events = actual.getEvents();
        Assert.assertNotNull(events);
        Assert.assertEquals(events.size(), 1);
        GeoEventShared geoEventShared = events.get(0);
        Assert.assertEquals(geoEventShared.getId(), UUID.fromString("a8e5ccab-58ca-4c13-b2ea-1fab2777dd5d"));
        Assert.assertEquals(geoEventShared.getEventTime(), Instant.ofEpochMilli(1610438399000L));
        Assert.assertEquals(geoEventShared.getLatitude(), 1.23);
        Assert.assertEquals(geoEventShared.getLongitude(), 32.1);
    }

}
