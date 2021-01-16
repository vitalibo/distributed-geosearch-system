package com.github.vitalibo.geosearch.subject.infrastructure;

import com.github.vitalibo.geosearch.subject.core.Channel;
import com.github.vitalibo.geosearch.subject.core.Runner;
import com.github.vitalibo.geosearch.subject.core.Source;
import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import com.github.vitalibo.geosearch.subject.core.source.RandomGeoEventSource;
import com.github.vitalibo.geosearch.subject.infrastructure.kafka.KafkaChannel;
import com.github.vitalibo.geosearch.subject.infrastructure.kafka.model.transform.ProducerRecordTranslator;
import com.github.vitalibo.geosearch.subject.infrastructure.websocket.BlitzortungLightningSource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Arrays;
import java.util.Properties;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

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

    public Source<GeoEvent> createRandomGeoEventSource() {
        final Configuration.Random randomConf = configuration.getSource().getRandom();

        return new RandomGeoEventSource(
            randomConf.getBackOffSleepIntervalMillis(),
            randomConf.getProbability());
    }

    public Source<GeoEvent> createBlitzortungLightningSource() {
        final Configuration.Blitzortung blitzortungConf = configuration.getSource().getBlitzortung();

        return new BlitzortungLightningSource(
            blitzortungConf.getHostUriPattern(),
            blitzortungConf.getHosts(),
            blitzortungConf.getPort(),
            blitzortungConf.getMinRunningTimeToReconnect());
    }

    public Runner<GeoEvent> createRunner(Source<GeoEvent> source) {
        return new Runner<>(
            source,
            createKafkaChannel());
    }

    private Channel<GeoEvent> createKafkaChannel() {
        final Configuration.Kafka kafkaConf = configuration.getChannel().getKafka();

        final Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, kafkaConf.getBootstrapServers());
        properties.put(SECURITY_PROTOCOL_CONFIG, kafkaConf.getSecurityProtocol());
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, kafkaConf.getKeySerializerClass());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, kafkaConf.getValueSerializerClass());
        properties.put(ACKS_CONFIG, kafkaConf.getAcknowledgments());
        properties.put(SCHEMA_REGISTRY_URL_CONFIG, kafkaConf.getSchemaRegistryUrl());
        kafkaConf.getDynamicConf().forEach(properties::put);

        return new KafkaChannel<>(
            new KafkaProducer<>(properties),
            new ProducerRecordTranslator(kafkaConf.getTopicGeoEvent())
                ::from);
    }

}
