package com.github.vitalibo.geosearch.processor;

import com.github.vitalibo.geosearch.processor.core.Topic;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.testng.annotations.AfterClass;

import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Function;

public class TestTopology implements AutoCloseable {

    protected TopologyTestDriver driver;

    public TopologyTestDriver configure(StreamsBuilder topologyBuilder) {
        return configure(topologyBuilder, new Properties());
    }

    public TopologyTestDriver configure(StreamsBuilder topologyBuilder, Properties properties) {
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, this.getClass().getName());
        properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "mock:9092");
        properties.setProperty(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://foo");
        properties.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
        driver = new TopologyTestDriver(topologyBuilder.build(), properties);
        return driver;
    }

    public <K, V> TestInputTopic<K, V> createMockInputTopic(Serde<K> key, Serde<V> value) {
        return createMockInputTopic(randomName(key, value), key, value);
    }

    public <K, V> TestInputTopic<K, V> createMockInputTopic(String name, Serde<K> key, Serde<V> value) {
        return new TestInputTopic<>(new Topic<>(name, key, value), () -> driver);
    }

    public <K, V> TestInputTopic<K, V> createMockInputTopic(Serde<K> key, Serde<V> value,
                                                            Instant startTimestamp, Duration autoAdvance) {
        return createMockInputTopic(randomName(key, value), key, value, startTimestamp, autoAdvance);
    }

    public <K, V> TestInputTopic<K, V> createMockInputTopic(String name, Serde<K> key, Serde<V> value,
                                                            Instant startTimestamp, Duration autoAdvance) {
        return new TestInputTopic<>(new Topic<>(name, key, value), () -> driver, startTimestamp, autoAdvance);
    }

    public <K, V> TestOutputTopic<K, V> createMockOutputTopic(Serde<K> key, Serde<V> value) {
        return createMockOutputTopic(randomName(key, value), key, value);
    }

    public <K, V> TestOutputTopic<K, V> createMockOutputTopic(String name, Serde<K> key, Serde<V> value) {
        return new TestOutputTopic<>(new Topic<>(name, key, value), () -> driver);
    }

    @Override
    @AfterClass
    public void close() {
        if (driver != null) {
            driver.close();
        }
    }

    private static String randomName(Serde<?> key, Serde<?> value) {
        final Function<Serde<?>, String> f = (o) -> {
            try {
                return ((ParameterizedType) o.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
            } catch (Exception ignored) {
                return "?";
            }
        };

        return String.format("%s <%s, %s>", UUID.randomUUID(), f.apply(key), f.apply(value));
    }

}
