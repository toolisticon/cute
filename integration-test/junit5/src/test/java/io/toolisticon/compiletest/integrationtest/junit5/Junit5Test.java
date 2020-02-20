package io.toolisticon.compiletest.integrationtest.junit5;


import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.UnitTestProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Test class to test testng extension.
 */
public class Junit5Test {

    @Test
    public void warningMessageTest() {

        CompileTestBuilder
                .unitTest()
                .useProcessor(new UnitTestProcessor() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "WARNING!");
                    }
                })
                .expectedWarningMessages("WARNING!")
                .compilationShouldSucceed()
                .testCompilation();


    }

    @Test
    public void successfullFailingCompilationTest_ByErrorMessage() {

        CompileTestBuilder
                .unitTest()
                .useProcessor(new UnitTestProcessor() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR!");
                    }
                })
                .expectedErrorMessages("ERROR!")
                .compilationShouldFail()
                .testCompilation();


    }

    @Test
    public void failingCompilationTest_ByErrorMessage() {

        try {
            CompileTestBuilder
                    .unitTest()
                    .useProcessor(new UnitTestProcessor() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {
                            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR!");
                        }
                    })
                    .compilationShouldSucceed()
                    .testCompilation();

            Assertions.fail("Should have failed");
        } catch (AssertionError error) {
            Assertions.assertEquals(error.getMessage(), "Compilation should have succeeded but failed");
        }

    }

}
