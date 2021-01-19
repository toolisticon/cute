package io.toolisticon.cute;

import io.toolisticon.cute.common.SimpleTestProcessor1;
import io.toolisticon.cute.common.SimpleTestProcessor2;
import io.toolisticon.cute.impl.CompileTestConfiguration;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CompileTestBuilderTest {

    @Test
    public void test_UnitTest_successfulCompilation_build() {

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = JavaFileObjectUtils.readFromString("Jupp.txt", "TATA!");
        CompileTestBuilder
                .unitTest()
                .defineTest(
                        new UnitTest() {
                            @Override
                            public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {

                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "MANDATORY_WARNING");
                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "WARNING");
                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "NOTE");


                                try {
                                    FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", typeElement);
                                    Writer writer = fileObject.openWriter();
                                    writer.write("TATA!");
                                    writer.close();


                                } catch (IOException e) {

                                }

                            }
                        })
                .expectWarningMessageThatContains("WARNING")
                .expectMandatoryWarningMessageThatContains("MANDATORY_WARNING")
                .expectNoteMessageThatContains("NOTE")
                .compilationShouldSucceed()
                .executeTest();


    }


    @Test
    public void test_UnitTest_successfulCompilation_withInitializedProcessorUnderTest_build() {

        CompileTestBuilder
                .unitTest()
                .defineTest(SimpleTestProcessor1.class, new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, TypeElement>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                        MatcherAssert.assertThat(unit.getProcessingEnvironment(), Matchers.equalTo(processingEnvironment));

                    }
                })
                .compilationShouldSucceed()
                .executeTest();


    }

    @PassIn
    private static class PassInProcessorAndElement {

    }

    @Test
    public void test_UnitTest_successfulCompilation_withInitializedProcessorUnderTestAndPassIn_build() {

        CompileTestBuilder
                .unitTest()
                .defineTestWithPassedInElement(SimpleTestProcessor1.class, PassInProcessorAndElement.class, new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, TypeElement>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                        MatcherAssert.assertThat(typeElement, Matchers.notNullValue());
                        MatcherAssert.assertThat(typeElement.getQualifiedName().toString(), Matchers.is(PassInProcessorAndElement.class.getCanonicalName()));

                    }
                })
                .compilationShouldSucceed()
                .executeTest();


    }

    @Retention(RetentionPolicy.RUNTIME)
    private static @interface CustomPassInAnnotation {

    }

    @CustomPassInAnnotation
    private static class PassInProcessorAndElementWithCustomAnnotation {

    }

    @Test
    public void test_UnitTest_successfulCompilation_withInitializedProcessorUnderTestAndPassInWithCustomAnnotation_build() {

        CompileTestBuilder
                .unitTest()
                .defineTestWithPassedInElement(SimpleTestProcessor1.class, PassInProcessorAndElementWithCustomAnnotation.class, CustomPassInAnnotation.class, new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, TypeElement>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                        MatcherAssert.assertThat(typeElement, Matchers.notNullValue());
                        MatcherAssert.assertThat(typeElement.getQualifiedName().toString(), Matchers.is(PassInProcessorAndElementWithCustomAnnotation.class.getCanonicalName()));

                    }
                })
                .compilationShouldSucceed()
                .executeTest();


    }

    @Test
    public void test_UnitTest_failingCompilation_build() {

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder
                .unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element typeElement) {

                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR");


                    }
                })
                .expectErrorMessageThatContains("ERROR")
                .compilationShouldFail()
                .executeTest();


    }


    private void assertCompilerMessages(Set<CompileTestConfiguration.CompilerMessageCheck> compilerMessageChecks, Diagnostic.Kind kind, CompileTestConfiguration.ComparisionKind comparisionKind, String... expectedMessages) {

        List<String> configuredExpectedMessages = new ArrayList<>();

        Iterator<CompileTestConfiguration.CompilerMessageCheck> iterator = compilerMessageChecks.iterator();
        while (iterator.hasNext()) {
            CompileTestConfiguration.CompilerMessageCheck element = iterator.next();

            MatcherAssert.assertThat(element.getComparisionKind(), Matchers.is(comparisionKind));
            MatcherAssert.assertThat(element.getKind(), Matchers.is(kind));

            configuredExpectedMessages.add(element.getExpectedMessage());

        }

        MatcherAssert.assertThat(configuredExpectedMessages
                , Matchers.containsInAnyOrder(expectedMessages));
    }

    @Test
    public void test_addWarningChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectWarningMessageThatContains("WARN1");


        assertCompilerMessages(builder.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.WARNING, CompileTestConfiguration.ComparisionKind.CONTAINS, "WARN1");

        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectWarningMessageThatContains("WARN2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.WARNING, CompileTestConfiguration.ComparisionKind.CONTAINS, "WARN1", "WARN2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectWarningMessageThatContains()
                .expectWarningMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.WARNING, CompileTestConfiguration.ComparisionKind.CONTAINS, "WARN1", "WARN2");


    }

    public void test_addMandatoryWarningChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectMandatoryWarningMessageThatContains("MWARN1");

        assertCompilerMessages(builder.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.MANDATORY_WARNING, CompileTestConfiguration.ComparisionKind.CONTAINS, "MWARN1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectMandatoryWarningMessageThatContains("MWARN2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.MANDATORY_WARNING, CompileTestConfiguration.ComparisionKind.CONTAINS, "MWARN1", "MWARN2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectMandatoryWarningMessageThatContains()
                .expectMandatoryWarningMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.MANDATORY_WARNING, CompileTestConfiguration.ComparisionKind.CONTAINS, "MWARN1", "MWARN2");


    }

    public void test_addNoteChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectNoteMessageThatContains("NOTE1");


        assertCompilerMessages(builder.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.NOTE, CompileTestConfiguration.ComparisionKind.CONTAINS, "NOTE1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectNoteMessageThatContains("NOTE2");


        assertCompilerMessages(builder2.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.NOTE, CompileTestConfiguration.ComparisionKind.CONTAINS, "NOTE1", "NOTE2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectNoteMessageThatContains()
                .expectNoteMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.NOTE, CompileTestConfiguration.ComparisionKind.CONTAINS, "NOTE1", "NOTE2");


    }

    public void test_addErrorChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectErrorMessageThatContains("ERROR1");


        assertCompilerMessages(builder.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.ERROR, CompileTestConfiguration.ComparisionKind.CONTAINS, "ERROR1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectErrorMessageThatContains("ERROR2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.ERROR, CompileTestConfiguration.ComparisionKind.CONTAINS, "ERROR1", "ERROR2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectErrorMessageThatContains()
                .expectErrorMessageThatContains(null);

        assertCompilerMessages(builder3.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.ERROR, CompileTestConfiguration.ComparisionKind.CONTAINS, "ERROR1", "ERROR2");


    }

    @Test
    public void test_compilationShouldSucceed() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest();

        MatcherAssert.assertThat(builder.compilationShouldSucceed().createCompileTestConfiguration().getCompilationShouldSucceed(), Matchers.is(Boolean.TRUE));
        MatcherAssert.assertThat(builder.compilationShouldFail().createCompileTestConfiguration().getCompilationShouldSucceed(), Matchers.is(Boolean.FALSE));


    }

    @Test
    public void test_addSource() {

        JavaFileObject testSource1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject testSource2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addSources(testSource1)
                .addSources(testSource2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.containsInAnyOrder(testSource1, testSource2));


    }

    @Test
    public void test_addSourceFromResources() {

        final String resource = "/compiletests/TestClass.java";

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addSources(resource);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles().iterator().next().getName().toString(), Matchers.is(resource));


    }

    @Test
    public void test_useProcessors() {

        Class<? extends Processor> testProcessor1 = SimpleTestProcessor1.class;
        Class<? extends Processor> testProcessor2 = SimpleTestProcessor2.class;

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addProcessors(testProcessor1)
                .addProcessors(testProcessor2)
                .addProcessors((Class<? extends Processor>) null);


        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getProcessorTypes(), Matchers.containsInAnyOrder(testProcessor1, testProcessor2));


    }

    @Test
    public void test_useProcessorAndExpectException() {

        Class<? extends Processor> testProcessor1 = SimpleTestProcessor1.class;
        Class<? extends Processor> testProcessor2 = SimpleTestProcessor2.class;

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addProcessorWithExpectedException(testProcessor1, IllegalArgumentException.class)
                .addProcessorWithExpectedException(testProcessor2, IllegalStateException.class);


        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getProcessorsWithExpectedExceptions(), Matchers.<CompileTestConfiguration.ProcessorWithExpectedException>hasSize(2));


    }

    @Test
    public void test_useSource_addSingleSource() {

        JavaFileObject javaFileObject = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource(javaFileObject);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.contains(javaFileObject));

    }

    @Test
    public void test_useSource_addSourceTwice_onlySecondSourceShouldBeUsed() {

        JavaFileObject javaFileObject1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject javaFileObject2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource(javaFileObject1)
                .useSource(javaFileObject2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.contains(javaFileObject2));

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useSource_addNullValuedSource_asJavaFileObject() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource((JavaFileObject) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useSource_addNullValuedSource_asString() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource((String) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_addNullValuedProcessor() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useProcessor((Processor) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_addNullValuedUnitTestProcessor() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .defineTest((UnitTest) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_nonInstantiableConstructorForProcessorUnderTest() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .defineTest(AbstractProcessor.class, new UnitTestForTestingAnnotationProcessors<AbstractProcessor, TypeElement>() {
                    @Override
                    public void unitTest(AbstractProcessor unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                    }
                });


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_nullProcessorUnderTestClass() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .defineTest(null, new UnitTestForTestingAnnotationProcessors<AbstractProcessor, TypeElement>() {
                    @Override
                    public void unitTest(AbstractProcessor unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                    }
                });


    }


    public static class TestProcessor extends AbstractProcessor {
        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }

    @Test
    public void test_useProcessor_nonMatchingElement1() {


        try {
            CompileTestBuilder
                    .unitTest()
                    .<TestProcessor, ExecutableElement>defineTest(TestProcessor.class, new UnitTestForTestingAnnotationProcessors<TestProcessor, ExecutableElement>() {
                        @Override
                        public void unitTest(TestProcessor unit, ProcessingEnvironment processingEnvironment, ExecutableElement typeElement) {
                            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "ABC");
                        }
                    })
                    .executeTest();
        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(Constants.Messages.UNIT_TEST_PRECONDITION_INCOMPATIBLE_ELEMENT_TYPE.getMessagePattern()));

            return;
        }

        throw new AssertionError("Expected AssertionError to be thrown.");

    }

    @Test
    public void test_useProcessor_nonMatchingElement2() {

        try {
            CompileTestBuilder
                    .unitTest()
                    .<ExecutableElement>defineTest(new UnitTest<ExecutableElement>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, ExecutableElement typeElement) {
                            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "ABC");
                        }
                    })
                    .executeTest();
        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(Constants.Messages.UNIT_TEST_PRECONDITION_INCOMPATIBLE_ELEMENT_TYPE.getMessagePattern()));

            return;
        }

        throw new AssertionError("Expected AssertionError to be thrown.");

    }

    @Test
    public void test_useProcessor_withoutGenericTypeParameters1() {

        CompileTestBuilder
                .unitTest()
                .defineTest(TestProcessor.class, new UnitTestForTestingAnnotationProcessors() {
                    @Override
                    public void unitTest(Processor unit, ProcessingEnvironment processingEnvironment, Element typeElement) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "ABC");
                    }
                })
                .executeTest();

    }

    @Test
    public void test_useProcessor_withoutGenericTypeParameters2() {

        CompileTestBuilder
                .unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element element) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "ABC");
                    }
                })
                .executeTest();


    }

    @Test
    public void test_useProcessor_nonMatchingAnnotationType() {

        CompileTestBuilder
                .unitTest()
                .defineTest(new UnitTest() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, Element element) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
                    }
                })
                .expectWarningMessage().thatContains("ABC")
                .executeTest();


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_nullValued_Processor() {
        CompileTestBuilder.unitTest().useProcessor((Processor) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_nullValued_UnitTestProcessor() {
        CompileTestBuilder.unitTest().defineTest((UnitTest) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_nullValued_UnitTestProcessor2() {
        CompileTestBuilder.unitTest().defineTest(AbstractProcessor.class, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedProcessor() {

        CompileTestBuilder
                .compilationTest()
                .addProcessorWithExpectedException(null, IllegalStateException.class);


    }

    private static class SimpleTestProcessor extends AbstractProcessor {
        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedException() {

        CompileTestBuilder
                .compilationTest()
                .addProcessorWithExpectedException(SimpleTestProcessor.class, null);


    }

    @Test(expected = IllegalStateException.class)
    public void test_CompileTimeTestBuilder_testCompilation_noSourceFiles() {

        CompileTestBuilder
                .compilationTest()
                .executeTest();


    }

    @Test
    public void test_useModules() {

        MatcherAssert.assertThat(CompileTestBuilder.compilationTest().useModules("ABC", "DEF").compileTestConfiguration.getModules(), Matchers.contains("ABC", "DEF"));

    }

    @Test
    public void test_addCompilerMessageCheck() {

        CompileTestConfiguration configuration = CompileTestBuilder.compilationTest().expectErrorMessage().atSource("XYZ").atLineNumber(5L).atColumnNumber(6L).withLocale(Locale.ENGLISH).thatContains("ABC").compileTestConfiguration;
        CompileTestConfiguration.CompilerMessageCheck compilerMessageCheck = configuration.getCompilerMessageChecks().iterator().next();
        MatcherAssert.assertThat(compilerMessageCheck.getSource(), Matchers.is("XYZ"));
        MatcherAssert.assertThat(compilerMessageCheck.getKind(), Matchers.is(Diagnostic.Kind.ERROR));
        MatcherAssert.assertThat(compilerMessageCheck.getLineNumber(), Matchers.is(5L));
        MatcherAssert.assertThat(compilerMessageCheck.getColumnNumber(), Matchers.is(6L));
        MatcherAssert.assertThat(compilerMessageCheck.getLocale(), Matchers.is(Locale.ENGLISH));
        MatcherAssert.assertThat(compilerMessageCheck.getComparisionKind(), Matchers.is(CompileTestConfiguration.ComparisionKind.CONTAINS));
        MatcherAssert.assertThat(compilerMessageCheck.getExpectedMessage(), Matchers.is("ABC"));

    }

    @Test
    public void test_passInViaSourceCode_multipleAnnotated_withOnePassIn() {

        CompileTestBuilder
                .unitTest()
                .useSource("/compiletests/passintest/PassInTestClass.java")
                .<TypeElement>defineTest(new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        MatcherAssert.assertThat(element, Matchers.notNullValue());
                        MatcherAssert.assertThat(element.getSimpleName().toString(), Matchers.is("InnerTestClass"));
                    }
                })
                .executeTest();

    }

    @Test
    public void test_passInViaSourceCode_multipleAnnotated_withoutPassIn() {

        try {
            CompileTestBuilder
                    .unitTest()
                    .useSource("/compiletests/passintest/PassInTestClassMultipleAnnotatedWithoutPassIn.java")
                    .<TypeElement>defineTest(new UnitTest<TypeElement>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                            throw new AssertionError("should have thrown assertion error!");
                        }
                    })
                    .executeTest();

        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(String.format(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT.getMessagePattern(), TestAnnotation.class.getCanonicalName())));

            return;
        }

        throw new AssertionError("Expected AssertionError to be thrown.");

    }

    @Test
    public void test_passInViaSourceCode_multipleAnnotated_withMultiplePassIn() {

        try {
            CompileTestBuilder
                    .unitTest()
                    .useSource("/compiletests/passintest/PassInTestClassMultipleAnnotatedWithMultiplePassIn.java")
                    .<TypeElement>defineTest(new UnitTest<TypeElement>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                            throw new AssertionError("should have thrown assertion error!");
                        }
                    })
                    .executeTest();

        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(String.format(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT_WITH_PASSIN_ANNOTATION.getMessagePattern(), TestAnnotation.class.getCanonicalName())));

            return;
        }

        throw new AssertionError("Expected AssertionError to be thrown.");

    }
}
