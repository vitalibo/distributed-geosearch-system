package com.github.vitalibo.geosearch.processor.infrastructure;

import com.github.vitalibo.geosearch.processor.core.KafkaStreams;
import com.github.vitalibo.geosearch.processor.core.stream.GeoSearchTopology;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.*;

public class Factory {

    @Getter(lazy = true)
    private static final Factory instance = new Factory(
        ConfigFactory.load(), ConfigFactory.parseResources("application.hocon"),
        ConfigFactory.parseResources("default-application.hocon"));

    @Getter
    private final Configuration configuration;

    Factory(Config... configs) {
        this.configuration = ConfigBeanFactory.create(
            Arrays.stream(configs)
                .reduce(Config::withFallback)
                .orElseThrow(IllegalStateException::new)
                .resolve(),
            Configuration.class);
    }

    public Topology createGeoSearchTopology() {
        return new GeoSearchTopology()
            .build();
    }

    public KafkaStreams createKafkaStream(Topology topology) {
        final Configuration.Kafka kafkaConf = configuration.getKafka();
        final Properties properties = new Properties();
        properties.put(APPLICATION_ID_CONFIG, kafkaConf.getApplicationId());
        properties.put(BOOTSTRAP_SERVERS_CONFIG, kafkaConf.getBootstrapServers());
        properties.put(SECURITY_PROTOCOL_CONFIG, kafkaConf.getSecurityProtocol());
        properties.put(TOPOLOGY_OPTIMIZATION_CONFIG, OPTIMIZE);
        properties.put(REPLICATION_FACTOR_CONFIG, kafkaConf.getReplicationFactor());
        properties.put(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);
        kafkaConf.getDynamicConf().forEach(properties::put);

        return new KafkaStreams(
            new org.apache.kafka.streams.KafkaStreams(
                topology,
                properties));
    }

    public static <T extends SpecificRecord> Serde<T> valueSchemaRegistryAvroSerDe(Configuration.Kafka kafkaConf) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(SCHEMA_REGISTRY_URL_CONFIG, kafkaConf.getSchemaRegistryUrl());
        kafkaConf.getSchemaRegistryDynamicConf().forEach(properties::put);

        final Serde<T> avroSerDe = new SpecificAvroSerde<>(
            new CachedSchemaRegistryClient(
                kafkaConf.getSchemaRegistryUrl(),
                Short.MAX_VALUE));

        avroSerDe.configure(properties, false);
        return avroSerDe;
    }

}
