package com.github.vitalibo.geosearch.api.infrastructure.websocket;

import com.github.vitalibo.geosearch.api.core.model.GeoEvent;
import com.github.vitalibo.geosearch.api.core.util.ErrorState;
import com.github.vitalibo.geosearch.api.core.util.JsonSerDe;
import com.github.vitalibo.geosearch.api.core.util.ValidationException;
import org.mockito.*;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class WebSocketUserInterfaceTest {

    @Mock
    private SimpMessagingTemplate mockSimpMessagingTemplate;
    @Captor
    private ArgumentCaptor<String> captorPayload;
    @Captor
    private ArgumentCaptor<MessageHeaders> captorMessageHeaders;

    private WebSocketUserInterface userInterface;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        userInterface = new WebSocketUserInterface(mockSimpMessagingTemplate, "/foo", "/bar");
    }

    @Test
    public void testTestNotify() {
        GeoEvent event = new GeoEvent()
            .withId(UUID.fromString("20841252-5b03-4a13-a6a1-279caaf9841c"))
            .withTimestamp("Fri, 22 Jan 2021 18:24:45 GMT")
            .withLatitude(1.23)
            .withLongitude(3.21);

        userInterface.notify("sid", event);

        Mockito.verify(mockSimpMessagingTemplate).convertAndSendToUser(
            Mockito.eq("sid"), Mockito.eq("/foo"), captorPayload.capture(), captorMessageHeaders.capture());
        GeoEvent actualGeoEvent = JsonSerDe.fromJsonString(captorPayload.getValue(), GeoEvent.class);
        Assert.assertEquals(actualGeoEvent, event);
        MessageHeaders headers = captorMessageHeaders.getValue();
        Assert.assertEquals(headers.get("simpMessageType"), SimpMessageType.MESSAGE);
        Assert.assertEquals(headers.get("simpSessionId"), "sid");
    }

    @Test
    public void testNotifyValidationError() {
        ErrorState errorState = new ErrorState();
        errorState.addError("foo", "bar");

        userInterface.notifyError("sid", new ValidationException(errorState));

        Mockito.verify(mockSimpMessagingTemplate).convertAndSendToUser(
            Mockito.eq("sid"), Mockito.eq("/bar"), captorPayload.capture(), captorMessageHeaders.capture());
        Assert.assertEquals(captorPayload.getValue(), "{\"foo\":[\"bar\"]}");
        MessageHeaders headers = captorMessageHeaders.getValue();
        Assert.assertEquals(headers.get("simpMessageType"), SimpMessageType.MESSAGE);
        Assert.assertEquals(headers.get("simpSessionId"), "sid");
    }

    @Test
    public void testNotifyError() {
        userInterface.notifyError("sid", new RuntimeException("error"));

        Mockito.verify(mockSimpMessagingTemplate).convertAndSendToUser(
            Mockito.eq("sid"), Mockito.eq("/bar"), captorPayload.capture(), captorMessageHeaders.capture());
        Assert.assertEquals(captorPayload.getValue(), "{\"message\":\"error\"}");
        MessageHeaders headers = captorMessageHeaders.getValue();
        Assert.assertEquals(headers.get("simpMessageType"), SimpMessageType.MESSAGE);
        Assert.assertEquals(headers.get("simpSessionId"), "sid");
    }

}
