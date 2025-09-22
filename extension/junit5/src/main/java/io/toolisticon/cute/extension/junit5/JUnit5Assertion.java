package io.toolisticon.cute.extension.junit5;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;


@Service(value = AssertionSpi.class, priority = -10, description = "junit 5 assertion framework")
public class JUnit5Assertion implements AssertionSpi {
	
	private final static Set<Class<? extends Throwable>> ASSERTION_TYPES = new HashSet<>();
	static {
		ASSERTION_TYPES.add(AssertionError.class);
	}
	
    @Override
    public void fail(String message) {
        Assertions.fail(message);
    }
    
    @Override
	public void fail(String message, Throwable cause) {
    	Assertions.fail(message, cause);
	}
    
	@Override
	public Set<Class<? extends Throwable>> getSupportedAssertionTypes() {
		return ASSERTION_TYPES;
	}
    
}
