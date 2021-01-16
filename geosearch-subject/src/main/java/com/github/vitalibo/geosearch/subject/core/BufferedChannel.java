package com.github.vitalibo.geosearch.subject.core;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RequiredArgsConstructor
public abstract class BufferedChannel<T> implements Channel<T> {

    private final ScheduledExecutorService executor;
    private final int flashIntervalMillis;
    private final int maxBufferSize;
    private final List<T> buffer;

    private ScheduledFuture<?> scheduled;

    public BufferedChannel(int flashIntervalMillis, int maxBufferSize) {
        this(Executors.newSingleThreadScheduledExecutor(), flashIntervalMillis, maxBufferSize, new CopyOnWriteArrayList<>());
    }

    @Override
    public void send(T t) {
        if (buffer.isEmpty()) {
            schedule();
        }

        buffer.add(t);

        if (buffer.size() == maxBufferSize) {
            flash();
        }
    }

    private void schedule() {
        if (scheduled != null && (!scheduled.isDone() || !scheduled.isCancelled())) {
            scheduled.cancel(false);
        }

        scheduled = executor.schedule(this::flash, flashIntervalMillis, TimeUnit.MILLISECONDS);
    }

    private void flash() {
        if (buffer.isEmpty()) {
            return;
        }

        send(new ArrayList<>(buffer));
        buffer.clear();
    }

    public abstract void send(List<T> items);

}
