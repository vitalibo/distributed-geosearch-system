package com.github.vitalibo.geosearch.subject;

import com.github.vitalibo.geosearch.subject.core.Runner;
import com.github.vitalibo.geosearch.subject.core.Source;
import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;
import com.github.vitalibo.geosearch.subject.infrastructure.Factory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Application {

    private final Factory factory;

    public Application() {
        this(Factory.getInstance());
    }

    public void run(String[] args) {
        try {
            final Source<GeoEvent> source; // NOPMD
            switch (args[0]) {
                case "random":
                    source = factory.createRandomGeoEventSource();
                    break;
                case "blitzortung":
                    source = factory.createBlitzortungLightningSource();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported source type");
            }

            final Runner<GeoEvent> runner = factory.createRunner(source);
            runner.process();

        } catch (Exception e) {
            logger.error("Application failed", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.run(args);
    }
}
