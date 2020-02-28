package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.UnitTest;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Unit Test for checking correctness of compiler messages checks.
 */
public class CompilerMessageCheckTest {

    CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder.unitTest();

    @Test
    public void testComplexCompilerMessageCheck_findMessage_withAll() {

        builder.compilationShouldSucceed().<TypeElement>defineTest(new UnitTest<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarningMessage().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(13L).atColumnNumber(8L).thatIsEqualTo("ABC")

                .executeTest();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongSource() {

        builder.compilationShouldSucceed().<TypeElement>defineTest(new UnitTest<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarningMessage().atSource("/XYZ.java").atLineNumber(13L).atColumnNumber(8L).thatIsEqualTo("ABC")

                .executeTest();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongLine() {

        builder.compilationShouldSucceed().<TypeElement>defineTest(new UnitTest<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarningMessage().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(3L).atColumnNumber(8L).thatIsEqualTo("ABC")

                .executeTest();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongColumn() {

        builder.compilationShouldSucceed().<TypeElement>defineTest(new UnitTest<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarningMessage().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(13L).atColumnNumber(7L).thatIsEqualTo("ABC")

                .executeTest();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongMessage() {

        builder.compilationShouldSucceed().<TypeElement>defineTest(new UnitTest<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarningMessage().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(13L).atColumnNumber(8L).thatIsEqualTo("BC")

                .executeTest();

    }

    @Test
    public void testComplexCompilerMessageCheck_findMessageSubstring_withAll() {

        builder.compilationShouldSucceed().<TypeElement>defineTest(new UnitTest<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarningMessage().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(13L).atColumnNumber(8L).thatContains("BC")

                .executeTest();

    }



}
