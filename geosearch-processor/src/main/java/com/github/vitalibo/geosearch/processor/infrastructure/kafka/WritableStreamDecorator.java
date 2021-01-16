package com.github.vitalibo.geosearch.processor.infrastructure.kafka;

import com.github.vitalibo.geosearch.processor.core.Writable;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;

@RequiredArgsConstructor
public class WritableStreamDecorator<IK, IV, OK, OV> implements Writable.Stream<OK, OV> {

    private final Writable.Stream<IK, IV> original;
    private final KeyValueMapper<OK, OV, ? extends Iterable<? extends KeyValue<? extends IK, ? extends IV>>> mapper;

    @Override
    public void writeTo(KStream<OK, OV> stream) {
        original.writeTo(
            stream.flatMap(mapper));
    }

}
