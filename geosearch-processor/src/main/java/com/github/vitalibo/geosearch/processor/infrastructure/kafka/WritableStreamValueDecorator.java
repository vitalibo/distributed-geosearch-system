package com.github.vitalibo.geosearch.processor.infrastructure.kafka;

import com.github.vitalibo.geosearch.processor.core.Writable;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;

@RequiredArgsConstructor
public class WritableStreamValueDecorator<K, IV, OV> implements Writable.Stream<K, OV> {

    private final Writable.Stream<K, IV> original;
    private final ValueMapper<? super OV, ? extends Iterable<? extends IV>> mapper;

    @Override
    public void writeTo(KStream<K, OV> stream) {
        original.writeTo(
            stream.flatMapValues(mapper));
    }

}
