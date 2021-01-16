package com.github.vitalibo.geosearch.subject.infrastructure.kafka;

import com.github.vitalibo.geosearch.subject.core.Channel;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.function.Function;

@RequiredArgsConstructor
public class KafkaChannel<K, V, T> implements Channel<T> {

    private final KafkaProducer<K, V> producer;
    private final Function<T, ProducerRecord<K, V>> translator;

    @Override
    public void send(T t) {
        producer.send(translator.apply(t));
    }

    @Override
    public void close() {
        producer.close();
    }

}
