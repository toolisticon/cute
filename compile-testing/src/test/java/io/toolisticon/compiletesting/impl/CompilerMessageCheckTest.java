package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.UnitTestProcessor;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Unit Test for checking correctness of compiler messages checks.
 */
public class CompilerMessageCheckTest {

    CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder.unitTest();

    @Test
    public void testComplexCompilerMessageCheck_findMessage_withAll() {

        builder.compilationShouldSucceed().<TypeElement>useProcessor(new UnitTestProcessor<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(13L).atColumnNumber(8L).isEqual("ABC")

                .testCompilation();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongSource() {

        builder.compilationShouldSucceed().<TypeElement>useProcessor(new UnitTestProcessor<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarning().atSource("/XYZ.java").atLineNumber(13L).atColumnNumber(8L).isEqual("ABC")

                .testCompilation();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongLine() {

        builder.compilationShouldSucceed().<TypeElement>useProcessor(new UnitTestProcessor<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(3L).atColumnNumber(8L).isEqual("ABC")

                .testCompilation();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongColumn() {

        builder.compilationShouldSucceed().<TypeElement>useProcessor(new UnitTestProcessor<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(13L).atColumnNumber(7L).isEqual("ABC")

                .testCompilation();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongMessage() {

        builder.compilationShouldSucceed().<TypeElement>useProcessor(new UnitTestProcessor<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(13L).atColumnNumber(8L).isEqual("BC")

                .testCompilation();

    }

    @Test
    public void testComplexCompilerMessageCheck_findMessageSubstring_withAll() {

        builder.compilationShouldSucceed().<TypeElement>useProcessor(new UnitTestProcessor<TypeElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
            }
        })
                .expectWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLineNumber(13L).atColumnNumber(8L).contains("BC")

                .testCompilation();

    }



}
