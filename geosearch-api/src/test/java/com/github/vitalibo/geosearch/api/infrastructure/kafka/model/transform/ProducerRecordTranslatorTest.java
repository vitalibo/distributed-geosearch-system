package com.github.vitalibo.geosearch.api.infrastructure.kafka.model.transform;

import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandActionShared;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProducerRecordTranslatorTest {

    @Test
    public void testFromSubscribe() {
        GeoSearchCommand command = new GeoSearchCommand();
        command.setSessionId("foo");
        command.setDisconnect(false);
        command.setBoundingBox("{geojson}");
        ProducerRecordTranslator translator = new ProducerRecordTranslator("foo");

        ProducerRecord<String, GeoSearchCommandShared> actual = translator.from(command);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.topic(), "foo");
        Assert.assertEquals(actual.key(), String.valueOf(command.getId()));
        GeoSearchCommandShared value = actual.value();
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getId(), command.getId());
        Assert.assertEquals(value.getAction(), GeoSearchCommandActionShared.SUBSCRIBE);
        Assert.assertNotNull(value.getBoundingBox());
        Assert.assertEquals(value.getBoundingBox().getType(), "GeoJSON");
        Assert.assertEquals(value.getBoundingBox().getGeometry(), "{geojson}");
    }

    @Test
    public void testFromUnsubscribe() {
        GeoSearchCommand command = new GeoSearchCommand();
        command.setSessionId("foo");
        command.setDisconnect(true);
        command.setBoundingBox("{geojson}");
        ProducerRecordTranslator translator = new ProducerRecordTranslator("foo");

        ProducerRecord<String, GeoSearchCommandShared> actual = translator.from(command);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.topic(), "foo");
        Assert.assertEquals(actual.key(), String.valueOf(command.getId()));
        GeoSearchCommandShared value = actual.value();
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getId(), command.getId());
        Assert.assertEquals(value.getAction(), GeoSearchCommandActionShared.UNSUBSCRIBE);
        Assert.assertNull(value.getBoundingBox());
    }

}
