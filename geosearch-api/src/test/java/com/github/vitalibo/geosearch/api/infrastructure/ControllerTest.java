package com.github.vitalibo.geosearch.api.infrastructure;

import com.github.vitalibo.geosearch.api.core.facade.GeoSearchFacade;
import com.github.vitalibo.geosearch.shared.GeoSearchResultShared;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ControllerTest {

    @Mock
    private GeoSearchFacade mockGeoSearchFacade;

    private Controller controller;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        controller = new Controller(mockGeoSearchFacade);
    }

    @Test
    public void testHandleGeoSearch() {
        Message<String> message = new GenericMessage<>("foo", Map.of("simpSessionId", "bar"));

        controller.handleGeoSearch(message);

        Mockito.verify(mockGeoSearchFacade).handleAsyncCommand(Mockito.any());
    }

    @Test
    public void testHandleSessionDisconnect() {
        SessionDisconnectEvent event = new SessionDisconnectEvent(
            "", new GenericMessage<>(new byte[0]), "bar", CloseStatus.NORMAL);

        controller.handleSessionDisconnect(event);

        Mockito.verify(mockGeoSearchFacade).handleAsyncCommand(Mockito.any());
    }

    @Test
    public void testConsume() {
        ConsumerRecord<String, GeoSearchResultShared> record = new ConsumerRecord<>(
            "topic", 0, 0, "foo",
            GeoSearchResultShared.newBuilder()
                .setId(UUID.fromString("858b538e-0105-4142-bca9-1525895d8ec4"))
                .setEvents(Collections.emptyList())
                .build());

        controller.consume(record);

        Mockito.verify(mockGeoSearchFacade).handleAsyncQueryResult(Mockito.any());
    }

}
