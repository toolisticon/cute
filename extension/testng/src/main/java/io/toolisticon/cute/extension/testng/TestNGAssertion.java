package io.toolisticon.cute.extension.testng;

import io.toolisticon.cute.extension.api.AssertionSpi;
import io.toolisticon.spiap.api.Service;

import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;


@Service(value = AssertionSpi.class, priority = -10, description = "TestNG assertion framework")
public class TestNGAssertion implements AssertionSpi {
	
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
    	Assert.fail(message, cause);
	}
    
	@Override
	public Set<Class<? extends Throwable>> getSupportedAssertionTypes() {
		return ASSERTION_TYPES;
	}
}
