package com.github.vitalibo.geosearch.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.vitalibo.geosearch.processor.core.util.JsonSerDe;
import lombok.Data;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KeyValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TestHelper {

    private TestHelper() {
    }

    public static String resourceAsString(String resource) {
        return new BufferedReader(new InputStreamReader(resourceAsInputStream(resource)))
            .lines()
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public static InputStream resourceAsInputStream(String resource) {
        InputStream stream = TestHelper.class.getResourceAsStream(resource);
        Objects.requireNonNull(stream, String.format("Resource do not exists. '%s'", resource));
        return stream;
    }

    public static String resourcePath(String resource) {
        final StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        return File.separator +
            String.join(File.separator,
                stack.getClassName().replace('.', File.separatorChar),
                stack.getMethodName(),
                resource);
    }

    public static <T> T assertSerDe(Serde<T> serde, T obj) {
        return assertSerDe(serde, "foo", obj);
    }

    public static <T> T assertSerDe(Serde<T> serde, String topic, T obj) {
        Serializer<T> serializer = serde.serializer();
        byte[] data = serializer.serialize(topic, obj);

        Deserializer<T> deserializer = serde.deserializer();
        return deserializer.deserialize(topic, data);
    }

    public static <K, V> List<List<KeyValue<K, V>>> resourceAsMultiListKeyValue(String resource, TypeReference<List<List<DeserializedKeyValue<K, V>>>> type) {
        return JsonSerDe.fromJsonString(TestHelper.resourceAsInputStream(resource), type)
            .stream()
            .map(list -> list.stream()
                .map(o -> new KeyValue<>(o.getKey(), o.getValue()))
                .collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    @Data
    public static class DeserializedKeyValue<K, V> {

        private K key;
        private V value;

    }

}
