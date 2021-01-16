package com.github.vitalibo.geosearch.subject.core;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class PollableSource<T> extends Source<T> {

    private final int backOffSleepIntervalMillis;

    @Override
    @SneakyThrows(InterruptedException.class)
    public T next() {
        while (true) {
            final Optional<T> event = process();
            if (event.isPresent()) {
                return event.get();
            }

            Thread.sleep(backOffSleepIntervalMillis);
        }
    }

    public abstract Optional<T> process();

}
