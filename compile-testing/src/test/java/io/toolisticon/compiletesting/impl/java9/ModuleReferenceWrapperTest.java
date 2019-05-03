package io.toolisticon.compiletesting.impl.java9;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ModuleReferenceWrapperTest {

    @Test
    public void test_class () {

        if (!Java9SupportCheck.UNSUPPORTED_JAVA_VERSION) {

            MatcherAssert.assertThat(ModuleReferenceWrapper.MODULE_REFERENCE_CLASS, Matchers.notNullValue());

        } else {
            System.out.println("!!! TEST SKIPPED !!!");
        }

    }

    @Test
    public void test_methodDescriptor () {

        if (!Java9SupportCheck.UNSUPPORTED_JAVA_VERSION) {

            MatcherAssert.assertThat(ModuleReferenceWrapper.METHOD_DESCRIPTOR, Matchers.notNullValue());

        } else {
            System.out.println("!!! TEST SKIPPED !!!");
        }

    }

    @Test
    public void test_methodLocation () {

        if (!Java9SupportCheck.UNSUPPORTED_JAVA_VERSION) {

            MatcherAssert.assertThat(ModuleReferenceWrapper.METHOD_LOCATION, Matchers.notNullValue());

        } else {
            System.out.println("!!! TEST SKIPPED !!!");
        }

    }

}
