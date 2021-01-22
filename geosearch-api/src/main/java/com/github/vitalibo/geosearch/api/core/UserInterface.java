package com.github.vitalibo.geosearch.api.core;

import com.github.vitalibo.geosearch.api.core.model.GeoEvent;
import com.github.vitalibo.geosearch.api.core.util.ValidationException;

public interface UserInterface {

    void notify(String sessionId, GeoEvent event);

    void notifyError(String sessionId, Exception exception);
    void notifyError(String sessionId, ValidationException exception);

}
