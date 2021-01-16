package com.github.vitalibo.geosearch.processor.infrastructure.kafka;

import com.github.vitalibo.geosearch.processor.core.Readable;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;

@RequiredArgsConstructor
public class ReadableStreamDecorator<IK, IV, OK, OV> implements Readable.Stream<OK, OV> {

    private final Readable.Stream<IK, IV> original;
    private final KeyValueMapper<IK, IV, ? extends Iterable<? extends KeyValue<? extends OK, ? extends OV>>> mapper;

    @Override
    public KStream<OK, OV> stream(StreamsBuilder builder) {
        return original.stream(builder)
            .flatMap(mapper);
    }

}
