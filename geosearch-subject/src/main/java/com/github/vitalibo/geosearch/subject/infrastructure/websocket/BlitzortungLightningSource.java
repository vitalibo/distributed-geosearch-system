package com.github.vitalibo.geosearch.subject.infrastructure.websocket;

import com.github.vitalibo.geosearch.subject.core.EventDrivenSource;
import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import com.github.vitalibo.geosearch.subject.core.util.JsonSerDe;
import com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.BlitzortungLightningStrike;
import com.github.vitalibo.geosearch.subject.infrastructure.websocket.model.transform.GeoEventTranslator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Slf4j
public class BlitzortungLightningSource extends EventDrivenSource<GeoEvent> {

    private final String hostUriPattern;
    private final List<String> hosts;
    private final int port;
    private final int minRunningTimeToReconnect;
    private final BiFunction<BlitzortungLightningSource, URI, WebSocketSubscriber> webSocketFactory;

    public BlitzortungLightningSource(String hostUriPattern, List<String> hosts, int port, int minRunningTimeToReconnect) {
        this(hostUriPattern, hosts, port, minRunningTimeToReconnect, (that, uri) -> that.new WebSocketSubscriber(uri));
    }

    BlitzortungLightningSource(String hostUriPattern, List<String> hosts, int port, int minRunningTimeToReconnect, // NOPMD
                               BiFunction<BlitzortungLightningSource, URI, WebSocketSubscriber> webSocketFactory) {
        super(1);
        this.hostUriPattern = hostUriPattern;
        this.hosts = hosts;
        this.port = port;
        this.minRunningTimeToReconnect = minRunningTimeToReconnect;
        this.webSocketFactory = webSocketFactory;
    }

    @Override
    public void start() {
        submit(() -> {
            Collections.shuffle(hosts);
            for (String host : hosts) {
                connect(String.format(hostUriPattern, host, port));
            }
        });
    }

    @SneakyThrows
    void connect(String uri) {
        logger.info("Try connect to: {}", uri);
        final WebSocketSubscriber subscriber = webSocketFactory.apply(this, new URI(uri));
        subscriber.connect();

        while (true) {
            long runningTime = subscriber.await();
            if (runningTime < minRunningTimeToReconnect) {
                break;
            }

            logger.warn("Reconnect to: {}", uri);
            subscriber.reconnect();
        }
    }

    public class WebSocketSubscriber extends WebSocketClient {

        private final Supplier<Instant> now;
        private final GeoEventTranslator translator;

        public WebSocketSubscriber(URI uri) {
            this(uri, Instant::now, new GeoEventTranslator());
        }

        WebSocketSubscriber(URI uri, Supplier<Instant> now, GeoEventTranslator translator) {
            super(uri);
            this.now = now;
            this.translator = translator;
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            logger.info("Connection with {} established", getURI());
            send(JsonSerDe.toJsonString(Map.of("time", 0)));
        }

        @Override
        public void onMessage(String message) {
            BlitzortungLightningStrike event = JsonSerDe.fromJsonString(message, BlitzortungLightningStrike.class);
            logger.debug("{}", event);
            process(translator.from(event));
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            logger.error("{} connection with code {}. {}", remote ? "Remote close" : "Close", code, reason);
        }

        @Override
        public void onError(Exception e) {
            logger.error(e.getMessage(), e);
        }

        @SneakyThrows
        public long await() {
            Instant start = now.get();
            while (ReadyState.CLOSED != getReadyState()) {
                Thread.sleep(1000);
            }

            return ChronoUnit.SECONDS.between(start, now.get());
        }

    }

}
