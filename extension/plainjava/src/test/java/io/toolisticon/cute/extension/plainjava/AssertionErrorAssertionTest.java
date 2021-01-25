package io.toolisticon.cute.extension.plainjava;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.cute.extension.api.AssertionSpiServiceLocator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class AssertionErrorAssertionTest {

    @Test
    public void testServiceLocator() {

        MatcherAssert.assertThat(AssertionSpiServiceLocator.locate().getClass(), Matchers.is((Class)AssertionErrorAssertion.class));

    }

    @Test
    public void testTriggeringAssertionError() {

        AssertionSpi service = AssertionSpiServiceLocator.locate();

        try {
            service.fail("TEST");
        } catch (AssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.is("TEST"));
            return;
        }
        Assert.fail("Expected triggered AsserionError");
    }

}
