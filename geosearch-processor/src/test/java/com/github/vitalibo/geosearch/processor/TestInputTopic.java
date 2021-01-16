package com.github.vitalibo.geosearch.processor;

import com.github.vitalibo.geosearch.processor.core.Readable;
import com.github.vitalibo.geosearch.processor.core.Topic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.kafka.streams.TopologyTestDriver;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class TestInputTopic<K, V> implements Readable.Stream<K, V>, Readable.Table<K, V>, Readable.GlobalTable<K, V> {

    @Delegate(types = {Readable.Stream.class, Readable.Table.class, Readable.GlobalTable.class})
    private final Topic<K, V> topic;
    private final Supplier<TopologyTestDriver> supplier;

    private final Instant startTimestamp;
    private final Duration autoAdvance;

    @Delegate
    @Getter(lazy = true)
    private final org.apache.kafka.streams.TestInputTopic<K, V> testInputTopic =
        createInternalTestInputTopic(supplier.get(), topic, startTimestamp, autoAdvance);

    public TestInputTopic(Topic<K, V> topic, Supplier<TopologyTestDriver> supplier) {
        this(topic, supplier, Instant.now(), Duration.ZERO);
    }

    private static <K, V> org.apache.kafka.streams.TestInputTopic<K, V> createInternalTestInputTopic(TopologyTestDriver driver, Topic<K, V> topic,
                                                                                                     Instant startTimestamp, Duration autoAdvance) {
        return driver.createInputTopic(
            topic.getName(), topic.getKeySerDe().serializer(), topic.getValueSerDe().serializer(),
            startTimestamp, autoAdvance);
    }

}
