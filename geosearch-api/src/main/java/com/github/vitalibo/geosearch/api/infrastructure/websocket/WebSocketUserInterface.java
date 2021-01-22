package com.github.vitalibo.geosearch.api.infrastructure.websocket;

import com.github.vitalibo.geosearch.api.core.UserInterface;
import com.github.vitalibo.geosearch.api.core.model.GeoEvent;
import com.github.vitalibo.geosearch.api.core.util.JsonSerDe;
import com.github.vitalibo.geosearch.api.core.util.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

@RequiredArgsConstructor
public class WebSocketUserInterface implements UserInterface {

    private final SimpMessagingTemplate outboundChannel;
    private final String eventDest;
    private final String errorDest;

    @Override
    public void notify(String sessionId, GeoEvent event) {
        sendToSession(eventDest, sessionId,
            JsonSerDe.toJsonString(event));
    }

    @Override
    public void notifyError(String sessionId, Exception exception) {
        sendToSession(errorDest, sessionId,
            JsonSerDe.toJsonString(Map.of("message", exception.getMessage())));
    }

    @Override
    public void notifyError(String sessionId, ValidationException exception) {
        sendToSession(errorDest, sessionId,
            JsonSerDe.toJsonString(exception.getErrorState()));
    }

    @SneakyThrows
    private void sendToSession(String destination, String sessionId, String payload) {
        if (sessionId == null) {
            return;
        }

        final SimpMessageHeaderAccessor headerAccessor =
            SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        outboundChannel.convertAndSendToUser(sessionId, destination, payload, headerAccessor.getMessageHeaders());
    }

}
