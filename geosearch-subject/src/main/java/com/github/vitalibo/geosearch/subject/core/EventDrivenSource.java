package com.github.vitalibo.geosearch.subject.core;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@RequiredArgsConstructor
public abstract class EventDrivenSource<T> extends Source<T> {

    private final BlockingQueue<T> queue;
    private final ExecutorService executor;

    public EventDrivenSource(int threads) {
        this(new LinkedBlockingQueue<>(), Executors.newFixedThreadPool(threads));
    }

    public abstract void start();

    public void submit(Runnable runnable) {
        executor.submit(runnable);
    }

    @Override
    @SneakyThrows(InterruptedException.class)
    public T next() {
        return queue.take();
    }

    @SneakyThrows
    public void process(T t) {
        queue.put(t);
    }

}
