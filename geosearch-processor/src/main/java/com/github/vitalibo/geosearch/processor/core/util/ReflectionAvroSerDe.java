package com.github.vitalibo.geosearch.processor.core.util;

import lombok.experimental.Delegate;
import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReflectionAvroSerDe<T> implements Serde<T> {

    private final Schema schema;

    @Delegate
    private final Serde<T> delegate;

    public ReflectionAvroSerDe(Class<T> cls) {
        this.schema = ReflectData.get().getSchema(cls);
        this.delegate = Serdes.serdeFrom(
            new ReflectionAvroSerializer(),
            new ReflectionAvroDeserializer());
    }

    class ReflectionAvroSerializer implements Serializer<T> {

        @Override
        public byte[] serialize(String topic, T data) {
            if (data == null) {
                return null;
            }

            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(stream, null);
                DatumWriter<T> datumWriter = new ReflectDatumWriter<>(schema);
                datumWriter.write(data, encoder);
                encoder.flush();
                return stream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Can't serialize data", e);
            }
        }

    }

    class ReflectionAvroDeserializer implements Deserializer<T> {

        @Override
        public T deserialize(String topic, byte[] data) {
            if (data == null) {
                return null;
            }

            try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
                DatumReader<T> datumReader = new ReflectDatumReader<>(schema);
                BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(stream, null);
                return datumReader.read(null, decoder);
            } catch (IOException e) {
                throw new RuntimeException("Can't deserialize data", e);
            }
        }

    }

}
