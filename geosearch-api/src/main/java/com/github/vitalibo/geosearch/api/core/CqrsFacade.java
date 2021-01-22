package com.github.vitalibo.geosearch.api.core;

public interface CqrsFacade<C, R> {

    void handleAsyncCommand(C command);
    void handleAsyncQueryResult(R result);

}
