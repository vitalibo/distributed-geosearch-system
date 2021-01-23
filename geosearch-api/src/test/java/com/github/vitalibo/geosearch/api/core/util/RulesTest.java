package com.github.vitalibo.geosearch.api.core.util;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.function.BiConsumer;

public class RulesTest {

    @Mock
    private BiConsumer<String, ErrorState> mockRule;

    private Rules<String> rules;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        rules = new Rules<>(mockRule);
    }

    @Test
    public void testApplyRules() {
        rules.verify("foo");

        Mockito.verify(mockRule).accept(Mockito.eq("foo"), Mockito.any());
    }

    @Test
    public void testVerify() {
        Rules<String> rules = new Rules<>(
            Collections.singletonList((request, errorState) -> errorState.addError("foo", request)));

        ValidationException actual = Assert.expectThrows(ValidationException.class, () -> rules.verify("bar"));

        ErrorState errorState = actual.getErrorState();
        Assert.assertEquals(errorState.get("foo"), Collections.singletonList("bar"));
    }

}
