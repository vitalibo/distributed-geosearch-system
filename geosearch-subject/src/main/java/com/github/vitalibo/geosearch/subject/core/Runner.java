package com.github.vitalibo.geosearch.subject.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Runner<T> {

    private final Source<T> source;
    private final Channel<T> channel;

    public void process() {
        try (source; channel) {
            if (source instanceof EventDrivenSource) {
                ((EventDrivenSource<T>) source).start();
            }

            for (T event : source) {
                channel.send(event);
            }
        }
    }

}
