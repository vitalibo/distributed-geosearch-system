package com.github.vitalibo.geosearch.processor.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

@Slf4j
public abstract class TopologyBuilder extends StreamsBuilder {

    public <K, V> KStream<K, V> stream(Readable.Stream<K, V> stream) {
        return stream.stream(this);
    }

    public <K, V> void writeTo(Writable.Stream<K, V> sink, KStream<K, V> stream) {
        sink.writeTo(stream);
    }

    public <K, V> KTable<K, V> table(Readable.Table<K, V> table) {
        return table.table(this);
    }

    public <K, V> GlobalKTable<K, V> globalTable(Readable.GlobalTable<K, V> globalTable) {
        return globalTable.globalTable(this);
    }

    public abstract void defineTopology();

    @Override
    public synchronized Topology build() {
        defineTopology();
        final Topology topology = super.build();
        if (logger.isInfoEnabled()) {
            logger.info("{}", topology.describe());
        }

        return topology;
    }

}
