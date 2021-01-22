package com.github.vitalibo.geosearch.api.infrastructure;

import com.github.vitalibo.geosearch.api.core.facade.GeoSearchFacade;
import com.github.vitalibo.geosearch.api.infrastructure.kafka.KafkaGeoSearchEngine;
import com.github.vitalibo.geosearch.api.infrastructure.websocket.WebSocketUserInterface;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@org.springframework.context.annotation.Configuration
public class Factory {

    @Autowired
    private Configuration configuration;
    @Autowired
    private KafkaTemplate<String, GeoSearchCommandShared> kafkaTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Bean
    public GeoSearchFacade createGeoSearchFacade() {
        final Configuration.Kafka kafkaConf = configuration.getKafka();
        final Configuration.WebSocket websocketConf = configuration.getWebsocket();

        return new GeoSearchFacade(
            new KafkaGeoSearchEngine(
                kafkaTemplate,
                kafkaConf.getTopicGeoSearchCommand()),
            new WebSocketUserInterface(
                messagingTemplate,
                websocketConf.getEventDestination(),
                websocketConf.getErrorDestination()));
    }

}
