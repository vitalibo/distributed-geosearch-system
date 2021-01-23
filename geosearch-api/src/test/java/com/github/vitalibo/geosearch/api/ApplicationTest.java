package com.github.vitalibo.geosearch.api;

import com.github.vitalibo.geosearch.api.core.util.JsonSerDe;
import com.github.vitalibo.geosearch.api.infrastructure.Controller;
import com.github.vitalibo.geosearch.shared.GeoEventShared;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;
import com.github.vitalibo.geosearch.shared.GeoSearchResultShared;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ApplicationTest.MockFactory.class)
public class ApplicationTest extends AbstractTestNGSpringContextTests {

    static {
        System.setProperty("spring.config.name", "default-application,application");
        System.setProperty("spring.autoconfigure.exclude", "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration");
    }

    @LocalServerPort
    private Integer port;
    private WebSocketStompClient webSocketStompClient;
    private StompSession session;

    @Autowired
    private Controller controller;
    @Autowired
    private KafkaTemplate<String, GeoSearchCommandShared> mockKafkaTemplate;
    @Captor
    private ArgumentCaptor<ProducerRecord<String, GeoSearchCommandShared>> captorProducerRecord;
    @Mock
    private ConsumerRecord<String, GeoSearchResultShared> mockConsumerRecord;

    @BeforeClass
    public void createWebSocketStompClient() {
        webSocketStompClient = new WebSocketStompClient(
            new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new StringMessageConverter());
    }

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        session = webSocketStompClient
            .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {})
            .get(1, TimeUnit.SECONDS);
    }


    @Test
    public void testFlow() throws InterruptedException {
        StompSubscriber<String> subscriber = new StompSubscriber<>(1);
        session.subscribe("/user/queue/geo_search/result", subscriber);
        session.send("/app/geo_search", TestHelper.resourceAsString(TestHelper.resourcePath("command.json")));

        Thread.sleep(1_000);
        Mockito.verify(mockKafkaTemplate).send(captorProducerRecord.capture());
        ProducerRecord<String, GeoSearchCommandShared> value = captorProducerRecord.getValue();
        final UUID sid = value.value().getId();

        Mockito.when(mockConsumerRecord.value()).thenReturn(GeoSearchResultShared.newBuilder()
            .setId(sid)
            .setEvents(Collections.singletonList(
                GeoEventShared.newBuilder()
                    .setId(UUID.fromString("727897c0-a92d-49c5-b783-a347b9708c03"))
                    .setEventTime(Instant.ofEpochMilli(1611339885000L))
                    .setLatitude(1.23)
                    .setLongitude(32.1)
                    .build()))
            .build());
        controller.consume(mockConsumerRecord);

        Assert.assertEquals(
            subscriber.poll(1, TimeUnit.SECONDS),
            JsonSerDe.toJsonString(JsonSerDe.fromJsonString(
                TestHelper.resourceAsString(TestHelper.resourcePath("event.json")), Map.class)));
    }

    @AfterMethod
    public void tearDown() {
        session.disconnect();
    }

    @TestConfiguration
    public static class MockFactory {

        @Bean
        @Primary
        public KafkaTemplate<String, GeoSearchCommandShared> createMockKafkaTemplate() {
            return Mockito.mock(KafkaTemplate.class);
        }

    }

    private static class StompSubscriber<T> extends ArrayBlockingQueue<T> implements StompFrameHandler {

        public StompSubscriber(int capacity) {
            super(capacity);
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.add((T) payload);
        }

    }

}
