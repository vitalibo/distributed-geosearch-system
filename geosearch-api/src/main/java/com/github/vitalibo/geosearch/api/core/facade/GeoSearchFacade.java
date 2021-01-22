package com.github.vitalibo.geosearch.api.core.facade;

import com.github.vitalibo.geosearch.api.core.CqrsFacade;
import com.github.vitalibo.geosearch.api.core.GeoSearchEngine;
import com.github.vitalibo.geosearch.api.core.UserInterface;
import com.github.vitalibo.geosearch.api.core.ValidationRules;
import com.github.vitalibo.geosearch.api.core.model.GeoEvent;
import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.api.core.model.GeoSearchResult;
import com.github.vitalibo.geosearch.api.core.util.Rules;
import com.github.vitalibo.geosearch.api.core.util.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@RequiredArgsConstructor
public class GeoSearchFacade implements CqrsFacade<GeoSearchCommand, GeoSearchResult> {

    private final Rules<GeoSearchCommand> rules;
    private final Map<UUID, String> sessions;
    private final GeoSearchEngine engine;
    private final UserInterface ui;

    public GeoSearchFacade(GeoSearchEngine engine, UserInterface ui) {
        this(
            new Rules<>(
                ValidationRules::verifyBoundingBoxIsValidGeoJson,
                ValidationRules::verifyBoundingBoxContainsPolygon,
                ValidationRules::verifyPolygonHasCorrectGeoCoordinates),
            new HashMap<>(), engine, ui);
    }

    @Override
    public void handleAsyncCommand(GeoSearchCommand command) {
        try {
            rules.verify(command);

            final BiConsumer<UUID, String> consumer =
                command.isDisconnect() ? sessions::remove : sessions::put;
            consumer.accept(command.getId(), command.getSessionId());

            engine.submit(command);
        } catch (ValidationException e) {
            ui.notifyError(command.getSessionId(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ui.notifyError(command.getSessionId(), e);
        }
    }

    @Override
    public void handleAsyncQueryResult(GeoSearchResult result) {
        String sessionId = sessions.get(result.getId());

        for (GeoEvent event : result.getEvents()) {
            ui.notify(sessionId, event);
        }
    }

}
