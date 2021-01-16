package com.github.vitalibo.geosearch.processor.infrastructure.kafka;

import com.github.vitalibo.geosearch.processor.core.Readable;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;

@RequiredArgsConstructor
public class ReadableStreamValueDecorator<K, IV, OV> implements Readable.Stream<K, OV> {

    private final Readable.Stream<K, IV> original;
    private final ValueMapper<? super IV, ? extends Iterable<? extends OV>> mapper;

    @Override
    public KStream<K, OV> stream(StreamsBuilder builder) {
        return original.stream(builder)
            .flatMapValues(mapper);
    }

}
