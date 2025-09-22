package io.toolisticon.cute.extension.junit4;

import java.lang.AssertionError;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

@Service(value = AssertionSpi.class, priority = -5, description = "junit 4 assertion framework")
public class JUnit4Assertion implements AssertionSpi {
    
	private final static Set<Class<? extends Throwable>> ASSERTION_TYPES = new HashSet<>();
	static {
		ASSERTION_TYPES.add(AssertionError.class);
	}
	
	@Override
    public void fail(String message) {
        Assert.fail(message);
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
