package com.github.vitalibo.geosearch.api.infrastructure.websocket.model.transform;

import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class GeoSearchCommandTranslatorTest {

    @Test
    public void testFromMessage() {
        Message<String> message = new GenericMessage<>("foo", Map.of("simpSessionId", "bar"));

        GeoSearchCommand actual = GeoSearchCommandTranslator.from(message);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getSessionId(), "bar");
        Assert.assertEquals(actual.getBoundingBox(), "foo");
        Assert.assertNotNull(actual.getId());
    }

    @Test
    public void testFromSessionDisconnectEvent() {
        SessionDisconnectEvent event = new SessionDisconnectEvent(
            "", new GenericMessage<>(new byte[0]), "bar", CloseStatus.NORMAL);

        GeoSearchCommand actual = GeoSearchCommandTranslator.from(event);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getSessionId(), "bar");
        Assert.assertNull(actual.getBoundingBox());
        Assert.assertNotNull(actual.getId());
    }

}
