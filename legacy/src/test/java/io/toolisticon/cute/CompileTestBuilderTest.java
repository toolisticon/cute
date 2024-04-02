package io.toolisticon.cute;

import io.toolisticon.cute.common.SimpleTestProcessor1;
import io.toolisticon.cute.common.SimpleTestProcessor2;
import io.toolisticon.fluapigen.validation.api.ValidatorException;
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
import java.util.Arrays;
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


    private void assertCompilerMessages(List<CuteApi.CompilerMessageCheckBB> compilerMessageChecks, CuteApi.CompilerMessageKind kind, CuteApi.CompilerMessageComparisonType comparisonKind, String... expectedMessages) {

        List<String> configuredExpectedMessages = new ArrayList<>();

        Iterator<CuteApi.CompilerMessageCheckBB> iterator = compilerMessageChecks.iterator();
        while (iterator.hasNext()) {
            CuteApi.CompilerMessageCheckBB element = iterator.next();

            MatcherAssert.assertThat(element.getComparisonType(), Matchers.is(comparisonKind));
            MatcherAssert.assertThat(element.getKind(), Matchers.is(kind));

            configuredExpectedMessages.addAll(element.getSearchString());

        }

        MatcherAssert.assertThat(configuredExpectedMessages
                , Matchers.containsInAnyOrder(expectedMessages));
    }

    @Test
    public void test_addWarningChecks() {

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectWarningMessageThatContains("WARN1");


        assertCompilerMessages(builder.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.WARNING, CuteApi.CompilerMessageComparisonType.CONTAINS, "WARN1");

        CompileTestBuilderApi.CompilationTestBuilder builder2 = builder
                .expectWarningMessageThatContains("WARN2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.WARNING, CuteApi.CompilerMessageComparisonType.CONTAINS, "WARN1", "WARN2");


        // TODO: MUST handle null check
        /*-
        CompileTestBuilderApi.CompilationTestBuilder builder3 = builder2
                .expectWarningMessageThatContains()
                .expectWarningMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().compilerMessageChecks(), CuteFluentApi.CompilerMessageKind.WARNING, CuteFluentApi.CompilerMessageComparisonType.CONTAINS, "WARN1", "WARN2");
*/

    }

    public void test_addMandatoryWarningChecks() {

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectMandatoryWarningMessageThatContains("MWARN1");

        assertCompilerMessages(builder.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.MANDATORY_WARNING, CuteApi.CompilerMessageComparisonType.CONTAINS, "MWARN1");


        CompileTestBuilderApi.CompilationTestBuilder builder2 = builder
                .expectMandatoryWarningMessageThatContains("MWARN2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.MANDATORY_WARNING, CuteApi.CompilerMessageComparisonType.CONTAINS, "MWARN1", "MWARN2");


        CompileTestBuilderApi.CompilationTestBuilder builder3 = builder2
                .expectMandatoryWarningMessageThatContains()
                .expectMandatoryWarningMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.MANDATORY_WARNING, CuteApi.CompilerMessageComparisonType.CONTAINS, "MWARN1", "MWARN2");


    }

    public void test_addNoteChecks() {

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectNoteMessageThatContains("NOTE1");


        assertCompilerMessages(builder.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.NOTE, CuteApi.CompilerMessageComparisonType.CONTAINS, "NOTE1");


        CompileTestBuilderApi.CompilationTestBuilder builder2 = builder
                .expectNoteMessageThatContains("NOTE2");


        assertCompilerMessages(builder2.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.NOTE, CuteApi.CompilerMessageComparisonType.CONTAINS, "NOTE1", "NOTE2");


        CompileTestBuilderApi.CompilationTestBuilder builder3 = builder2
                .expectNoteMessageThatContains()
                .expectNoteMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.NOTE, CuteApi.CompilerMessageComparisonType.CONTAINS, "NOTE1", "NOTE2");


    }

    public void test_addErrorChecks() {

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectErrorMessageThatContains("ERROR1");


        assertCompilerMessages(builder.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.ERROR, CuteApi.CompilerMessageComparisonType.CONTAINS, "ERROR1");


        CompileTestBuilderApi.CompilationTestBuilder builder2 = builder
                .expectErrorMessageThatContains("ERROR2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.ERROR, CuteApi.CompilerMessageComparisonType.CONTAINS, "ERROR1", "ERROR2");


        CompileTestBuilderApi.CompilationTestBuilder builder3 = builder2
                .expectErrorMessageThatContains()
                .expectErrorMessageThatContains(null);

        assertCompilerMessages(builder3.createCompileTestConfiguration().compilerMessageChecks(), CuteApi.CompilerMessageKind.ERROR, CuteApi.CompilerMessageComparisonType.CONTAINS, "ERROR1", "ERROR2");


    }

    @Test
    public void test_compilationShouldSucceed() {

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest();

        MatcherAssert.assertThat(builder.compilationShouldSucceed().createCompileTestConfiguration().compilationSucceeded(), Matchers.is(Boolean.TRUE));
        MatcherAssert.assertThat(builder.compilationShouldFail().createCompileTestConfiguration().compilationSucceeded(), Matchers.is(Boolean.FALSE));


    }

    @Test
    public void test_addSource() {

        JavaFileObject testSource1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject testSource2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addSources(testSource1)
                .addSources(testSource2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles(), Matchers.containsInAnyOrder(testSource1, testSource2));


    }

    @Test
    public void test_addSourceFromResources() {

        final String resource = "/compiletests/TestClass.java";

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addSources(resource);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles().iterator().next().getName().toString(), Matchers.is(resource));

    }

    @Test
    public void test_addSourceFromString_compileTest() {

        final String content = "package io.toolisticon.annotationprocessortoolkit.testhelper;" + System.lineSeparator()
                + "public class TestClass {" + System.lineSeparator()
                + "}";

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addSource("io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", content);

        // Check if source name is correct
        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles().iterator().next().getName().toString(), Matchers.is("/io/toolisticon/annotationprocessortoolkit/testhelper/TestClass.java"));

        // Check if classes are compiled in the end
        builder.compilationShouldSucceed()
                .expectThatJavaFileObjectExists(StandardLocation.CLASS_OUTPUT, "io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", JavaFileObject.Kind.CLASS)
                .executeTest();
    }

    @Test
    public void test_addSourceFromString_unitTest() {

        final String content = "package io.toolisticon.annotationprocessortoolkit.testhelper;" + System.lineSeparator()
                + "import io.toolisticon.cute.PassIn;" + System.lineSeparator()
                + "@PassIn" + System.lineSeparator()
                + "public class TestClass {" + System.lineSeparator()
                + "}";

        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource("io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", content);

        // Check if source name is correct
        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles().iterator().next().getName().toString(), Matchers.is("/io/toolisticon/annotationprocessortoolkit/testhelper/TestClass.java"));

        // Check if classes are compiled in the end
        builder.<TypeElement>defineTest(PassIn.class, new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        MatcherAssert.assertThat("io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", Matchers.is(element.getQualifiedName().toString()));
                    }
                }).compilationShouldSucceed()
                .expectThatJavaFileObjectExists(StandardLocation.CLASS_OUTPUT, "io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", JavaFileObject.Kind.CLASS)
                .executeTest();

    }

    @Test
    public void test_useProcessors() {

        Class<? extends Processor> testProcessor1 = SimpleTestProcessor1.class;
        Class<? extends Processor> testProcessor2 = SimpleTestProcessor2.class;

        CompileTestBuilderApi.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addProcessors(testProcessor1)
                .addProcessors(testProcessor2);


        MatcherAssert.assertThat(builder.createCompileTestConfiguration().processors(), Matchers.containsInAnyOrder(testProcessor1, testProcessor2));


    }

    // TODO: HOWTO HANDLE EXPECTED EXCEPTIONS OF PROCESSOR
    /*-
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
    */
    @Test
    public void test_useSource_addSingleSource() {

        JavaFileObject javaFileObject = Mockito.mock(JavaFileObject.class);

        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource(javaFileObject);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles(), Matchers.contains(javaFileObject));

    }

    @Test
    public void test_useSource_addSourceTwice_onlySecondSourceShouldBeUsed() {

        JavaFileObject javaFileObject1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject javaFileObject2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource(javaFileObject1)
                .useSource(javaFileObject2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles(), Matchers.contains(javaFileObject2));

    }

    @Test(expected = ValidatorException.class)
    public void test_useSource_addNullValuedSource_asJavaFileObject() {


        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource((JavaFileObject) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useSource_addNullValuedSource_asString() {


        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource((String) null);


    }

    @Test(expected = ValidatorException.class)
    public void test_useProcessor_addNullValuedProcessor() {


        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useProcessor((Class<Processor>) null);


    }

    @Test(expected = ValidatorException.class)
    public void test_useProcessor_addNullValuedUnitTestProcessor() {


        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .defineTest((UnitTest) null);


    }

    @Test(expected = ValidatorException.class)
    public void test_useProcessor_nonInstantiableConstructorForProcessorUnderTest() {

        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .defineTest(AbstractProcessor.class, new UnitTestForTestingAnnotationProcessors<AbstractProcessor, TypeElement>() {
                    @Override
                    public void unitTest(AbstractProcessor unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                    }
                });

    }

    @Test(expected = ValidatorException.class)
    public void test_useProcessor_nullProcessorUnderTestClass() {


        CompileTestBuilderApi.UnitTestBuilder builder = CompileTestBuilder
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

    @Test(expected = ValidatorException.class)
    public void test_useProcessor_nullValued_Processor() {
        CompileTestBuilder.unitTest().useProcessor(null);
    }

    @Test(expected = ValidatorException.class)
    public void test_useProcessor_nullValued_UnitTestProcessor() {
        CompileTestBuilder.unitTest().defineTest((UnitTest) null);
    }

    @Test(expected = ValidatorException.class)
    public void test_useProcessor_nullValued_UnitTestProcessor2() {
        CompileTestBuilder.unitTest().defineTest(AbstractProcessor.class, null);
    }


    //TODO: MUST DECIDE HOW TO HANDLE EXPECTED EXCEPTIONS OF PROCESSORS
    /*-
    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedProcessor() {

        CompileTestBuilder
                .compilationTest()
                .addProcessorWithExpectedException(null, IllegalStateException.class);


    }
     */

    private static class SimpleTestProcessor extends AbstractProcessor {
        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }

    //TODO: MUST DECIDE HOW TO HANDLE EXPECTED EXCEPTIONS OF PROCESSORS
/*-
    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedException() {

        CompileTestBuilder
                .compilationTest()
                .addProcessorWithExpectedException(SimpleTestProcessor.class, null);


    }
*/

    @Test(expected = IllegalStateException.class)
    public void test_CompileTimeTestBuilder_testCompilation_noSourceFiles() {

        CompileTestBuilder
                .compilationTest()
                .executeTest();


    }

    @Test
    public void test_useModules() {

        MatcherAssert.assertThat(CompileTestBuilder.compilationTest().useModules("ABC", "DEF").createCompileTestConfiguration().modules(), Matchers.contains("ABC", "DEF"));

    }

    @Test
    public void test_addCompilerMessageCheck() {

        CuteApi.CompilerTestBB configuration = CompileTestBuilder.compilationTest().expectErrorMessage().atSource("XYZ").atLineNumber(5L).atColumnNumber(6L).withLocale(Locale.ENGLISH).thatContains("ABC").createCompileTestConfiguration();
        CuteApi.CompilerMessageCheckBB compilerMessageCheck = configuration.compilerMessageChecks().iterator().next();
        MatcherAssert.assertThat(compilerMessageCheck.atSource(), Matchers.is("XYZ"));
        MatcherAssert.assertThat(compilerMessageCheck.getKind(), Matchers.is(CuteApi.CompilerMessageKind.ERROR));
        MatcherAssert.assertThat(compilerMessageCheck.atLine(), Matchers.is(5));
        MatcherAssert.assertThat(compilerMessageCheck.atColumn(), Matchers.is(6));
        MatcherAssert.assertThat(compilerMessageCheck.withLocale(), Matchers.is(Locale.ENGLISH));
        MatcherAssert.assertThat(compilerMessageCheck.getComparisonType(), Matchers.is(CuteApi.CompilerMessageComparisonType.CONTAINS));
        MatcherAssert.assertThat(compilerMessageCheck.getSearchString(), Matchers.contains("ABC"));

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
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(String.format(Constants.Messages.MESSAGE_PROCESSOR_HASNT_BEEN_APPLIED.getMessagePattern(), UnitTestAnnotationProcessorClass.class.getCanonicalName(), Arrays.asList(TestAnnotation.class.getCanonicalName()))));
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
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(String.format(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT_WITH_PASSIN_ANNOTATION.getMessagePattern(), PassIn.class.getCanonicalName(),PassIn.class.getCanonicalName())));
            return;
        }

        throw new AssertionError("Expected AssertionError to be thrown.");

    }

    @Test
    public void test_passInViaSourceCode_withNonMatchingElementType() {

        try {
            CompileTestBuilder
                    .unitTest()
                    .useSource("/compiletests/passintest/PassInTestClass.java")
                    .<ExecutableElement>defineTest(new UnitTest<ExecutableElement>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, ExecutableElement element) {
                            throw new AssertionError("should have thrown assertion error!");
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
    public void test_passInViaSourceCode_withProcessorPassIn_withMatchingElementButClassCastException() {


        CompileTestBuilder
                .unitTest()
                .useSource("/compiletests/passintest/PassInTestClass.java")
                .<SimpleTestProcessor1, TypeElement>defineTest(SimpleTestProcessor1.class, new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, TypeElement>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, TypeElement element) {
                        throw new ClassCastException("Test Class Cast Exception");
                    }
                })
                .expectedThrownException(ClassCastException.class)
                .executeTest();

    }

    @PassIn
    static class PassInClass {

    }

    @Test
    public void test_passIn_withNonMatchingElementType() {

        try {
            CompileTestBuilder
                    .unitTest()
                    .defineTestWithPassedInElement(PassInClass.class, new UnitTest<ExecutableElement>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, ExecutableElement element) {
                            throw new AssertionError("should have thrown assertion error!");
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
    public void test_passIn_withMatchingElementButClassCastException() {

        CompileTestBuilder
                .unitTest()
                .<TypeElement>defineTestWithPassedInElement(PassInClass.class, new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        throw new ClassCastException("Test Class Cast Exception");
                    }
                })
                .expectedThrownException(ClassCastException.class)
                .executeTest();

    }

    @Test
    public void test_passIn_withProcessorPassIn_withNonMatchingElementType() {

        try {
            CompileTestBuilder
                    .unitTest()
                    .<SimpleTestProcessor1, ExecutableElement>defineTestWithPassedInElement(SimpleTestProcessor1.class, PassInClass.class, new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, ExecutableElement>() {
                        @Override
                        public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, ExecutableElement element) {
                            throw new AssertionError("should have thrown assertion error!");
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
    public void test_passIn_withProcessorPassIn_withMatchingElementButClassCastException() {

        CompileTestBuilder
                .unitTest()
                .<SimpleTestProcessor1, Element>defineTestWithPassedInElement(SimpleTestProcessor1.class, PassInClass.class, new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, Element>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, Element element) {
                        throw new ClassCastException("Test Class Cast Exception");
                    }
                })
                .expectedThrownException(ClassCastException.class)
                .executeTest();

    }

}
