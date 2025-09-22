package io.toolisticon.cute.extension.plainjava;

import java.util.HashSet;
import java.util.Set;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;

@Service(value = AssertionSpi.class, priority = 0, description = "Java's AssertionError should work with all testing frameworks")
public class AssertionErrorAssertion implements AssertionSpi {
	
	private final static Set<Class<? extends Throwable>> ASSERTION_TYPES = new HashSet<>();
	static {
		ASSERTION_TYPES.add(AssertionError.class);
	}
	
    @Override
    public void fail(String message) {
        throw new AssertionError(message);
    }
    
    @Override
	public void fail(String message, Throwable cause) {
    	throw new AssertionError(message, cause);
	}
    
    
	@Override
	public Set<Class<? extends Throwable>> getSupportedAssertionTypes() {
		return ASSERTION_TYPES;
	}
    
}
