package io.toolisticon.cute;

import io.toolisticon.cute.common.SimpleTestProcessor1;
import io.toolisticon.cute.common.SimpleTestProcessor2;
import io.toolisticon.cute.impl.CompileTestConfiguration;
import io.toolisticon.fluapigen.validation.api.Validator;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CompileTestBuilderTest {

    @Test
    public void test_UnitTest_successfulCompilation_build() {

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = JavaFileObjectUtils.readFromString("Jupp.txt", "TATA!");
        CompileTestBuilderOld
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

        CompileTestBuilderOld
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

        CompileTestBuilderOld
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

        CompileTestBuilderOld
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

        CompileTestBuilderOld
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


    private void assertCompilerMessages(List<CompileTestBuilder.CompilerMessageCheckBB> compilerMessageChecks, CompileTestBuilder.CompilerMessageKind kind, CompileTestBuilder.CompilerMessageComparisonType comparisonKind, String... expectedMessages) {

        List<String> configuredExpectedMessages = new ArrayList<>();

        Iterator<CompileTestBuilder.CompilerMessageCheckBB> iterator = compilerMessageChecks.iterator();
        while (iterator.hasNext()) {
            CompileTestBuilder.CompilerMessageCheckBB element = iterator.next();

            MatcherAssert.assertThat(element.getComparisonType(), Matchers.is(comparisonKind));
            MatcherAssert.assertThat(element.getKind(), Matchers.is(kind));

            configuredExpectedMessages.addAll(element.getSearchString());

        }

        MatcherAssert.assertThat(configuredExpectedMessages
                , Matchers.containsInAnyOrder(expectedMessages));
    }

    @Test
    public void test_addWarningChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
                .compilationTest()
                .expectWarningMessageThatContains("WARN1");


        assertCompilerMessages(builder.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.WARNING, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "WARN1");

        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectWarningMessageThatContains("WARN2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.WARNING, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "WARN1", "WARN2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectWarningMessageThatContains()
                .expectWarningMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.WARNING, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "WARN1", "WARN2");


    }

    public void test_addMandatoryWarningChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
                .compilationTest()
                .expectMandatoryWarningMessageThatContains("MWARN1");

        assertCompilerMessages(builder.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.MANDATORY_WARNING, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "MWARN1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectMandatoryWarningMessageThatContains("MWARN2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.MANDATORY_WARNING, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "MWARN1", "MWARN2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectMandatoryWarningMessageThatContains()
                .expectMandatoryWarningMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.MANDATORY_WARNING, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "MWARN1", "MWARN2");


    }

    public void test_addNoteChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
                .compilationTest()
                .expectNoteMessageThatContains("NOTE1");


        assertCompilerMessages(builder.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.NOTE, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "NOTE1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectNoteMessageThatContains("NOTE2");


        assertCompilerMessages(builder2.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.NOTE, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "NOTE1", "NOTE2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectNoteMessageThatContains()
                .expectNoteMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.NOTE, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "NOTE1", "NOTE2");


    }

    public void test_addErrorChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
                .compilationTest()
                .expectErrorMessageThatContains("ERROR1");


        assertCompilerMessages(builder.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.ERROR, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "ERROR1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectErrorMessageThatContains("ERROR2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.ERROR, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "ERROR1", "ERROR2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectErrorMessageThatContains()
                .expectErrorMessageThatContains(null);

        assertCompilerMessages(builder3.createCompileTestConfiguration().compilerMessageChecks(), CompileTestBuilder.CompilerMessageKind.ERROR, CompileTestBuilder.CompilerMessageComparisonType.CONTAINS, "ERROR1", "ERROR2");


    }

    @Test
    public void test_compilationShouldSucceed() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
                .compilationTest();

        MatcherAssert.assertThat(builder.compilationShouldSucceed().createCompileTestConfiguration().compilationSucceeded(), Matchers.is(Boolean.TRUE));
        MatcherAssert.assertThat(builder.compilationShouldFail().createCompileTestConfiguration().compilationSucceeded(), Matchers.is(Boolean.FALSE));


    }

    @Test
    public void test_addSource() {

        JavaFileObject testSource1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject testSource2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
                .compilationTest()
                .addSources(testSource1)
                .addSources(testSource2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles(), Matchers.containsInAnyOrder(testSource1, testSource2));


    }

    @Test
    public void test_addSourceFromResources() {

        final String resource = "/compiletests/TestClass.java";

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
                .compilationTest()
                .addSources(resource);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles().iterator().next().getName().toString(), Matchers.is(resource));

    }

    @Test
    public void test_addSourceFromString_compileTest() {

        final String content = "package io.toolisticon.annotationprocessortoolkit.testhelper;" + System.lineSeparator()
                + "public class TestClass {" + System.lineSeparator()
                + "}";

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
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

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
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

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
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

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilderOld
                .compilationTest()
                .addProcessorWithExpectedException(testProcessor1, IllegalArgumentException.class)
                .addProcessorWithExpectedException(testProcessor2, IllegalStateException.class);


        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getProcessorsWithExpectedExceptions(), Matchers.<CompileTestConfiguration.ProcessorWithExpectedException>hasSize(2));


    }
    */
    @Test
    public void test_useSource_addSingleSource() {

        JavaFileObject javaFileObject = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
                .unitTest()
                .useSource(javaFileObject);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles(), Matchers.contains(javaFileObject));

    }

    @Test
    public void test_useSource_addSourceTwice_onlySecondSourceShouldBeUsed() {

        JavaFileObject javaFileObject1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject javaFileObject2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
                .unitTest()
                .useSource(javaFileObject1)
                .useSource(javaFileObject2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().sourceFiles(), Matchers.contains(javaFileObject2));

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useSource_addNullValuedSource_asJavaFileObject() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
                .unitTest()
                .useSource((JavaFileObject) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useSource_addNullValuedSource_asString() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
                .unitTest()
                .useSource((String) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_addNullValuedProcessor() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
                .unitTest()
                .useProcessor((Class<Processor>) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_addNullValuedUnitTestProcessor() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
                .unitTest()
                .defineTest((UnitTest) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_nonInstantiableConstructorForProcessorUnderTest() {

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
                .unitTest()
                .defineTest(AbstractProcessor.class, new UnitTestForTestingAnnotationProcessors<AbstractProcessor, TypeElement>() {
                    @Override
                    public void unitTest(AbstractProcessor unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                    }
                });

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_nullProcessorUnderTestClass() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilderOld
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
            CompileTestBuilderOld
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
            CompileTestBuilderOld
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

        CompileTestBuilderOld
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

        CompileTestBuilderOld
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

        CompileTestBuilderOld
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
        CompileTestBuilderOld.unitTest().useProcessor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_nullValued_UnitTestProcessor() {
        CompileTestBuilderOld.unitTest().defineTest((UnitTest) null);
    }

    @Test(expected = Validator.ValidatorException.class)
    public void test_useProcessor_nullValued_UnitTestProcessor2() {
        CompileTestBuilderOld.unitTest().defineTest(AbstractProcessor.class, null);
    }


    //TODO: MUST DECIDE HOW TO HANDLE EXPECTED EXCEPTIONS OF PROCESSORS
    /*-
    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedProcessor() {

        CompileTestBuilderOld
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

        CompileTestBuilderOld
                .compilationTest()
                .addProcessorWithExpectedException(SimpleTestProcessor.class, null);


    }
*/

    @Test(expected = IllegalStateException.class)
    public void test_CompileTimeTestBuilder_testCompilation_noSourceFiles() {

        CompileTestBuilderOld
                .compilationTest()
                .executeTest();


    }

    @Test
    public void test_useModules() {

        MatcherAssert.assertThat(CompileTestBuilderOld.compilationTest().useModules("ABC", "DEF").createCompileTestConfiguration().modules(), Matchers.contains("ABC", "DEF"));

    }

    @Test
    public void test_addCompilerMessageCheck() {

        CompileTestBuilder.CompilerTestBB configuration = CompileTestBuilderOld.compilationTest().expectErrorMessage().atSource("XYZ").atLineNumber(5L).atColumnNumber(6L).withLocale(Locale.ENGLISH).thatContains("ABC").createCompileTestConfiguration();
        CompileTestBuilder.CompilerMessageCheckBB compilerMessageCheck = configuration.compilerMessageChecks().iterator().next();
        MatcherAssert.assertThat(compilerMessageCheck.atSource(), Matchers.is("XYZ"));
        MatcherAssert.assertThat(compilerMessageCheck.getKind(), Matchers.is(CompileTestBuilder.CompilerMessageKind.ERROR));
        MatcherAssert.assertThat(compilerMessageCheck.atLine(), Matchers.is(5L));
        MatcherAssert.assertThat(compilerMessageCheck.atColumn(), Matchers.is(6L));
        MatcherAssert.assertThat(compilerMessageCheck.withLocale(), Matchers.is(Locale.ENGLISH));
        MatcherAssert.assertThat(compilerMessageCheck.getKind(), Matchers.is(CompileTestConfiguration.ComparisonKind.CONTAINS));
        MatcherAssert.assertThat(compilerMessageCheck.getSearchString(), Matchers.contains("ABC"));

    }

    @Test
    public void test_passInViaSourceCode_multipleAnnotated_withOnePassIn() {

        CompileTestBuilderOld
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
            CompileTestBuilderOld
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
            CompileTestBuilderOld
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

    @Test
    public void test_passInViaSourceCode_withNonMatchingElementType() {

        try {
            CompileTestBuilderOld
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


        CompileTestBuilderOld
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
            CompileTestBuilderOld
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

        CompileTestBuilderOld
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
            CompileTestBuilderOld
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

        CompileTestBuilderOld
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
