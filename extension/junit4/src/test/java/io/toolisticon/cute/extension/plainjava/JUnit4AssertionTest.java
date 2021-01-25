package io.toolisticon.cute.extension.plainjava;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.cute.extension.api.AssertionSpiServiceLocator;
import io.toolisticon.cute.extension.junit4.JUnit4Assertion;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link JUnit4Assertion}.
 */
public class JUnit4AssertionTest {

    @Test
    public void testServiceLocator() {

        MatcherAssert.assertThat(AssertionSpiServiceLocator.locate().getClass(), Matchers.is((Class) JUnit4Assertion.class));

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
