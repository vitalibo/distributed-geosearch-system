package com.github.vitalibo.geosearch.processor.core;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

public interface Readable {

    interface Stream<K, V> {

        KStream<K, V> stream(StreamsBuilder builder);

    }

    @FunctionalInterface
    interface Table<K, V> {

        KTable<K, V> table(StreamsBuilder builder);

    }

    @FunctionalInterface
    interface GlobalTable<K, V> {

        GlobalKTable<K, V> globalTable(StreamsBuilder builder);

    }

}
