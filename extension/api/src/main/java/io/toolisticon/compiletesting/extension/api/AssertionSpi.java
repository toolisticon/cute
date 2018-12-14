package io.toolisticon.compiletesting.extension.api;

import io.toolisticon.spiap.api.Spi;

/**
 * Spi to set a failing assertion during compile testing.
 */
@Spi
public interface AssertionSpi {

    /**
     * Triggers a failing assertion.
     *
     * @param message the failing assertion message
     */
    void fail(String message);

}
