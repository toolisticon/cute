package io.toolisticon.compiletesting.extension.junit5;

import io.toolisticon.compiletesting.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;
import org.junit.jupiter.api.Assertions;


@Service(value = AssertionSpi.class, priority = -10, description = "junit 5 assertion framework")
public class JUnit5Assertion implements AssertionSpi {
    @Override
    public void fail(String message) {
        Assertions.fail(message);
    }
}
