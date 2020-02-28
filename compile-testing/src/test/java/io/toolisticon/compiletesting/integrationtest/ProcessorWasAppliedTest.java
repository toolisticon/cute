package io.toolisticon.compiletesting.integrationtest;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import io.toolisticon.compiletesting.common.SimpleTestProcessor1;
import io.toolisticon.compiletesting.common.SimpleTestProcessor2;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration test to check if annotation processor was detected as applied correctly.
 */
public class ProcessorWasAppliedTest {

    @Test
    public void concreteProcessorClassInstance_wasApplied() {

        CompileTestBuilder
                .unitTest()
                .useProcessor(new SimpleTestProcessor1())
                .useSource(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                .executeTest();


    }

    @Test
    public void concreteProcessorClassInstance_wasNotApplied() {
        boolean assertionErrorWasTriggered = false;
        try {
            CompileTestBuilder
                    .unitTest()
                    .useProcessor(new SimpleTestProcessor2())
                    .useSource(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                    .executeTest();

        } catch (AssertionError e) {
            assertionErrorWasTriggered = true;

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Annotation processor " + SimpleTestProcessor2.class.getCanonicalName() + " hasn't been called"));

        }

        MatcherAssert.assertThat("AssertionError should have been triggered", assertionErrorWasTriggered);

    }

    @Test
    public void anonymousProcessorClassInstanceOfProcessor_wasApplied() {
        CompileTestBuilder
                .unitTest()
                .useProcessor(new SimpleTestProcessor1() {
                })
                .useSource(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                .executeTest();

    }

    @Test
    public void anonymousProcessorClassInstanceOfProcessor_wasNotApplied() {
        boolean assertionErrorWasTriggered = false;
        try {
            CompileTestBuilder
                    .unitTest()
                    .useProcessor(new SimpleTestProcessor2() {
                    })
                    .useSource(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                    .executeTest();

        } catch (AssertionError e) {
            assertionErrorWasTriggered = true;

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Annotation processor null hasn't been called"));

        }

        MatcherAssert.assertThat("AssertionError should have been triggered", assertionErrorWasTriggered);

    }

    @Test
    public void anonymousProcessorClassInstanceOfClass_wasApplied() {

        CompileTestBuilder
                .compilationTest()
                .addProcessors(SimpleTestProcessor1.class)
                .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                .executeTest();

    }

    @Test
    public void anonymousProcessorClassInstanceOfClass_wasNotApplied() {

        boolean assertionErrorWasTriggered = false;
        try {

            CompileTestBuilder
                    .compilationTest()
                    .addProcessors(SimpleTestProcessor2.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/AnnotationProcessorAppliedTestClass.java"))
                    .executeTest();

        } catch (AssertionError e) {
            assertionErrorWasTriggered = true;

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Annotation processor " + SimpleTestProcessor2.class.getCanonicalName() + " hasn't been called"));

        }

        MatcherAssert.assertThat("AssertionError should have been triggered", assertionErrorWasTriggered);


    }

}
