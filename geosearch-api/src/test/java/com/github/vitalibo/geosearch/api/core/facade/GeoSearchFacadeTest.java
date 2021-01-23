package com.github.vitalibo.geosearch.api.core.facade;

import com.github.vitalibo.geosearch.api.core.GeoSearchEngine;
import com.github.vitalibo.geosearch.api.core.UserInterface;
import com.github.vitalibo.geosearch.api.core.model.GeoEvent;
import com.github.vitalibo.geosearch.api.core.model.GeoSearchCommand;
import com.github.vitalibo.geosearch.api.core.model.GeoSearchResult;
import com.github.vitalibo.geosearch.api.core.util.Rules;
import com.github.vitalibo.geosearch.api.core.util.ValidationException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GeoSearchFacadeTest {

    @Mock
    private Rules<GeoSearchCommand> mockRules;
    @Mock
    private Map<UUID, String> mockSessions;
    @Mock
    private GeoSearchEngine mockGeoSearchEngine;
    @Mock
    private UserInterface mockUserInterface;

    private GeoSearchFacade facade;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        facade = new GeoSearchFacade(mockRules, mockSessions, mockGeoSearchEngine, mockUserInterface);
    }

    @Test
    public void testHandleAsyncCommandRulesFails() {
        Mockito.doThrow(ValidationException.class).when(mockRules).verify(Mockito.any());
        GeoSearchCommand command = new GeoSearchCommand()
            .withSessionId("foo");

        facade.handleAsyncCommand(command);

        Mockito.verify(mockRules).verify(command);
        Mockito.verify(mockSessions, Mockito.never()).put(Mockito.any(), Mockito.any());
        Mockito.verify(mockSessions, Mockito.never()).remove(Mockito.<UUID>any(), Mockito.<String>any());
        Mockito.verify(mockGeoSearchEngine, Mockito.never()).submit(Mockito.any());
        Mockito.verify(mockUserInterface).notifyError(Mockito.eq("foo"), Mockito.any(ValidationException.class));
    }

    @Test
    public void testHandleAsyncCommandFails() {
        Mockito.doThrow(RuntimeException.class).when(mockGeoSearchEngine).submit(Mockito.any());
        GeoSearchCommand command = new GeoSearchCommand()
            .withSessionId("foo");

        facade.handleAsyncCommand(command);

        Mockito.verify(mockRules).verify(command);
        Mockito.verify(mockSessions).put(Mockito.any(), Mockito.eq("foo"));
        Mockito.verify(mockSessions, Mockito.never()).remove(Mockito.<UUID>any(), Mockito.<String>any());
        Mockito.verify(mockGeoSearchEngine).submit(command);
        Mockito.verify(mockUserInterface).notifyError(Mockito.eq("foo"), Mockito.any(RuntimeException.class));
    }

    @Test
    public void testHandleAsyncCommandSubscribe() {
        GeoSearchCommand command = new GeoSearchCommand()
            .withSessionId("foo")
            .withBoundingBox("GeoJSON");

        facade.handleAsyncCommand(command);

        Mockito.verify(mockRules).verify(command);
        Mockito.verify(mockSessions).put(Mockito.any(), Mockito.eq("foo"));
        Mockito.verify(mockSessions, Mockito.never()).remove(Mockito.<UUID>any(), Mockito.<String>any());
        Mockito.verify(mockGeoSearchEngine).submit(command);
        Mockito.verify(mockUserInterface, Mockito.never()).notifyError(Mockito.any(), Mockito.any(RuntimeException.class));
    }

    @Test
    public void testHandleAsyncCommandUnsubscribe() {
        GeoSearchCommand command = new GeoSearchCommand()
            .withSessionId("foo")
            .withDisconnect(true);

        facade.handleAsyncCommand(command);

        Mockito.verify(mockRules).verify(command);
        Mockito.verify(mockSessions, Mockito.never()).put(Mockito.any(), Mockito.any());
        Mockito.verify(mockSessions).remove(Mockito.<UUID>any(), Mockito.eq("foo"));
        Mockito.verify(mockGeoSearchEngine).submit(command);
        Mockito.verify(mockUserInterface, Mockito.never()).notifyError(Mockito.any(), Mockito.any(RuntimeException.class));
    }

    @Test
    public void testHandleAsyncQueryResult() {
        Mockito.when(mockSessions.get(Mockito.<UUID>any())).thenReturn("foo");
        List<GeoEvent> events = Arrays.asList(
            new GeoEvent()
                .withId(UUID.fromString("35416c27-aa93-407c-836c-3b798ac8bf53")),
            new GeoEvent()
                .withId(UUID.fromString("1583b966-185b-43f2-8ba9-d46923022a95")));
        GeoSearchResult result = new GeoSearchResult()
            .withId(UUID.fromString("771f58fa-1fc4-4c7c-a7a9-71681d1d8642"))
            .withEvents(events);

        facade.handleAsyncQueryResult(result);

        Mockito.verify(mockUserInterface).notify("foo", events.get(0));
        Mockito.verify(mockUserInterface).notify("foo", events.get(1));
    }

}
