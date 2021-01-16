package com.github.vitalibo.geosearch.subject.core;

import java.util.Iterator;

public abstract class Source<T> implements Iterable<T>, Iterator<T>, AutoCloseable {

    private boolean running;

    public Source() {
        this(Runtime.getRuntime());
    }

    Source(Runtime runtime) {
        runtime.addShutdownHook(new Thread(this::close));
        running = true;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return running;
    }

    @Override
    public void close() {
        running = false;
    }

}
