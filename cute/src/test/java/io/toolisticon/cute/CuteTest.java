package io.toolisticon.cute;

import io.toolisticon.cute.common.SimpleTestProcessor1;
import io.toolisticon.cute.testcases.SimpleTestInterface;
import io.toolisticon.fluapigen.validation.api.ValidatorException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Unit tests for {@link Cute}.
 */
public class CuteTest {

    @Test
    public void test_UnitTest_successfulCompilation_build() throws IOException{

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = JavaFileObjectUtils.readFromString("Jupp.txt", "TATA!");
        Cute
                .unitTest()
                .when(
                        new UnitTestWithoutPassIn() {
                            @Override
                            public void unitTest(ProcessingEnvironment processingEnvironment) {

                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "MANDATORY_WARNING");
                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "WARNING");
                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "NOTE");


                                try {
                                    FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt");
                                    Writer writer = fileObject.openWriter();
                                    writer.write("TATA!");
                                    writer.close();


                                } catch (IOException e) {

                                }

                            }
                        })
                .thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().contains("WARNING")
                .andThat().compilerMessage().ofKindMandatoryWarning().contains("MANDATORY_WARNING")
                .andThat().compilerMessage().ofKindNote().contains("NOTE")
                .executeTest().doManualAssertions(e -> {
                    MatcherAssert.assertThat("Expected to find warning message that contains WARNING", !(e.getCompilerMessages().stream().filter(f -> f.getKind() == Diagnostic.Kind.WARNING).filter(f -> f.getMessage().contains("WARNING")).count() == 0));
                    MatcherAssert.assertThat("Should not find generated SOURCE FILES", e.getJavaFileObjects().stream().filter(f -> f.getKind() == JavaFileObject.Kind.SOURCE).count() == 0);
                    MatcherAssert.assertThat("Should  find generated RESOURCE file that contains TATA", e.getFileObjects().stream().filter(f -> f.getName().equals("/root/Jupp.txt")).filter(f -> {
                        try {
                            return f.getCharContent(true).toString().contains("TATA");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }).count() == 1);
                    MatcherAssert.assertThat("Should be true", true);
                });


    }


    @Test
    public void test_UnitTest_successfulCompilation_withInitializedProcessorUnderTest_build() {

        Cute.unitTest()
                .when().passInProcessor(SimpleTestProcessor1.class)
                .intoUnitTest(new UnitTestForTestingAnnotationProcessorsWithoutPassIn<SimpleTestProcessor1>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment) {

                        MatcherAssert.assertThat(unit.getProcessingEnvironment(), Matchers.equalTo(processingEnvironment));

                    }
                })
                .thenExpectThat().compilationSucceeds()
                .executeTest();
    }

    @PassIn
    private static class PassInProcessorAndElement {

    }


    @Test
    public void test_UnitTest_successfulCompilation_withInitializedProcessorUnderTestAndPassIn_build() {

        Cute
                .unitTest()
                .when()
                .passInProcessor(SimpleTestProcessor1.class)
                .andPassInElement().<TypeElement>fromClass(PassInProcessorAndElement.class)
                .intoUnitTest(new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, TypeElement>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                        MatcherAssert.assertThat(typeElement, Matchers.notNullValue());
                        MatcherAssert.assertThat(typeElement.getQualifiedName().toString(), Matchers.is(PassInProcessorAndElement.class.getCanonicalName()));

                    }
                })
                .thenExpectThat()
                .compilationSucceeds()
                .executeTest();


    }

    /**
     * @Retention(RetentionPolicy.RUNTIME) private static @interface CustomPassInAnnotation {
     * <p>
     * }
     * @CustomPassInAnnotation private static class PassInProcessorAndElementWithCustomAnnotation {
     * <p>
     * }
     * @Test public void test_UnitTest_successfulCompilation_withInitializedProcessorUnderTestAndPassInWithCustomAnnotation_build() {
     * <p>
     * CuteFluentApiStarter
     * .unitTest()
     * .when()
     * .passInProcessor(SimpleTestProcessor1.class)
     * .andPassInElement().fromClass(PassInProcessorAndElementWithCustomAnnotation.class)
     * .defineTestWithPassedInElement(SimpleTestProcessor1.class, PassInProcessorAndElementWithCustomAnnotation.class, CustomPassInAnnotation.class, new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, TypeElement>() {
     * @Override public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement) {
     * <p>
     * MatcherAssert.assertThat(typeElement, Matchers.notNullValue());
     * MatcherAssert.assertThat(typeElement.getQualifiedName().toString(), Matchers.is(PassInProcessorAndElementWithCustomAnnotation.class.getCanonicalName()));
     * <p>
     * }
     * })
     * .compilationShouldSucceed()
     * .executeTest();
     * <p>
     * <p>
     * }
     */

    @Test
    public void test_UnitTest_failingCompilation_build() {

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = Mockito.mock(JavaFileObject.class);

        Cute
                .unitTest()
                .when().unitTestWithoutPassIn(new UnitTestWithoutPassIn() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment) {

                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR");
                    }
                })
                .thenExpectThat()
                .compilationFails()
                .andThat().compilerMessage().ofKindError().contains("ERROR")
                .executeTest();


    }

/*-
    private void assertCompilerMessages(Set<CompileTestConfiguration.CompilerMessageCheck> compilerMessageChecks, Diagnostic.Kind kind, CompileTestConfiguration.ComparisonKind comparisonKind, String... expectedMessages) {

        List<String> configuredExpectedMessages = new ArrayList<>();

        Iterator<CompileTestConfiguration.CompilerMessageCheck> iterator = compilerMessageChecks.iterator();
        while (iterator.hasNext()) {
            CompileTestConfiguration.CompilerMessageCheck element = iterator.next();

            MatcherAssert.assertThat(element.getComparisonKind(), Matchers.is(comparisonKind));
            MatcherAssert.assertThat(element.getKind(), Matchers.is(kind));

            configuredExpectedMessages.add(element.getExpectedMessage());

        }

        MatcherAssert.assertThat(configuredExpectedMessages
                , Matchers.containsInAnyOrder(expectedMessages));
    }


    @Test
    public void test_addWarningChecks() {

        CuteFluentApiStarter. builder = CuteFluentApiStarter
                .blackBoxTest()
                .compilationTest()
                .expectWarningMessageThatContains("WARN1");


        assertCompilerMessages(builder.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.WARNING, CompileTestConfiguration.ComparisonKind.CONTAINS, "WARN1");

        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectWarningMessageThatContains("WARN2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.WARNING, CompileTestConfiguration.ComparisonKind.CONTAINS, "WARN1", "WARN2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectWarningMessageThatContains()
                .expectWarningMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.WARNING, CompileTestConfiguration.ComparisonKind.CONTAINS, "WARN1", "WARN2");


    }
*/
    /*-
    public void test_addMandatoryWarningChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectMandatoryWarningMessageThatContains("MWARN1");

        assertCompilerMessages(builder.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.MANDATORY_WARNING, CompileTestConfiguration.ComparisonKind.CONTAINS, "MWARN1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectMandatoryWarningMessageThatContains("MWARN2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.MANDATORY_WARNING, CompileTestConfiguration.ComparisonKind.CONTAINS, "MWARN1", "MWARN2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectMandatoryWarningMessageThatContains()
                .expectMandatoryWarningMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.MANDATORY_WARNING, CompileTestConfiguration.ComparisonKind.CONTAINS, "MWARN1", "MWARN2");


    }

    public void test_addNoteChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectNoteMessageThatContains("NOTE1");


        assertCompilerMessages(builder.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.NOTE, CompileTestConfiguration.ComparisonKind.CONTAINS, "NOTE1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectNoteMessageThatContains("NOTE2");


        assertCompilerMessages(builder2.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.NOTE, CompileTestConfiguration.ComparisonKind.CONTAINS, "NOTE1", "NOTE2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectNoteMessageThatContains()
                .expectNoteMessageThatContains(null);


        assertCompilerMessages(builder3.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.NOTE, CompileTestConfiguration.ComparisonKind.CONTAINS, "NOTE1", "NOTE2");


    }

    public void test_addErrorChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectErrorMessageThatContains("ERROR1");


        assertCompilerMessages(builder.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.ERROR, CompileTestConfiguration.ComparisonKind.CONTAINS, "ERROR1");


        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectErrorMessageThatContains("ERROR2");

        assertCompilerMessages(builder2.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.ERROR, CompileTestConfiguration.ComparisonKind.CONTAINS, "ERROR1", "ERROR2");


        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectErrorMessageThatContains()
                .expectErrorMessageThatContains(null);

        assertCompilerMessages(builder3.createCompileTestConfiguration().getCompilerMessageChecks(), Diagnostic.Kind.ERROR, CompileTestConfiguration.ComparisonKind.CONTAINS, "ERROR1", "ERROR2");


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
    public void test_addSourceFromString_compileTest() {

        final String content ="package io.toolisticon.annotationprocessortoolkit.testhelper;" + System.lineSeparator()
                        + "public class TestClass {" + System.lineSeparator()
                        + "}";

        CuteApi.BlackBoxTestFinalGivenInterface builder = Cute
                .blackBoxTest()
                .given().processors(AbstractProcessor.class)
                .andSourceFile("io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", content);

        // Check if source name is correct
        MatcherAssert.assertThat(builder..createCompileTestConfiguration().getSourceFiles().iterator().next().getName().toString(), Matchers.is("/io/toolisticon/annotationprocessortoolkit/testhelper/TestClass.java"));

        // Check if classes are compiled in the end
        builder.compilationShouldSucceed()
                .expectThatJavaFileObjectExists(StandardLocation.CLASS_OUTPUT,"io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", JavaFileObject.Kind.CLASS)
                .executeTest();
    }


    @Test
    public void test_addSourceFromString_unitTest() {

        final String content ="package io.toolisticon.annotationprocessortoolkit.testhelper;" + System.lineSeparator()
                + "import io.toolisticon.cute.PassIn;" + System.lineSeparator()
                + "@PassIn" + System.lineSeparator()
                + "public class TestClass {" + System.lineSeparator()
                + "}";

        CuteFluentApi.PassInElementInterface builder = (CuteFluentApi.PassInElementInterface) CuteFluentApiStarter
                .unitTest()
                .when().passInElement().fromStringSource("io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", content);

        // Check if source name is correct
        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles().iterator().next().getName().toString(), Matchers.is("/io/toolisticon/annotationprocessortoolkit/testhelper/TestClass.java"));

        // Check if classes are compiled in the end
        builder.<TypeElement>defineTest(PassIn.class, new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        MatcherAssert.assertThat("io.toolisticon.annotationprocessortoolkit.testhelper.TestClass" , Matchers.is(element.getQualifiedName().toString()));
                    }
                }).compilationShouldSucceed()
                .expectThatJavaFileObjectExists(StandardLocation.CLASS_OUTPUT,"io.toolisticon.annotationprocessortoolkit.testhelper.TestClass", JavaFileObject.Kind.CLASS)
                .executeTest();

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
        MatcherAssert.assertThat(compilerMessageCheck.getComparisonKind(), Matchers.is(CompileTestConfiguration.ComparisonKind.CONTAINS));
        MatcherAssert.assertThat(compilerMessageCheck.getExpectedMessage(), Matchers.is("ABC"));

    }
    */

    @Test
    public void test_passInViaSourceCode_multipleAnnotated_withOnePassIn() {

        Cute
                .unitTest()
                .when()
                .passInElement().<TypeElement>fromSourceFile("/compiletests/passintest/PassInTestClass.java")
                .intoUnitTest(new UnitTest<TypeElement>() {
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
            Cute
                    .unitTest()
                    .when()
                    .passInElement().<TypeElement>fromSourceFile("/compiletests/passintest/PassInTestClassMultipleAnnotatedWithoutPassIn.java")
                    .intoUnitTest(new UnitTest<TypeElement>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                            throw new AssertionError("should have thrown assertion error!");
                        }
                    })
                    .executeTest();

        } catch (AssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(String.format(Constants.Messages.MESSAGE_PROCESSOR_HASNT_BEEN_APPLIED.getMessagePattern(), UnitTestAnnotationProcessorClass.class.getCanonicalName(), Arrays.asList(PassIn.class.getCanonicalName()))));
            return;
        }

        throw new AssertionError("Expected AssertionError to be thrown.");

    }

    @Test
    public void test_passInViaSourceCode_multipleAnnotated_withMultiplePassIn() {

        try {
            Cute
                    .unitTest()
                    .when()
                    .passInElement().<TypeElement>fromSourceFile("/compiletests/passintest/PassInTestClassMultipleAnnotatedWithMultiplePassIn.java")
                    .intoUnitTest(new UnitTest<TypeElement>() {
                        @Override
                        public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                            throw new AssertionError("should have thrown assertion error!");
                        }
                    })
                    .executeTest();

        } catch (AssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString(String.format(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT_WITH_PASSIN_ANNOTATION.getMessagePattern())));
            return;
        }

        throw new AssertionError("Expected AssertionError to be thrown.");

    }

    @Test
    public void test_passInViaSourceCode_withNonMatchingElementType() {

        try {
            Cute
                    .unitTest()
                    .given()
                    .useSourceFile("/compiletests/passintest/PassInTestClass.java")
                    .when()
                    .passInElement().<ExecutableElement>fromGivenSourceFiles()
                    .intoUnitTest(new UnitTest<ExecutableElement>() {
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


        Cute
                .unitTest()
                .given().useSourceFile("/compiletests/passintest/PassInTestClass.java")
                .when()
                .passInElement().<TypeElement>fromGivenSourceFiles()
                .andPassInProcessor(SimpleTestProcessor1.class)
                .intoUnitTest(new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, TypeElement>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, TypeElement element) {
                        throw new ClassCastException("Test Class Cast Exception");
                    }
                })
                .thenExpectThat()
                .exceptionIsThrown(ClassCastException.class)
                .executeTest();

    }

    @PassIn
    static class PassInClass {

    }


    @Test
    public void test_passIn_withNonMatchingElementType() {

        try {
            Cute
                    .unitTest()
                    .when()
                    .passInElement().<ExecutableElement>fromClass(PassInClass.class)
                    .intoUnitTest(new UnitTest<ExecutableElement>() {
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

        Cute
                .unitTest()
                .when()
                .passInElement().<TypeElement>fromClass(PassInClass.class)
                .intoUnitTest(new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        throw new ClassCastException("Test Class Cast Exception");
                    }
                })
                .thenExpectThat()
                .exceptionIsThrown(ClassCastException.class)
                .executeTest();

    }

    @Test
    public void test_passIn_withProcessorPassIn_withNonMatchingElementType() {

        try {
            Cute
                    .unitTest()
                    .when()
                    .passInProcessor(SimpleTestProcessor1.class)
                    .andPassInElement().<ExecutableElement>fromClass(PassInClass.class)
                    .intoUnitTest(new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, ExecutableElement>() {
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

        Cute
                .unitTest()
                .when()
                .passInProcessor(SimpleTestProcessor1.class)
                .andPassInElement().fromClass(PassInClass.class)
                .intoUnitTest(new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, Element>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, Element element) {
                        throw new ClassCastException("Test Class Cast Exception");
                    }
                })
                .thenExpectThat()
                .exceptionIsThrown(ClassCastException.class)
                .executeTest();

    }


    @Test(expected = ValidatorException.class)
    public void blackBoxTest_nullValuedProcessor() {
        Cute.blackBoxTest().given().processor((Class<? extends Processor>)null);
    }

    @Test(expected = ValidatorException.class)
    public void blackBoxTest_nullValuedProcessors() {
        Cute.blackBoxTest().given().processor((Class<? extends Processor>)null);
    }

    @Test(expected = ValidatorException.class)
    public void blackBoxTest_nullValuedProcessorInArray() {
        Cute.blackBoxTest().given().processors((Class<? extends Processor>)null, (Class<? extends Processor>)null);
    }

    @Test(expected = ValidatorException.class)
    public void blackBoxTest_nullValuedProcessorCollection() {
        Cute.blackBoxTest().given().processors((Collection<Class<? extends Processor>>) null);
    }

    @Test()
    public void blackBoxTest_emptyProcessors_shouldJustCompileCode() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/TestClass.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClass("io.toolisticon.cute.TestClass").exists()
                .executeTest();
    }

    @Test()
    public void blackBoxTest_emptyProcessorCollection_shouldJustCompileCode() {
        Cute.blackBoxTest().given().processors(Collections.emptyList())
                .andSourceFiles("/TestClass.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClass("io.toolisticon.cute.TestClass").exists()
                .executeTest();
    }


    @Test()
    public void blackBoxTest_noProcessors_shouldJustCompileCode() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/TestClass.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClass("io.toolisticon.cute.TestClass").exists()
                .executeTest();
    }

    @Test
    public void blackBoxTest_executeTest_WithoutChecks_SuccessfulCompilation() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/TestClass.java")
                .executeTest();
    }

    @Test
    public void blackBoxTest_executeTest_WithoutChecks_FailedCompilation() {
        try {
            Cute.blackBoxTest().given().noProcessors()
                    .andSourceFiles("/BrokenTestClass.java")
                    .executeTest();

        } catch (AssertionError e) {
            if (e.getMessage().contains("Compilation should have succeeded but failed")) {
                return;
            }
        }
        throw new AssertionError("Should have got Assertion error that compilation was expected to be successful but failed");
    }

    @Test()
    public void blackBoxTest_justCompileCodeAndDoClassTests() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/TestClass.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClass("io.toolisticon.cute.TestClass").testedSuccessfullyBy(new GeneratedClassesTestForSpecificClass() {
                    @Override
                    public void doTests(Class<?> clazz,CuteClassLoader cuteClassLoader) throws Exception{
                        MatcherAssert.assertThat(clazz.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClass"));

                        Object instance = clazz.getConstructor().newInstance();
                        MatcherAssert.assertThat(instance, Matchers.notNullValue());
                    }
                })
                .executeTest();
    }

    @Test()
    public void blackBoxTest_justCompileCodeAndDoClassTests2() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/TestClassWithInnerClasses.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClass("io.toolisticon.cute.TestClassWithInnerClasses").testedSuccessfullyBy(new GeneratedClassesTestForSpecificClass() {
                    @Override
                    public void doTests(Class<?>clazz, CuteClassLoader cuteClassLoader) throws Exception{
                        MatcherAssert.assertThat(clazz.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses"));

                        Class<?> innerClazz = cuteClassLoader.getClass("io.toolisticon.cute.TestClassWithInnerClasses$InnerClass");
                        MatcherAssert.assertThat(innerClazz.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses.InnerClass"));

                        Class<?> staticInnerClazz = cuteClassLoader.getClass("io.toolisticon.cute.TestClassWithInnerClasses$StaticInnerClass");
                        MatcherAssert.assertThat(staticInnerClazz.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses.StaticInnerClass"));

                        Class<?> innerInterface = cuteClassLoader.getClass("io.toolisticon.cute.TestClassWithInnerClasses$InnerInterface");
                        MatcherAssert.assertThat(innerInterface.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses.InnerInterface"));

                        Object instance = clazz.getConstructor().newInstance();
                        MatcherAssert.assertThat(instance, Matchers.notNullValue());

                    }
                })
                .executeTest();
    }

    @Test()
    public void blackBoxTest_justCompileCodeAndDoClassTest3() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/TestClassWithInnerClasses.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClassesTestedSuccessfullyBy(new GeneratedClassesTest() {
                    @Override
                    public void doTests(CuteClassLoader cuteClassLoader) throws Exception{
                        Class<?> clazz = cuteClassLoader.getClass("io.toolisticon.cute.TestClassWithInnerClasses");
                        MatcherAssert.assertThat(clazz.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses"));

                        Class<?> innerClazz = cuteClassLoader.getClass("io.toolisticon.cute.TestClassWithInnerClasses$InnerClass");
                        MatcherAssert.assertThat(innerClazz.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses.InnerClass"));

                        Class<?> staticInnerClazz = cuteClassLoader.getClass("io.toolisticon.cute.TestClassWithInnerClasses$StaticInnerClass");
                        MatcherAssert.assertThat(staticInnerClazz.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses.StaticInnerClass"));

                        Class<?> innerInterface = cuteClassLoader.getClass("io.toolisticon.cute.TestClassWithInnerClasses$InnerInterface");
                        MatcherAssert.assertThat(innerInterface.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses.InnerInterface"));

                        Object instance = clazz.getConstructor().newInstance();
                        MatcherAssert.assertThat(instance, Matchers.notNullValue());

                    }
                })
                .executeTest();
    }

    @Test()
    public void blackBoxTest_justCompileCodeAndDoClassTest4() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/TestClassWithInnerClasses.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClass("io.toolisticon.cute.TestClassWithInnerClasses$InnerClass").testedSuccessfullyBy(new GeneratedClassesTestForSpecificClass() {
                    @Override
                    public void doTests( Class<?> innerClazz, CuteClassLoader cuteClassLoader) throws Exception{

                        MatcherAssert.assertThat(innerClazz.getCanonicalName(),Matchers.is("io.toolisticon.cute.TestClassWithInnerClasses.InnerClass"));


                    }
                })
                .executeTest();
    }

    @Test()
    public void blackBoxTest_justCompileCodeAndDoClassTestWithImplementedInterface() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/TestClassWithImplementedInterface.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClass("io.toolisticon.cute.TestClassWithImplementedInterface").testedSuccessfullyBy(new GeneratedClassesTestForSpecificClass() {
                    @Override
                    public void doTests(Class<?> clazz, CuteClassLoader cuteClassLoader) throws Exception{

                        SimpleTestInterface unit = (SimpleTestInterface) clazz.getConstructor().newInstance();
                        MatcherAssert.assertThat(unit.saySomething(), Matchers.is("WHATS UP?"));

                    }
                })
                .executeTest();
    }


    @Test()
    public void blackBoxTest_justCompileCodeAndDoClassTestWithImplementedInterfaceAndRelationsBetweenClasses() {
        Cute.blackBoxTest().given().noProcessors()
                .andSourceFiles("/compiletests/withmultiplerelatedsourcefiles/JustOutput.java", "/compiletests/withmultiplerelatedsourcefiles/TestClassWithImplementedInterface.java")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().generatedClass("io.toolisticon.cute.TestClassWithImplementedInterface").testedSuccessfullyBy(new GeneratedClassesTestForSpecificClass() {
                    @Override
                    public void doTests(Class<?> clazz, CuteClassLoader cuteClassLoader) throws Exception{

                        SimpleTestInterface unit = (SimpleTestInterface) clazz.getConstructor().newInstance();
                        MatcherAssert.assertThat(unit.saySomething(), Matchers.is("WHATS UP???"));

                    }
                })
                .executeTest();
    }
}
