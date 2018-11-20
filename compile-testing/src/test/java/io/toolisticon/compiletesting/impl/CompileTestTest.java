package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import io.toolisticon.compiletesting.UnitTestProcessor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
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

                    .addGeneratedFileObjectExistsCheck(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt")
                    .addGeneratedFileObjectExistsCheck(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", JavaFileObjectUtils.readFromString("XXX", "TATA!"))
                    .addGeneratedFileObjectExistsCheck(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", new GeneratedFileObjectMatcher<FileObject>() {
                        @Override
                        public boolean check(FileObject fileObject) throws IOException {
                            return fileObject.getCharContent(false).toString().contains("TAT");
                        }
                    })
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
                    .addGeneratedFileObjectExistsCheck(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", JavaFileObjectUtils.readFromString("test", "WURST!"))
                    .testCompilation();

            Assert.fail("Should have triggered an assertion error");

        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("exists but doesn't match expected FileObject"));

        }


    }

    @Test
    public void test_JavaFileObjectExists() {
        CompileTestBuilder.createCompileTestBuilder()
                .unitTest()
                .useProcessor(new UnitTestProcessor() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                        try {
                            JavaFileObject javaFileObject = processingEnvironment.getFiler().createSourceFile("io.toolisticon.compiletesting.CheckTest");
                            Writer writer = javaFileObject.openWriter();
                            writer.write("package io.toolisticon.compiletesting;\n");
                            writer.write("public class CheckTest{}");
                            writer.flush();
                            writer.close();

                        } catch (IOException e) {
                            throw new RuntimeException("WTF: " + e.getMessage(), e);
                        }

                    }
                })

                .compilationShouldSucceed()
                .addGeneratedJavaFileObjectExistsCheck(StandardLocation.CLASS_OUTPUT, "io.toolisticon.compiletesting.CheckTest", JavaFileObject.Kind.CLASS)
                .addGeneratedJavaFileObjectExistsCheck(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.compiletesting.CheckTest", JavaFileObject.Kind.SOURCE)
                .addGeneratedJavaFileObjectExistsCheck(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.compiletesting.CheckTest", JavaFileObject.Kind.SOURCE, JavaFileObjectUtils.readFromString("xyz", "package io.toolisticon.compiletesting;\npublic class CheckTest{}"))
                .addGeneratedJavaFileObjectExistsCheck(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.compiletesting.CheckTest", JavaFileObject.Kind.SOURCE, new GeneratedFileObjectMatcher<JavaFileObject>() {
                    @Override
                    public boolean check(JavaFileObject fileObject) throws IOException {
                        return fileObject.getCharContent(false).toString().contains("public class CheckTest{}");
                    }
                })
                .testCompilation();

    }
}
