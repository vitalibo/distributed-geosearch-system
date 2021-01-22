package com.github.vitalibo.geosearch.api.core.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidationException extends RuntimeException {

    @Getter
    private final ErrorState errorState;

}
