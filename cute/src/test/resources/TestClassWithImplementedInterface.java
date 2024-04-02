package io.toolisticon.cute;

import io.toolisticon.cute.TestAnnotation;
import io.toolisticon.cute.testcases.SimpleTestInterface;

/**
 * Test class for annotation processor tools.
 */
@TestAnnotation
public class TestClassWithImplementedInterface implements SimpleTestInterface {

    @Override
    public String saySomething() {
        return "WHATS UP?";
    }
}
