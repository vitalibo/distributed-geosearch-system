package com.github.vitalibo.geosearch.processor;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public final class TestHelper {

    private TestHelper() {
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

}
