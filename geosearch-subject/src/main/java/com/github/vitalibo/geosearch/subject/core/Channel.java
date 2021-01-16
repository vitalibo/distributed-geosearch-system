package com.github.vitalibo.geosearch.subject.core;

@FunctionalInterface
public interface Channel<T> extends AutoCloseable {

    void send(T t);

    @Override
    default void close() {
    }

}
