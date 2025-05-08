package io.toolisticon.cute.extension.plainjava;


import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;
import io.toolisticon.spiap.api.Spi;

@Service(value = AssertionSpi.class, priority = 0, description = "Java's AssertionError should work with all testing frameworks")
public class AssertionErrorAssertion implements AssertionSpi {
    @Override
    public void fail(String message) {
        throw new AssertionError(message);
    }
}
