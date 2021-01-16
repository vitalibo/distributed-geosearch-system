package com.github.vitalibo.geosearch.subject.core.source;

import com.github.vitalibo.geosearch.subject.core.PollableSource;
import com.github.vitalibo.geosearch.subject.core.model.GeoEvent;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class RandomGeoEventSource extends PollableSource<GeoEvent> {

    private final double probability;
    private final Supplier<Double> random;
    private final Supplier<Instant> now;
    private final Supplier<UUID> uuid;

    public RandomGeoEventSource(int backOffSleepIntervalMillis, double probability) {
        this(backOffSleepIntervalMillis, probability, Math::random, Instant::now, UUID::randomUUID);
    }

    RandomGeoEventSource(int backOffSleepIntervalMillis, double probability, // NOPMD
                         Supplier<Double> random, Supplier<Instant> now, Supplier<UUID> uuid) {
        super(backOffSleepIntervalMillis);
        this.probability = probability;
        this.random = random;
        this.now = now;
        this.uuid = uuid;
    }

    @Override
    public Optional<GeoEvent> process() {
        if (random.get() > probability) {
            return Optional.empty();
        }

        return Optional.of(new GeoEvent()
            .withId(uuid.get())
            .withTimestamp(now.get())
            .withLatitude(random.get() * 180 - 90)
            .withLongitude(random.get() * 360 - 180));
    }

}
