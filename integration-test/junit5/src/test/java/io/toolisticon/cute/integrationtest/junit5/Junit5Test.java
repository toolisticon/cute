package io.toolisticon.cute.integrationtest.junit5;


import io.toolisticon.cute.Cute;
import io.toolisticon.cute.UnitTestWithoutPassIn;
import io.toolisticon.cute.extension.api.AssertionSpiServiceLocator;
import io.toolisticon.cute.extension.junit5.JUnit5Assertion;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

/**
 * Test class to test testng extension.
 */
public class Junit5Test {

    @Test
    public void testServiceLocator() {

        MatcherAssert.assertThat(AssertionSpiServiceLocator.locate().getClass(), Matchers.is((Class) JUnit5Assertion.class));

    }

    @Test
    public void warningMessageTest() {

        Cute
                .unitTest()
                .when(new UnitTestWithoutPassIn() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "WARNING!");
                    }
                })
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().contains("WARNING!")
                .executeTest();


    }

    @Test
    public void successfulFailingCompilationTest_ByErrorMessage() {

        Cute
                .unitTest()
                .when(new UnitTestWithoutPassIn() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR!");
                    }
                })
                .thenExpectThat()
                .compilationFails()
                .andThat().compilerMessage().ofKindError().contains("ERROR!")
                .executeTest();


    }

    @Test
    public void failingCompilationTest_ByErrorMessage() {

        try {
            Cute
                    .unitTest()
                    .when(new UnitTestWithoutPassIn() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment) {
                            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR!");
                        }
                    })
                    .thenExpectThat().compilationSucceeds()
                    .executeTest();

            Assertions.fail("Should have failed");
        } catch (AssertionError error) {
            Assertions.assertTrue(error.getMessage().startsWith("Compilation should have succeeded but failed"));
        }

    }

}
