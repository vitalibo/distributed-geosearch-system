package com.github.vitalibo.geosearch.processor.core.stream;

import com.github.vitalibo.geosearch.processor.core.Readable;
import com.github.vitalibo.geosearch.processor.core.TopologyBuilder;
import com.github.vitalibo.geosearch.processor.core.Writable;
import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchQuery;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchResult;
import com.github.vitalibo.geosearch.processor.core.stream.transform.GeoSearchOps;
import com.github.vitalibo.geosearch.processor.core.util.SerDe;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GeoSearchTopology extends TopologyBuilder {

    private final Readable.Stream<Integer, GeoEvent> sourceGeoEvent;
    private final Readable.Stream<String, GeoSearchQuery> sourceGeoSearchQuery;
    private final Writable.Stream<String, GeoSearchResult> sinkGeoSearchResult;

    private final int geohashLength;

    @Override
    public void defineTopology() {
        KTable<String, Collection<GeoSearchQuery>> queries = stream(sourceGeoSearchQuery)
            .flatTransform(GeoSearchOps.defineCoverBoundingBox(geohashLength))
            .groupByKey(Grouped.with(SerDe.String(), SerDe.GeoSearchQuery()))
            .aggregate(HashMap::new, GeoSearchOps::pack, Materialized.with(SerDe.String(), SerDe.HashMap(SerDe.String(), SerDe.GeoSearchQuery())))
            .mapValues(Map::values);

        KStream<String, GeoSearchResult> events = stream(sourceGeoEvent)
            .selectKey(GeoSearchOps.encodeGeoHash(geohashLength))
            .join(queries, KeyValue::pair, Joined.with(SerDe.String(), SerDe.GeoEvent(), SerDe.Collection(SerDe.GeoSearchQuery())))
            .flatMapValues(GeoSearchOps::unpack)
            .flatMap(GeoSearchOps::accurateGeoSearch);

        writeTo(sinkGeoSearchResult, events);
    }

}
