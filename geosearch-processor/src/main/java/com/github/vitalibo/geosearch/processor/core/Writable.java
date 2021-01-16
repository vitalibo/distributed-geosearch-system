package com.github.vitalibo.geosearch.processor.core;

import org.apache.kafka.streams.kstream.KStream;

public interface Writable {

    @FunctionalInterface
    interface Stream<K, V> {

        void writeTo(KStream<K, V> stream);

    }

}
