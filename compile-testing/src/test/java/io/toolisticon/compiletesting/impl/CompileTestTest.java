package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import io.toolisticon.compiletesting.UnitTestProcessor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;

public class CompileTestTest {

    @Test
    public void test_UnitTest_checkMatchingFileObject() {


        try {
            CompileTestBuilder.createCompileTestBuilder()
                    .unitTest()
                    .useProcessor(new UnitTestProcessor() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                            try {

                                FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", typeElement);
                                Writer writer = fileObject.openWriter();
                                writer.write("TATA!");
                                writer.close();


                            } catch (IOException e) {

                            }

                        }
                    })

                    .compilationShouldSucceed()

                    .addExpectedGeneratedFileObjects(JavaFileObjectUtils.readFromString("test", "TATA!"))
                    .testCompilation();
        } catch (AssertionError e) {
            Assert.fail("Should not have thrown an AssertionError");
        }

    }

    @Test
    public void test_UnitTest_checkNonMatchingFileObject() {


        try {
            CompileTestBuilder.createCompileTestBuilder()
                    .unitTest()
                    .useProcessor(new UnitTestProcessor() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {
                            try {
                                FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", typeElement);
                                Writer writer = fileObject.openWriter();
                                writer.write("TATA!");
                                writer.close();


                            } catch (IOException e) {

                            }

                        }
                    })

                    .compilationShouldSucceed()

                    .addExpectedGeneratedFileObjects(JavaFileObjectUtils.readFromString("test", "WURST!"))
                    .testCompilation();

            Assert.fail("Should have triggered an assertion error");

        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Expected generated FileObject can't be found"));

        }


    }
}
