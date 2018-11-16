package io.toolisticon.compiletesting.extension.junit5;

import io.toolisticon.compiletesting.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;
import org.testng.Assert;


@Service(value = AssertionSpi.class, priority = -10, description = "TestNG assertion framework")
public class TestNGAssertion implements AssertionSpi {
    @Override
    public void fail(String message) {
        Assert.fail(message);
    }
}
