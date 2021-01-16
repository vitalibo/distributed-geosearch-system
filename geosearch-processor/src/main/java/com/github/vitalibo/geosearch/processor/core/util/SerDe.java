package com.github.vitalibo.geosearch.processor.core.util;

import com.github.vitalibo.geosearch.processor.core.model.GeoEvent;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchQuery;
import com.github.vitalibo.geosearch.processor.core.model.GeoSearchResult;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.*;

@SuppressWarnings("PMD.MethodNamingConventions")
public class SerDe extends Serdes {

    public static <T> Serde<Collection<T>> Collection(Serde<T> delegate) {
        return new CollectionSerDe<>(delegate, ArrayList::new);
    }

    public static <T> Serde<List<T>> ArrayList(Serde<T> delegate) {
        return new CollectionSerDe<>(delegate, ArrayList::new);
    }

    public static <T> Serde<List<T>> LinkedList(Serde<T> delegate) {
        return new CollectionSerDe<>(delegate, ignored -> new LinkedList<>());
    }

    public static <T> Serde<Set<T>> HashSet(Serde<T> delegate) {
        return new CollectionSerDe<>(delegate, HashSet::new);
    }

    public static <K, V> Serde<Map<K, V>> HashMap(Serde<K> keySerde, Serde<V> valueSerDe) {
        return new MapSerDe<>(keySerde, valueSerDe, HashMap::new);
    }

    public static Serde<GeoSearchQuery> GeoSearchQuery() {
        return new ReflectionAvroSerDe<>(GeoSearchQuery.class);
    }

    public static Serde<GeoEvent> GeoEvent() {
        return new ReflectionAvroSerDe<>(GeoEvent.class);
    }

    public static Serde<GeoSearchResult> GeoSearchResult() {
        return new ReflectionAvroSerDe<>(GeoSearchResult.class);
    }

}
