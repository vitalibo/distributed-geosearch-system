package com.github.vitalibo.geosearch.api.core.util;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class Rules<T> {

    private final List<BiConsumer<T, ErrorState>> rules; // NOPMD

    @SafeVarargs
    public Rules(BiConsumer<T, ErrorState>... rules) {
        this.rules = Arrays.asList(rules);
    }

    public void verify(T command) {
        verify(rules, command);
    }

    private static <S> void verify(List<BiConsumer<S, ErrorState>> rules, S command) {
        final ErrorState errorState = new ErrorState();
        for (BiConsumer<S, ErrorState> rule : rules) {
            rule.accept(command, errorState);
            if (errorState.hasErrors()) {
                throw new ValidationException(errorState);
            }
        }
    }

}
