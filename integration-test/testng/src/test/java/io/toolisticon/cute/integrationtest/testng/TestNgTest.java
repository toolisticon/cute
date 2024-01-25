package io.toolisticon.cute.integrationtest.testng;

import io.toolisticon.cute.Cute;
import io.toolisticon.cute.UnitTest;
import io.toolisticon.cute.UnitTestWithoutPassIn;
import io.toolisticon.cute.extension.api.AssertionSpiServiceLocator;
import io.toolisticon.cute.extension.testng.TestNGAssertion;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Test class to test testng extension.
 */
public class TestNgTest {

    @Test
    public void testServiceLocator() {

        MatcherAssert.assertThat(AssertionSpiServiceLocator.locate().getClass(), Matchers.is((Class) TestNGAssertion.class));

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
    public void successfullFailingCompilationTest_ByErrorMessage() {

        Cute
                .unitTest()
                .when(new UnitTestWithoutPassIn() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR!");
                    }
                })
                .thenExpectThat().compilationFails()
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

            Assert.fail("Should have failed");
        } catch (AssertionError error) {
            MatcherAssert.assertThat(error.getMessage(), Matchers.containsString("Compilation should have succeeded but failed"));
        }

    }

}
