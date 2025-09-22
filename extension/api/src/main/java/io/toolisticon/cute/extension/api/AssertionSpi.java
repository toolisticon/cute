package io.toolisticon.cute.extension.api;

import java.util.Set;

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
    
    /**
     * Triggers a failing assertion.
     *
     * @param message the failing assertion message
     */
    void fail(String message, Throwable cause);

    /**
     * Get the differt assertion error types used by the framework.
     * @return
     */
    Set<Class<? extends Throwable>> getSupportedAssertionTypes();
    
    
}
