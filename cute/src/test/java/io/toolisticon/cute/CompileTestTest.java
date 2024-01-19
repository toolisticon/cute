package io.toolisticon.cute;

import io.toolisticon.cute.Cute;
import io.toolisticon.cute.GeneratedFileObjectMatcher;
import io.toolisticon.cute.JavaFileObjectUtils;
import io.toolisticon.cute.UnitTest;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;

public class CompileTestTest {

    @Test
    public void test_UnitTest_checkMatchingFileObject() {

        Cute.unitTest()
                        .given().useCompilerOptions("-verbose  ", " -source    1.7   ", "-target 1.7")
                .when().passInElement().<TypeElement>fromSourceFile("/AnnotationProcessorUnitTestTestClass.java")
                .intoUnitTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {

                        try {

                            FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt");
                            Writer writer = fileObject.openWriter();
                            writer.write("TATA!");
                            writer.close();


                        } catch (IOException ignored) {

                        }

                    }
                })
                .thenExpectThat().compilationSucceeds()
                .andThat().fileObject(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt").exists()

                .andThat().fileObject(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt").equals( JavaFileObjectUtils.readFromString("TATA!"))

                .andThat().fileObject(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt").matches( new GeneratedFileObjectMatcher() {
                    @Override
                    public boolean check(FileObject fileObject) throws IOException {
                        return fileObject.getCharContent(false).toString().contains("TAT");
                    }
                })


                .executeTest();

    }

    /*-
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
                            JavaFileObject javaFileObject = processingEnvironment.getFiler().createSourceFile("io.toolisticon.cute.CheckTest");
                            Writer writer = javaFileObject.openWriter();
                            writer.write("package io.toolisticon.cute;\n");
                            writer.write("public class CheckTest{}");
                            writer.flush();
                            writer.close();

                        } catch (IOException e) {
                            throw new RuntimeException("WTF: " + e.getMessage(), e);
                        }

                    }
                })

                .compilationShouldSucceed()
                .expectThatJavaFileObjectExists(StandardLocation.CLASS_OUTPUT, "io.toolisticon.cute.CheckTest", JavaFileObject.Kind.CLASS)
                .expectThatGeneratedClassExists("io.toolisticon.cute.CheckTest")
                .expectThatJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.cute.CheckTest", JavaFileObject.Kind.SOURCE)
                .expectThatJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.cute.CheckTest", JavaFileObject.Kind.SOURCE, JavaFileObjectUtils.readFromString("xyz", "package io.toolisticon.cute;\npublic class CheckTest{}"))
                .expectThatJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.cute.CheckTest", JavaFileObject.Kind.SOURCE, new GeneratedFileObjectMatcher() {
                    @Override
                    public boolean check(FileObject fileObject) throws IOException {
                        return fileObject.getCharContent(false).toString().contains("public class CheckTest{}");
                    }
                })
                .expectThatGeneratedSourceFileExists("io.toolisticon.cute.CheckTest")
                .expectThatGeneratedSourceFileExists("io.toolisticon.cute.CheckTest", JavaFileObjectUtils.readFromString("xyz", "package io.toolisticon.cute;\npublic class CheckTest{}"))
                .expectThatGeneratedSourceFileExists("io.toolisticon.cute.CheckTest", new GeneratedFileObjectMatcher() {
                    @Override
                    public boolean check(FileObject fileObject) throws IOException {
                        return fileObject.getCharContent(false).toString().contains("public class CheckTest{}");
                    }
                })
                .expectThatGeneratedSourceFileDoesntExist("io.toolisticon.cute.CheckTestNotExistent")
                .expectThatFileObjectDoesntExist(StandardLocation.SOURCE_OUTPUT, "io.toolisticon.cute", "SomethingThatDoesntExist.txt")
                .executeTest();

    }

    @Test(expected = InvalidTestConfigurationException.class)
    public void executeTest_CompilationSucceedAndErrorMessageExpectedShouldThowInvalidTestConfigurationException() {
        CompileTestBuilder.unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {

                    }
                }).compilationShouldSucceed().expectErrorMessageThatContains("XXX").executeTest();
    }

    @Test
    public void executeTest_expectedCompilationShouldHaveSucceededButFailed() {
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

    private static class PassInTestClass_MethodParameter {

        public void testMethod(@PassIn String attribute) {

        }

    }

    @Test
    public void executeTest_expectedPassInToWork_withMethodParameter() {


        CompileTestBuilder.unitTest()
                .defineTestWithPassedInElement(PassInTestClass_MethodParameter.class, new UnitTest<VariableElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, VariableElement element) {

                        MatcherAssert.assertThat(element, Matchers.notNullValue());
                        MatcherAssert.assertThat(element.getKind(), Matchers.is(ElementKind.PARAMETER));


                    }
                })
                .compilationShouldSucceed()
                .executeTest();


    }

    private static class PassInTestClass_ConstructorParameter {

        public PassInTestClass_ConstructorParameter(@PassIn String attribute) {

        }

    }

    @Test
    public void executeTest_expectedPassInToWork_withConstructorParameter() {


        CompileTestBuilder.unitTest()
                .defineTestWithPassedInElement(PassInTestClass_ConstructorParameter.class, new UnitTest<VariableElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, VariableElement element) {

                        MatcherAssert.assertThat(element, Matchers.notNullValue());
                        MatcherAssert.assertThat(element.getKind(), Matchers.is(ElementKind.PARAMETER));

                    }
                })
                .compilationShouldSucceed()
                .executeTest();


    }

    private static class PassInTestClass_Constructor {

        @PassIn
        public PassInTestClass_Constructor(String attribute) {

        }

    }

    @Test
    public void executeTest_expectedPassInToWork_withConstructor() {


        CompileTestBuilder.unitTest()
                .defineTestWithPassedInElement(PassInTestClass_Constructor.class, new UnitTest<ExecutableElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, ExecutableElement element) {

                        MatcherAssert.assertThat(element, Matchers.notNullValue());
                        MatcherAssert.assertThat(element.getParameters(), Matchers.hasSize(1));
                        MatcherAssert.assertThat(element.getKind(), Matchers.is(ElementKind.CONSTRUCTOR));

                    }
                })
                .compilationShouldSucceed()
                .executeTest();


    }

    @PassIn
    private static class PassInTestClass_WithMultiplePassInAnnotations {

        @PassIn
        public PassInTestClass_WithMultiplePassInAnnotations(String attribute) {

        }

    }

    @Test
    public void executeTest_expectedPassInToFail_withMultiplePassInAnnotations() {

        boolean assertionErrorWasThrown = false;
        try {
            CompileTestBuilder.unitTest()
                    .defineTestWithPassedInElement(PassInTestClass_WithMultiplePassInAnnotations.class, new UnitTest<ExecutableElement>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, ExecutableElement element) {

                        }
                    })
                    .compilationShouldSucceed()
                    .executeTest();

        } catch (AssertionError e) {
            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.UNIT_TEST_PASS_IN_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT.getMessagePattern());
            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertionError about 'expecting compilation to fail but was successful' should have been thrown", assertionErrorWasThrown);

    }

    @Test
    public void executeTest_shouldThrowUnexpectedClassCastExceptionCorrectly() {

        boolean assertionErrorWasThrown = false;
        try {
            CompileTestBuilder.unitTest()
                    .defineTest(new UnitTest<Element>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, Element element) {
                            throw new ClassCastException();
                        }
                    })
                    .executeTest();

        } catch (AssertionError e) {
            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.ASSERTION_GOT_UNEXPECTED_EXCEPTION.getMessagePattern());
            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("Should get unexpected ClassCastException assertion error", assertionErrorWasThrown);

    }

    @Test
    public void executeTest_shouldHandleExpectedClassCastExceptionCorrectly() {


        CompileTestBuilder.unitTest()
                .defineTest(new UnitTest<Element>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element element) {
                        throw new ClassCastException();
                    }
                })
                .expectedThrownException(ClassCastException.class)
                .executeTest();


    }

    */
}
