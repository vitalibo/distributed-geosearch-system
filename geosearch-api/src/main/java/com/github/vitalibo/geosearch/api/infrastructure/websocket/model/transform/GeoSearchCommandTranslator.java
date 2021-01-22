package com.github.vitalibo.geosearch.api.infrastructure.websocket.model.transform;

import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public final class GeoSearchCommandTranslator {

    private GeoSearchCommandTranslator() {
    }

    public static GeoSearchCommand from(Message<String> message) {
        final MessageHeaders headers = message.getHeaders();

        return new GeoSearchCommand()
            .withSessionId((String) headers.get("simpSessionId"))
            .withBoundingBox(message.getPayload());
    }

    public static GeoSearchCommand from(SessionDisconnectEvent event) {
        return new GeoSearchCommand()
            .withSessionId(event.getSessionId())
            .withDisconnect(true);
    }

}
