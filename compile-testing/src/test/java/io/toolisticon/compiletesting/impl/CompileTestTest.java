package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.Constants;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;
import io.toolisticon.compiletesting.InvalidTestConfigurationException;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import io.toolisticon.compiletesting.TestUtilities;
import io.toolisticon.compiletesting.UnitTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;

public class CompileTestTest {

    @Test
    public void test_UnitTest_checkMatchingFileObject() {


        CompileTestBuilder
                .unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {

                        try {

                            FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", typeElement);
                            Writer writer = fileObject.openWriter();
                            writer.write("TATA!");
                            writer.close();


                        } catch (IOException ignored) {

                        }

                    }
                })
                .useCompilerOptions("-verbose  ", " -source    1.7   ", "-target 1.7")
                .compilationShouldSucceed()

                .expectThatFileObjectExists(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt")
                .expectThatFileObjectExists(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", JavaFileObjectUtils.readFromString("TATA!"))
                .expectThatFileObjectExists(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", new GeneratedFileObjectMatcher<FileObject>() {
                    @Override
                    public boolean check(FileObject fileObject) throws IOException {
                        return fileObject.getCharContent(false).toString().contains("TAT");
                    }
                })
                .executeTest();

    }

    @Test
    public void test_UnitTest_checkNonMatchingFileObject() {


        try {
            CompileTestBuilder
                    .unitTest()
                    .defineTest(new UnitTest() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {
                            try {
                                FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", typeElement);
                                Writer writer = fileObject.openWriter();
                                writer.write("TATA!");
                                writer.close();


                            } catch (IOException ignored) {

                            }

                        }
                    })

                    .compilationShouldSucceed()
                    .expectThatFileObjectExists(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", JavaFileObjectUtils.readFromString("WURST!"))
                    .executeTest();

            Assert.fail("Should have triggered an assertion error");

        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("exists but doesn't match passed GeneratedFileObjectMatcher" +
                    ""));

        }


    }

    @Test
    public void test_JavaFileObjectExists() {
        CompileTestBuilder
                .unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {

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
                .expectThatJavaFileObjectExists(StandardLocation.CLASS_OUTPUT, "io.toolisticon.compiletesting.CheckTest", JavaFileObject.Kind.CLASS)
                .expectThatGeneratedClassExists("io.toolisticon.compiletesting.CheckTest")
                .expectThatJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.compiletesting.CheckTest", JavaFileObject.Kind.SOURCE)
                .expectThatJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.compiletesting.CheckTest", JavaFileObject.Kind.SOURCE, JavaFileObjectUtils.readFromString("xyz", "package io.toolisticon.compiletesting;\npublic class CheckTest{}"))
                .expectThatJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.compiletesting.CheckTest", JavaFileObject.Kind.SOURCE, new GeneratedFileObjectMatcher<JavaFileObject>() {
                    @Override
                    public boolean check(JavaFileObject fileObject) throws IOException {
                        return fileObject.getCharContent(false).toString().contains("public class CheckTest{}");
                    }
                })
                .expectThatGeneratedSourceFileExists("io.toolisticon.compiletesting.CheckTest")
                .expectThatGeneratedSourceFileExists("io.toolisticon.compiletesting.CheckTest", JavaFileObjectUtils.readFromString("xyz", "package io.toolisticon.compiletesting;\npublic class CheckTest{}"))
                .expectThatGeneratedSourceFileExists("io.toolisticon.compiletesting.CheckTest", new GeneratedFileObjectMatcher<JavaFileObject>() {
                    @Override
                    public boolean check(JavaFileObject fileObject) throws IOException {
                        return fileObject.getCharContent(false).toString().contains("public class CheckTest{}");
                    }
                })
                .expectGeneratedSourceFileNotToExist("io.toolisticon.compiletesting.CheckTestNotExistent")
                .expectFileObjectNotToExist(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.compiletesting", "SomethingThatDoesntExist.txt")
                .executeTest();

    }

    @Test(expected = InvalidTestConfigurationException.class)
    public void executeTest_CompilationSucceedAndErrorMessageExpectedShouldThowInvalidTestConfigurationException() {
        CompileTestBuilder.unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {

                    }
                }).compilationShouldSucceed().expectErrorMessagesThatContain("XXX").executeTest();
    }

    @Test
    public void executeTest_expectedComiplationShouldHaveSucceededButFailed() {
        boolean assertionErrorWasThrown = false;
        try {

            CompileTestBuilder.unitTest()
                    .defineTest(new UnitTest() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {
                            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "FAIL!");
                        }
                    })
                    .compilationShouldSucceed()
                    .executeTest();

        } catch (AssertionError e) {
            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_COMPILATION_SHOULD_HAVE_SUCCEEDED_BUT_FAILED.getMessagePattern());
            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertionError about 'expecting compilation to be successful but failed' should have been thrown", assertionErrorWasThrown);


    }

    @Test
    public void executeTest_expectedCompilationShouldHaveFailedButSucceeded() {
        boolean assertionErrorWasThrown = false;
        try {

            CompileTestBuilder.unitTest()
                    .defineTest(new UnitTest() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {

                        }
                    })
                    .compilationShouldFail()
                    .executeTest();

        } catch (AssertionError e) {
            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_COMPILATION_SHOULD_HAVE_FAILED_BUT_SUCCEEDED.getMessagePattern());
            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertionError about 'expecting compilation to fail but was successful' should have been thrown", assertionErrorWasThrown);

    }

}
