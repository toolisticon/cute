package io.toolisticon.cute.extension.junit5;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.cute.extension.api.AssertionSpiServiceLocator;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link JUnit5Assertion}.
 */
public class JUnit5AssertionTest {

    @Test
    public void testServiceLocator() {

        MatcherAssert.assertThat(AssertionSpiServiceLocator.locate().getClass(), Matchers.is((Class) JUnit5Assertion.class));

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
        Assertions.fail("Expected triggered AsserionError");
    }

}
