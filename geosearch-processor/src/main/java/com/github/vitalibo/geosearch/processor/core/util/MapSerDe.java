package com.github.vitalibo.geosearch.processor.core.util;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.io.*;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class MapSerDe<K, V> implements Serde<Map<K, V>> {

    @Delegate
    private final Serde<Map<K, V>> wrapper;

    MapSerDe(Serde<K> keySerde, Serde<V> valueSerDe, Function<Integer, Map<K, V>> mapFactory) {
        this.wrapper = Serdes.serdeFrom(
            new MapSerializer<>(keySerde.serializer(), valueSerDe.serializer()),
            new MapDeserializer<>(keySerde.deserializer(), valueSerDe.deserializer(), mapFactory));
    }

    @RequiredArgsConstructor
    static class MapSerializer<K, V> implements Serializer<Map<K, V>> {

        private final Serializer<K> keySerializer;
        private final Serializer<V> valueSerializer;

        @Override
        public byte[] serialize(String topic, Map<K, V> data) {
            if (data == null) {
                return null;
            }

            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 DataOutputStream output = new DataOutputStream(stream)) {

                output.writeInt(data.size());
                byte[] bytes;
                for (Map.Entry<K, V> entry : data.entrySet()) {
                    bytes = keySerializer.serialize(topic, entry.getKey());
                    output.writeInt(bytes.length);
                    output.write(bytes);
                    bytes = valueSerializer.serialize(topic, entry.getValue());
                    output.writeInt(bytes.length);
                    output.write(bytes);
                }

                return stream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Can't serialize data", e);
            }
        }

    }

    @RequiredArgsConstructor
    static class MapDeserializer<K, V> implements Deserializer<Map<K, V>> {

        private final Deserializer<K> keyDeserializer;
        private final Deserializer<V> valueDeserializer;
        private final Function<Integer, Map<K, V>> mapFactory;

        @Override
        public Map<K, V> deserialize(String topic, byte[] data) {
            if (data == null) {
                return null;
            }

            try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data))) {
                final int size = stream.readInt();
                final Map<K, V> map = mapFactory.apply(size);
                byte[] bytes;
                for (int i = 0; i < size; i++) {
                    bytes = new byte[stream.readInt()];
                    stream.read(bytes);
                    K key = keyDeserializer.deserialize(topic, bytes);
                    bytes = new byte[stream.readInt()];
                    stream.read(bytes);
                    V value = valueDeserializer.deserialize(topic, bytes);
                    map.put(key, value);
                }

                return map;
            } catch (IOException e) {
                throw new RuntimeException("Can't deserialize data", e);
            }
        }

    }

}
