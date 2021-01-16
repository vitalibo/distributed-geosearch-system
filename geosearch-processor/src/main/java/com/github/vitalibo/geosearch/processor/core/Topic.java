package com.github.vitalibo.geosearch.processor.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

@Getter
@RequiredArgsConstructor
public class Topic<K, V> implements Readable.Stream<K, V>, Writable.Stream<K, V>, Readable.Table<K, V>, Readable.GlobalTable<K, V> {

    private final String name;
    private final Serde<K> keySerDe;
    private final Serde<V> valueSerDe;

    @Override
    public KStream<K, V> stream(StreamsBuilder builder) {
        return builder.stream(name, Consumed.with(keySerDe, valueSerDe));
    }

    @Override
    public void writeTo(KStream<K, V> stream) {
        stream.to(name, Produced.with(keySerDe, valueSerDe));
    }

    @Override
    public KTable<K, V> table(StreamsBuilder builder) {
        return builder.table(name, Materialized.with(keySerDe, valueSerDe));
    }

    @Override
    public GlobalKTable<K, V> globalTable(StreamsBuilder builder) {
        return builder.globalTable(name, Materialized.with(keySerDe, valueSerDe));
    }

}
