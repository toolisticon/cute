package io.toolisticon.cute.extension.junit4;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;
import org.junit.Assert;

@Service(value = AssertionSpi.class, priority = -5, description = "junit 4 assertion framework")
public class JUnit4Assertion implements AssertionSpi {
    @Override
    public void fail(String message) {
        Assert.fail(message);
    }
}
