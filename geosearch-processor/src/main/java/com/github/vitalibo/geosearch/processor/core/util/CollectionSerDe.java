package com.github.vitalibo.geosearch.processor.core.util;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.io.*;
import java.util.Collection;
import java.util.function.Function;

public class CollectionSerDe<T, C extends Collection<T>> implements Serde<C> {

    @Delegate
    private final Serde<C> wrapped;

    CollectionSerDe(Serde<T> delegate, Function<Integer, C> collectionFactory) {
        this.wrapped = Serdes.serdeFrom(
            new CollectionSerializer(delegate.serializer()),
            new CollectionDeserializer(delegate.deserializer(), collectionFactory));
    }

    @RequiredArgsConstructor
    class CollectionSerializer implements Serializer<C> {

        private final Serializer<T> delegate;

        @Override
        public byte[] serialize(String topic, C data) {
            if (data == null) {
                return null;
            }

            try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 DataOutputStream output = new DataOutputStream(stream)) {

                output.writeInt(data.size());
                for (T datum : data) {
                    byte[] bytes = delegate.serialize(topic, datum);
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
    class CollectionDeserializer implements Deserializer<C> {

        private final Deserializer<T> delegate;
        private final Function<Integer, C> collectionFactory;

        @Override
        public C deserialize(String topic, byte[] data) {
            if (data == null) {
                return null;
            }

            try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data))) {
                final int size = stream.readInt();
                final C collection = collectionFactory.apply(size);
                for (int i = 0; i < size; i++) {
                    byte[] bytes = new byte[stream.readInt()];
                    stream.read(bytes);
                    collection.add(delegate.deserialize(topic, bytes));
                }

                return collection;
            } catch (IOException e) {
                throw new RuntimeException("Can't deserialize data", e);
            }
        }

    }

}
