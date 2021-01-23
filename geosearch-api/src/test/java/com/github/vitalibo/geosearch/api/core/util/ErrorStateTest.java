package com.github.vitalibo.geosearch.api.core.util;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class ErrorStateTest {

    private ErrorState errorState;

    @BeforeMethod
    public void setUp() {
        errorState = new ErrorState();
    }

    @Test
    public void testHasErrors() {
        Assert.assertFalse(errorState.hasErrors());

        errorState.addError("foo", "bar");
        Assert.assertTrue(errorState.hasErrors());
    }

    @Test
    public void testAddError() {
        errorState.addError("foo", "bar");

        Assert.assertEquals(errorState.get("foo"), Collections.singletonList("bar"));
    }

}