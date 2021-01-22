package com.github.vitalibo.geosearch.api.core;

import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;

public interface GeoSearchEngine {

    void submit(GeoSearchCommand command);

}
