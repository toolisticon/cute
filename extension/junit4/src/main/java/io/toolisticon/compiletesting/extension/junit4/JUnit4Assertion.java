package io.toolisticon.compiletesting.extension.junit4;

import io.toolisticon.compiletesting.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;
import org.junit.Assert;

@Service(value = AssertionSpi.class, priority = 0, description = "junit 4 is default assertion framework")
public class JUnit4Assertion implements AssertionSpi {
    @Override
    public void fail(String message) {
        Assert.fail(message);
    }
}
