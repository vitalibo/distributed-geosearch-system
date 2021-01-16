package com.github.vitalibo.geosearch.subject.infrastructure.websocket;

import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.BlitzortungLightningStrike;
import com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.transform.GeoEventTranslator;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BlitzortungLightningSourceTest {

    @Mock
    private BiFunction<BlitzortungLightningSource, URI, BlitzortungLightningSource.WebSocketSubscriber> mockWebSocketFactory;
    @Mock
    private BlitzortungLightningSource.WebSocketSubscriber mockWebSocketSubscriber;
    @Mock
    private Supplier<Instant> mockInstantSupplier;
    @Mock
    private ServerHandshake mockServerHandshake;
    @Mock
    private GeoEventTranslator mockGeoEventTranslator;
    @Captor
    private ArgumentCaptor<Runnable> captorRunnable;
    @Captor
    private ArgumentCaptor<GeoEvent> captorGeoEvent;
    @Captor
    private ArgumentCaptor<BlitzortungLightningStrike> captorBlitzortungLightningStrike;

    private BlitzortungLightningSource spySource;
    private BlitzortungLightningSource.WebSocketSubscriber spySubscriber;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        spySource = Mockito.spy(new BlitzortungLightningSource(
            "ws://%s.com:%s", Arrays.asList("foo", "bar"),
            8080, 100, mockWebSocketFactory));
        spySubscriber = Mockito.spy(spySource.new WebSocketSubscriber(
            new URI("ws://foo.com"), mockInstantSupplier, mockGeoEventTranslator));
    }

    @Test
    public void testStart() {
        Mockito.doNothing().when(spySource).submit(Mockito.any());
        Mockito.doNothing().when(spySource).connect(Mockito.any());

        spySource.start();

        Mockito.verify(spySource).submit(captorRunnable.capture());
        Runnable function = captorRunnable.getValue();

        function.run();

        Mockito.verify(spySource).connect("ws://foo.com:8080");
        Mockito.verify(spySource).connect("ws://bar.com:8080");
    }

    @Test
    public void testConnect() {
        Mockito.when(mockWebSocketFactory.apply(Mockito.any(), Mockito.any())).thenReturn(mockWebSocketSubscriber);
        Mockito.when(mockWebSocketSubscriber.await()).thenReturn(200L, 200L, 50L);

        spySource.connect("ws://foo.com:8080");

        Mockito.verify(mockWebSocketSubscriber).connect();
        Mockito.verify(mockWebSocketSubscriber, Mockito.times(3)).await();
        Mockito.verify(mockWebSocketSubscriber, Mockito.times(2)).reconnect();
    }

    @Test
    public void testWebSocketSubscriberOnOpen() {
        Mockito.doNothing().when(spySubscriber).send(Mockito.anyString());

        spySubscriber.onOpen(mockServerHandshake);

        Mockito.verify(spySubscriber).send("{\"time\":0}");
    }

    @Test
    public void testWebSocketSubscriberOnMessage() {
        Mockito.doNothing().when(spySource).process(Mockito.any());
        Mockito.when(mockGeoEventTranslator.from(Mockito.any()))
            .thenReturn(new GeoEvent()
                .withTimestamp(Instant.ofEpochMilli(1609481738634L)));

        spySubscriber.onMessage("{\"time\":1609481738634303700}");

        Mockito.verify(spySource).process(captorGeoEvent.capture());
        GeoEvent actual = captorGeoEvent.getValue();
        Assert.assertEquals(actual.getTimestamp(), Instant.parse("2021-01-01T06:15:38.634Z"));
        Mockito.verify(mockGeoEventTranslator).from(captorBlitzortungLightningStrike.capture());
        BlitzortungLightningStrike event = captorBlitzortungLightningStrike.getValue();
        Assert.assertEquals(event.getTimestampInNanoSecond(), Long.valueOf(1609481738634303700L));
    }

    @Test
    public void testWebSocketSubscriberAwait() {
        Mockito.when(mockInstantSupplier.get())
            .thenReturn(Instant.ofEpochSecond(1609707290), Instant.ofEpochSecond(1609707350));
        Mockito.when(spySubscriber.getReadyState())
            .thenReturn(ReadyState.NOT_YET_CONNECTED, ReadyState.OPEN, ReadyState.CLOSING, ReadyState.CLOSED);

        long actual = spySubscriber.await();

        Assert.assertEquals(actual, 60);
        Mockito.verify(mockInstantSupplier, Mockito.times(2)).get();
        Mockito.verify(spySubscriber, Mockito.times(4)).getReadyState();
    }

}
