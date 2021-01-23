package com.github.vitalibo.geosearch.api.infrastructure.kafka.model.transform;

import com.github.vitalibo.geosearch.api.core.model.GeoEvent;
import com.github.vitalibo.geosearch.api.core.model.GeoSearchResult;
import com.github.vitalibo.geosearch.shared.GeoEventShared;
import com.github.vitalibo.geosearch.shared.GeoSearchResultShared;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GeoSearchResultTranslatorTest {

    @Test
    public void testFrom() {
        ConsumerRecord<String, GeoSearchResultShared> record = new ConsumerRecord<>(
            "topic", 0, 0, "foo",
            GeoSearchResultShared.newBuilder()
                .setId(UUID.fromString("858b538e-0105-4142-bca9-1525895d8ec4"))
                .setEvents(Collections.singletonList(
                    GeoEventShared.newBuilder()
                        .setId(UUID.fromString("727897c0-a92d-49c5-b783-a347b9708c03"))
                        .setEventTime(Instant.ofEpochMilli(1611339885000L))
                        .setLatitude(1.23)
                        .setLongitude(32.1)
                        .build()))
                .build());

        GeoSearchResult actual = GeoSearchResultTranslator.from(record);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getId(), UUID.fromString("858b538e-0105-4142-bca9-1525895d8ec4"));
        List<GeoEvent> events = actual.getEvents();
        Assert.assertNotNull(events);
        Assert.assertEquals(events.size(), 1);
        Assert.assertEquals(events.get(0).getId(), UUID.fromString("727897c0-a92d-49c5-b783-a347b9708c03"));
        Assert.assertEquals(events.get(0).getTimestamp(), "2021-01-22T18:24:45Z");
        Assert.assertEquals(events.get(0).getLatitude(), Double.valueOf(1.23));
        Assert.assertEquals(events.get(0).getLongitude(), Double.valueOf(32.1));
    }

}
