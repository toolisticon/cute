package io.toolisticon.cute.extension.testng;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;
import org.testng.Assert;


@Service(value = AssertionSpi.class, priority = -10, description = "TestNG assertion framework")
public class TestNGAssertion implements AssertionSpi {
    @Override
    public void fail(String message) {
        Assert.fail(message);
    }
}
