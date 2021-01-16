package com.github.vitalibo.geosearch.processor;

import com.github.vitalibo.geosearch.processor.core.KafkaStreams;
import com.github.vitalibo.geosearch.processor.infrastructure.Factory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.Topology;

@Slf4j
@RequiredArgsConstructor
public class Application {

    private final Factory factory;

    public Application() {
        this(Factory.getInstance());
    }

    public void run(String[] args) {
        final Topology topology;
        switch (args[0]) {
            case "geosearch":
                topology = factory.createGeoSearchTopology();
                break;
            default:
                throw new IllegalArgumentException("Unknown topology name");
        }

        try (KafkaStreams streams = factory.createKafkaStream(topology)) {
            streams.cleanUp();

            streams.start();
            streams.awaitTermination();
        } catch (Exception e) {
            logger.error("Kafka Streams failed execution", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.run(args);
    }
}
