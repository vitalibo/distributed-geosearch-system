package com.github.vitalibo.geosearch.api.infrastructure.kafka;

import com.github.vitalibo.geosearch.api.core.GeoSearchEngine;
import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.api.infrastructure.kafka.model.transform.ProducerRecordTranslator;
import com.github.vitalibo.geosearch.shared.GeoSearchCommandShared;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class KafkaGeoSearchEngine implements GeoSearchEngine {

    private final KafkaTemplate<String, GeoSearchCommandShared> template;
    private final ProducerRecordTranslator translator;

    public KafkaGeoSearchEngine(KafkaTemplate<String, GeoSearchCommandShared> template, String topic) {
        this(template, new ProducerRecordTranslator(topic));
    }

    @Override
    public void submit(GeoSearchCommand command) {
        ProducerRecord<String, GeoSearchCommandShared> record = translator.from(command);
        template.send(record);
    }

}
