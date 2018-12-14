package io.toolisticon.compiletesting.extension.plainjava;


import io.toolisticon.compiletesting.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;

@Service(value = AssertionSpi.class, priority = 0, description = "Java's AssertionError should work with all testing frameworks")
public class AssertionErrorAssertion implements AssertionSpi {
    @Override
    public void fail(String message) {
        throw new AssertionError(message);
    }
}
