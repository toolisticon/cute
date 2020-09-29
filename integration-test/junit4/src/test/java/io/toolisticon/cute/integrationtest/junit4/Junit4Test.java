package io.toolisticon.cute.integrationtest.junit4;


import io.toolisticon.cute.CompileTestBuilder;
import io.toolisticon.cute.UnitTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Test class to test junit 4 extension.
 */
public class Junit4Test {

    @Test
    public void warningMessageTest() {

        CompileTestBuilder
                .unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "WARNING!");
                    }
                })
                .expectWarningMessageThatContains("WARNING!")
                .compilationShouldSucceed()
                .executeTest();


    }

    @Test
    public void successfullFailingCompilationTest_ByErrorMessage() {

        CompileTestBuilder
                .unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR!");
                    }
                })
                .expectErrorMessageThatContains("ERROR!")
                .compilationShouldFail()
                .executeTest();


    }

    @Test
    public void failingCompilationTest_ByErrorMessage() {

        try {
            CompileTestBuilder
                    .unitTest()
                    .defineTest(new UnitTest() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {
                            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR!");
                        }
                    })
                    .compilationShouldSucceed()
                    .executeTest();

            Assert.fail("Should have failed");
        } catch (AssertionError error) {
            MatcherAssert.assertThat(error.getMessage(), Matchers.containsString("Compilation should have succeeded but failed"));
        }

    }

}
