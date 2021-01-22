package com.github.vitalibo.geosearch.api.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application")
@org.springframework.context.annotation.Configuration
public class Configuration {

    private Kafka kafka;
    private WebSocket websocket;

    @Data
    public static class Kafka {

        private String topicGeoSearchCommand;

    }

    @Data
    public static class WebSocket {

        private String eventDestination;
        private String errorDestination;

    }

}
