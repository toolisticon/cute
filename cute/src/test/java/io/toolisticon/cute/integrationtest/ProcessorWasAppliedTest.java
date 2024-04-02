package io.toolisticon.cute.integrationtest;

import io.toolisticon.cute.Cute;
import io.toolisticon.cute.JavaFileObjectUtils;
import io.toolisticon.cute.common.SimpleTestProcessor1;
import io.toolisticon.cute.common.SimpleTestProcessor2;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration test to check if annotation processor was detected as applied correctly.
 */
public class ProcessorWasAppliedTest {

    @Test
    public void concreteProcessorClassInstance_wasApplied() {

        Cute.blackBoxTest()
                .given().processor(SimpleTestProcessor1.class)
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                .whenCompiled()
                .thenExpectThat().compilationSucceeds()
                .executeTest();

    }

    @Test
    public void concreteProcessorClassInstance_wasNotApplied() {
        boolean assertionErrorWasTriggered = false;
        try {
            Cute.blackBoxTest()
                    .given().processor(SimpleTestProcessor2.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                    .executeTest();

        } catch (AssertionError e) {
            assertionErrorWasTriggered = true;

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Annotation processor " + SimpleTestProcessor2.class.getCanonicalName() + " hasn't been called"));

        }

        MatcherAssert.assertThat("AssertionError should have been triggered", assertionErrorWasTriggered);

    }

    @Test
    public void anonymousProcessorClassInstanceOfClass_wasApplied() {

        Cute.blackBoxTest()
                .given().processor(SimpleTestProcessor1.class)
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                .executeTest();

    }

    @Test
    public void anonymousProcessorClassInstanceOfClass_wasNotApplied() {

        boolean assertionErrorWasTriggered = false;
        try {

            Cute.blackBoxTest()
                    .given().processor(SimpleTestProcessor2.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                    .executeTest();

        } catch (AssertionError e) {
            assertionErrorWasTriggered = true;
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Annotation processor " + SimpleTestProcessor2.class.getCanonicalName() + " hasn't been called"));

        }

        MatcherAssert.assertThat("AssertionError should have been triggered", assertionErrorWasTriggered);


    }

}
