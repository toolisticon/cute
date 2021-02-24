package io.toolisticon.cute.extension.testng;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.cute.extension.api.AssertionSpiServiceLocator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link TestNGAssertion}.
 */
public class TestNGAssertionTest {

    @Test
    public void testServiceLocator() {

        MatcherAssert.assertThat(AssertionSpiServiceLocator.locate().getClass(), Matchers.is((Class)TestNGAssertion.class));

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
