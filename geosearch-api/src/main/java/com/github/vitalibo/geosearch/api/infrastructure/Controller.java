package com.github.vitalibo.geosearch.api.infrastructure;

import com.github.vitalibo.geosearch.api.core.facade.GeoSearchFacade;
import com.github.vitalibo.geosearch.api.infrastructure.kafka.model.transform.GeoSearchResultTranslator;
import com.github.vitalibo.geosearch.api.infrastructure.websocket.model.transform.GeoSearchCommandTranslator;
import com.github.vitalibo.geosearch.shared.GeoSearchResultShared;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@org.springframework.stereotype.Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Controller {

    private final GeoSearchFacade facade;

    @MessageMapping("/geo_search")
    public void handleGeoSearch(Message<String> message) {
        facade.handleAsyncCommand(GeoSearchCommandTranslator.from(message));
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        facade.handleAsyncCommand(GeoSearchCommandTranslator.from(event));
    }

    @KafkaListener(topics = "geo-search-result")
    public void consume(ConsumerRecord<String, GeoSearchResultShared> record) {
        facade.handleAsyncQueryResult(GeoSearchResultTranslator.from(record));
    }

}
