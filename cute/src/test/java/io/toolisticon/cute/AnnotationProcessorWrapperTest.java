package io.toolisticon.cute;


import io.toolisticon.cute.testcases.TestAnnotationProcessor;
import io.toolisticon.cute.testcases.TestAnnotationProcessorWithMissingNoArgConstructor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

/**
 * Test for Wrapper class {@link AnnotationProcessorWrapper} to.
 */
public class AnnotationProcessorWrapperTest {

    @Test
    public void createWrapperWithInstance() {

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(TestAnnotationProcessor.class);

        MatcherAssert.assertThat("Must return non null valued Processor", unit, Matchers.notNullValue());

    }

    @Test(expected = IllegalArgumentException.class)
    public void createWrapperWithNullValuedInstance() {

        AnnotationProcessorWrapper.wrapProcessor((AbstractProcessor) null);

    }

    @Test
    public void createWrapperWithType() {

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(TestAnnotationProcessor.class);
        MatcherAssert.assertThat("Must return non null valued Processor", unit, Matchers.notNullValue());

    }

    @Test
    public void createWrapperWithTypeAndException() {

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(TestAnnotationProcessor.class, IllegalStateException.class);
        MatcherAssert.assertThat("Must return non null valued Processor", unit, Matchers.notNullValue());

    }

    @Test(expected = IllegalArgumentException.class)
    public void createWrapperWithTypeAndException_nullValuedProcessorClass() {

        AnnotationProcessorWrapper.wrapProcessor((Class) null, IllegalStateException.class);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createWrapperWithTypeAndException_nullValuedExceptionClass() {

        AnnotationProcessorWrapper.wrapProcessor(TestAnnotationProcessor.class, (Class) null);

    }

    public static class InvalidProcessor extends AbstractProcessor {

        public InvalidProcessor(String abc) {

        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWrapperWithTypeAndException_notIntancableProcessorClass() {

        AnnotationProcessorWrapper.wrapProcessor(InvalidProcessor.class, IllegalStateException.class);

    }


    @Test(expected = IllegalArgumentException.class)
    public void createWrapperWithTypeOnProcessorWithNoArgConstructor() {

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(TestAnnotationProcessorWithMissingNoArgConstructor.class);


    }

    @Test(expected = IllegalArgumentException.class)
    public void createWrapperWithNullValuedType() {

        Processor unit = AnnotationProcessorWrapper.wrapProcessor((Class) null);

    }

    @Test
    public void testWrappedSupportedOptionsCall() {

        AbstractProcessor processorSpy = Mockito.spy(AbstractProcessor.class);

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(processorSpy);
        unit.getSupportedOptions();

        Mockito.verify(processorSpy).getSupportedOptions();


    }

    @Test
    public void testWrappedSupportedAnnotationTypesCall() {

        AbstractProcessor processorSpy = Mockito.spy(AbstractProcessor.class);

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(processorSpy);
        unit.getSupportedAnnotationTypes();

        Mockito.verify(processorSpy).getSupportedAnnotationTypes();


    }

    @Test
    public void testWrappedSupportedSourceVersionCall() {

        AbstractProcessor processorSpy = Mockito.spy(AbstractProcessor.class);

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(processorSpy);
        unit.getSupportedSourceVersion();

        Mockito.verify(processorSpy).getSupportedSourceVersion();


    }


    @Test
    public void testWrappedInitCall() {

        AbstractProcessor processorSpy = Mockito.spy(AbstractProcessor.class);

        Messager messager = Mockito.spy(Messager.class);
        ProcessingEnvironment processingEnvironment = Mockito.spy(ProcessingEnvironment.class);
        Mockito.when(processingEnvironment.getMessager()).thenReturn(messager);

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(processorSpy);
        unit.init(processingEnvironment);

        Mockito.verify(processorSpy).init(processingEnvironment);


    }


    @Test
    public void testWrappedProcessCall() {

        AbstractProcessor processorSpy = Mockito.spy(AbstractProcessor.class);

        Messager messager = Mockito.spy(Messager.class);
        ProcessingEnvironment processingEnvironment = Mockito.spy(ProcessingEnvironment.class);
        Mockito.when(processingEnvironment.getMessager()).thenReturn(messager);

        AnnotationProcessorWrapper unit = AnnotationProcessorWrapper.wrapProcessor(processorSpy);
        unit.init(processingEnvironment);


        Set<? extends TypeElement> set = new HashSet<>();
        RoundEnvironment roundEnvironment = Mockito.mock(RoundEnvironment.class);


        unit.process(set, roundEnvironment);

        Mockito.verify(messager).printMessage(Diagnostic.Kind.NOTE, unit.getProcessorWasAppliedMessage());
        Mockito.verify(processorSpy).process(set, roundEnvironment);

    }


    @Test
    public void testWrappedCompletionsCall() {

        AbstractProcessor processorSpy = Mockito.spy(AbstractProcessor.class);

        Element element = Mockito.mock(Element.class);
        AnnotationMirror annotationMirror = Mockito.mock(AnnotationMirror.class);
        ExecutableElement executableElement = Mockito.mock(ExecutableElement.class);
        String str = "XX";


        Processor unit = AnnotationProcessorWrapper.wrapProcessor(processorSpy);
        unit.getCompletions(element, annotationMirror, executableElement, str);

        Mockito.verify(processorSpy).getCompletions(element, annotationMirror, executableElement, str);


    }

    @Test
    public void getSupportedAnnotationsDefinedPerAnnotationCorrectly() {

        Processor unit = AnnotationProcessorWrapper.wrapProcessor(new TestAnnotationProcessor());

        MatcherAssert.assertThat(unit.getSupportedAnnotationTypes(), Matchers.contains(TestAnnotation.class
                .getCanonicalName()));


    }

    // ----------------------------------------------------
    // --  Test exception handling and detection on wrapper.
    // ----------------------------------------------------


    @Test
    public void process_withoutExpectedExceptionShouldSucceed() {

        Cute.unitTest().when(
                        processingEnvironment -> {

                        })
                .thenExpectThat().compilationSucceeds()
                .executeTest();


    }


    @Test
    public void process_testExpectedExceptionIsThrown_assertionShouldSucceed() {


        Cute.unitTest().when(processingEnvironment -> {
                    throw new IllegalArgumentException();
                })
                .thenExpectThat().exceptionIsThrown(IllegalArgumentException.class)
                .executeTest();


    }

    @Test
    public void process_testExpectedExceptionNotThrown_assertionShouldFail() {

        try {
            Cute.unitTest().when(processingEnvironment -> {

            })
                    .thenExpectThat().exceptionIsThrown(IllegalArgumentException.class)
                    .executeTest();
        } catch (AssertionError e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Expected exception of type 'java.lang.IllegalArgumentException'"));
            return;
        }

        MatcherAssert.assertThat("Expected assertion error wasn't triggered", false);

    }

    @Test
    public void process_testUnexpectedExceptionWasThrown_assertionShouldFail() {

        try {
            Cute.unitTest().when(processingEnvironment -> {
                throw new IllegalStateException();
            })
                    .thenExpectThat().exceptionIsThrown(IllegalArgumentException.class)
                    .executeTest();
        } catch (Throwable e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Expected exception of type 'java.lang.IllegalArgumentException' but exception of type 'java.lang.IllegalStateException' was thrown instead"));
        }

    }

    @Test
    public void process_testUnexpectedExceptionWasThrownWhenExpectedExceptionNotSet_assertionShouldFail() {

        try {
            Cute.unitTest().when(
                            processingEnvironment -> {
                                throw new IllegalStateException();
                            })
                    .thenExpectThat().compilationFails()
                    .executeTest();
        } catch (Throwable e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("An unexpected exception of type 'java.lang.IllegalStateException'"));
        }

    }

    @Test
    public void getWrappedProcessor_testGet() {

        Processor processor = Mockito.mock(Processor.class);

        AnnotationProcessorWrapper unit = AnnotationProcessorWrapper.wrapProcessor(processor);

        MatcherAssert.assertThat(unit.getWrappedProcessor(), Matchers.is(processor));

    }


}
