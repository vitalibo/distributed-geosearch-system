package com.github.vitalibo.geosearch.subject.infrastructure.kafka.model.transform;

import com.github.vitalibo.geosearch.shared.GeoEventShared;
import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.UUID;

public class ProducerRecordTranslatorTest {

    private ProducerRecordTranslator translator;

    @BeforeMethod
    public void setUp() {
        translator = new ProducerRecordTranslator("foo");
    }

    @Test
    public void testFrom() {
        GeoEvent event = new GeoEvent()
            .withId(UUID.fromString("5d7e5171-2c20-46ea-8a0a-cfb364caf9e8"))
            .withLatitude(12.34)
            .withLongitude(-45.21)
            .withTimestamp(Instant.parse("2021-02-01T01:02:03.004Z"));

        ProducerRecord<Integer, GeoEventShared> actual = translator.from(event);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.topic(), "foo");
        Assert.assertEquals(actual.key(), Integer.valueOf(6684807));
        GeoEventShared value = actual.value();
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getId(), UUID.fromString("5d7e5171-2c20-46ea-8a0a-cfb364caf9e8"));
        Assert.assertEquals(value.getEventTime(), Instant.parse("2021-02-01T01:02:03.004Z"));
        Assert.assertEquals(value.getLatitude(), 12.34);
        Assert.assertEquals(value.getLongitude(), -45.21);
    }

}
