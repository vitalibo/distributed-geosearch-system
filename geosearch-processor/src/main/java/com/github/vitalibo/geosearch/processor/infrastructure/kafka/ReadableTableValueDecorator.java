package com.github.vitalibo.geosearch.processor.infrastructure.kafka;

import com.github.vitalibo.geosearch.processor.core.Readable;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.ValueMapper;

@RequiredArgsConstructor
public class ReadableTableValueDecorator<K, VI, VO> implements Readable.Table<K, VO> {

    private final Readable.Table<K, VI> original;
    private final ValueMapper<VI, VO> mapper;

    @Override
    public KTable<K, VO> table(StreamsBuilder builder) {
        return original.table(builder)
            .mapValues(mapper);
    }

}
