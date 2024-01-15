package io.toolisticon.cute.impl;

import io.toolisticon.cute.Constants;
import io.toolisticon.cute.CuteFluentApi;
import io.toolisticon.cute.FailingAssertionException;
import io.toolisticon.cute.UnitTest;
import io.toolisticon.cute.UnitTestAnnotationProcessorClass;
import io.toolisticon.cute.UnitTestAnnotationProcessorClassForTestingAnnotationProcessors;
import io.toolisticon.cute.UnitTestAnnotationProcessorClassWithPassIn;
import io.toolisticon.cute.UnitTestAnnotationProcessorClassWithoutPassIn;
import io.toolisticon.cute.UnitTestForTestingAnnotationProcessors;
import io.toolisticon.cute.UnitTestForTestingAnnotationProcessorsWithoutPassIn;
import io.toolisticon.cute.UnitTestWithoutPassIn;

import javax.annotation.processing.Completion;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class for {@link Processor}. Allows generic creation of generic unit tests.
 */
final class AnnotationProcessorWrapper implements Processor {

    private final Processor wrappedProcessor;
    private final Class<? extends Throwable> expectedThrownException;
    private Messager messager;

    private boolean firstRound = true;
    private boolean expectedExceptionWasThrown = false;

    private AnnotationProcessorWrapper(Processor processor) {
        this(processor, null);
    }

    private AnnotationProcessorWrapper(Processor processor, Class<? extends Throwable> expectedThrownException) {
        this.wrappedProcessor = processor;
        this.expectedThrownException = expectedThrownException;
    }


    @Override
    public Set<String> getSupportedOptions() {
        return wrappedProcessor.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return wrappedProcessor.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return wrappedProcessor.getSupportedSourceVersion();
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {

        // get messager
        messager = processingEnv.getMessager();

        wrappedProcessor.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (firstRound) {
            // now set note message before calling the processor
            messager.printMessage(Diagnostic.Kind.NOTE, getProcessorWasAppliedMessage());
            firstRound = false;
        }

        boolean returnValue;

        try {

            returnValue = wrappedProcessor.process(annotations, roundEnv);

        } catch (Throwable e) {

            // pass through assertions
            if (AssertionError.class.isAssignableFrom(e.getClass())) {
                throw (AssertionError) e;
            }

            if (this.expectedThrownException != null) {

                if (!this.expectedThrownException.isAssignableFrom(e.getClass())) {
                    throw new FailingAssertionException(
                            Constants.Messages.ASSERTION_GOT_UNEXPECTED_EXCEPTION_INSTEAD_OF_EXPECTED.produceMessage(
                                    this.expectedThrownException.getCanonicalName(),
                                    e.getClass().getCanonicalName(),
                                    e.getMessage() != null ? Constants.Messages.TOKEN__WITH_MESSAGE + e.getMessage() : "")
                            , e);
                }

            } else {

                // Got unexpected exception
                throw new FailingAssertionException(
                        Constants.Messages.ASSERTION_GOT_UNEXPECTED_EXCEPTION.produceMessage(
                                e.getClass().getCanonicalName(),
                                e.getMessage() != null ? Constants.Messages.TOKEN__WITH_MESSAGE + e.getMessage() : ""), e);

            }

            return true;

        }

        // check in last round if expected exception has been thrown
        if (roundEnv.processingOver() && expectedExceptionWasThrown && this.expectedThrownException != null) {
            throw new FailingAssertionException(
                    Constants.Messages.ASSERTION_EXPECTED_EXCEPTION_NOT_THROWN.produceMessage(this.expectedThrownException.getCanonicalName())
            );
        }

        return returnValue;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return wrappedProcessor.getCompletions(element, annotation, member, userText);
    }

    public String getProcessorWasAppliedMessage() {
        return CompileTestUtilities.getAnnotationProcessorWasAppliedMessage(wrappedProcessor);
    }

    public Processor getWrappedProcessor() {
        return wrappedProcessor;
    }

    public static AnnotationProcessorWrapper wrapProcessor(Processor processorToWrap) {
        return wrapProcessor(processorToWrap, null);
    }

    public static AnnotationProcessorWrapper wrapProcessor(Processor processorToWrap, Class<? extends Throwable> expectedThrownException) {

        if (processorToWrap == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("processor"));
        }

        return new AnnotationProcessorWrapper(processorToWrap, expectedThrownException);
    }

    public static <T extends Processor> AnnotationProcessorWrapper wrapProcessor(Class<T> processorTypeToWrap) {
        if (processorTypeToWrap == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("type"));
        }

        try {
            return new AnnotationProcessorWrapper(processorTypeToWrap.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_INSTANTIATE_PROCESSOR.produceMessage(processorTypeToWrap.getCanonicalName()), e);
        }

    }

    public static <T extends Processor> AnnotationProcessorWrapper wrapProcessor(Class<T> processorTypeToWrap, Class<? extends Throwable> expectedThrownException) {

        if (processorTypeToWrap == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("type"));
        }

        if (expectedThrownException == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("expected exception"));
        }

        try {
            return new AnnotationProcessorWrapper(processorTypeToWrap.getDeclaredConstructor().newInstance(), expectedThrownException);
        } catch (Exception e) {
            throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_INSTANTIATE_PROCESSOR.produceMessage(processorTypeToWrap.getCanonicalName()), e);
        }

    }


    static Set<AnnotationProcessorWrapper> getWrappedProcessors(CuteFluentApi.CompilerTestBB compilerTestBB) {

        // return cached wrapped processors if available


        Set<AnnotationProcessorWrapper> wrappedProcessors = new HashSet<>();


        // need to add unit test processor


        // TODO: THIS CASE HANDLES PROCESSOR INSTANCES - NORMALLY ONLY USED IN UNIT TESTS ?!?
        /*-
        for (Processor processor : compilerTestBB.processors()) {

            wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, expectedThrownException));

        }
        */
        if (compilerTestBB.unitTest() != null) {
            Processor processor = null;
            Class<? extends Annotation> annotationToScanFor =  Constants.DEFAULT_ANNOTATION;
            if (compilerTestBB.unitTest() instanceof UnitTest) {
                if (compilerTestBB.passInConfiguration() != null && compilerTestBB.passInConfiguration().getPassedInClass() != null) {

                    processor = new UnitTestAnnotationProcessorClassWithPassIn<>(
                            compilerTestBB.passInConfiguration().getPassedInClass(),
                            annotationToScanFor,
                            (UnitTest<Element>) compilerTestBB.unitTest());

                } else {
                    // This is the legacy case
                    processor = new UnitTestAnnotationProcessorClass<>(
                            annotationToScanFor,
                            (UnitTest<Element>) compilerTestBB.unitTest());
                }

            } else if (compilerTestBB.unitTest() instanceof UnitTestWithoutPassIn){

                processor = new UnitTestAnnotationProcessorClassWithoutPassIn(
                        annotationToScanFor,
                        (UnitTestWithoutPassIn) compilerTestBB.unitTest());

            } else if (compilerTestBB.unitTest() instanceof UnitTestForTestingAnnotationProcessors) {

                Processor processorUnderTest = null;
                try {
                    processorUnderTest = compilerTestBB.passInConfiguration().getPassedInProcessor().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_INSTANTIATE_PROCESSOR.produceMessage(compilerTestBB.passInConfiguration().getPassedInProcessor().getCanonicalName()));
                }

                processor = new UnitTestAnnotationProcessorClassForTestingAnnotationProcessors<Processor, Element>(
                        processorUnderTest,
                        annotationToScanFor,
                        (UnitTestForTestingAnnotationProcessors) compilerTestBB.unitTest());

            } else if (compilerTestBB.unitTest() instanceof UnitTestForTestingAnnotationProcessorsWithoutPassIn) {

            }

            if (processor != null) {
                wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, compilerTestBB.getExceptionIsThrown()));
            }
        }

        // Configured processors by class
        for (Class<? extends Processor> processorType : compilerTestBB.processors()) {

            try {
                Processor processor = processorType.getDeclaredConstructor().newInstance();

                wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, compilerTestBB.getExceptionIsThrown()));

            } catch (Exception e) {
                throw new IllegalArgumentException("Passed processor " + processorType.getCanonicalName() + " cannot be instantiated.", e);
            }

        }

        // TODO: CURRENTLY NOT IMPLEMENTED WITH NEW FLUENT API
        /*-
        for (CompileTestConfiguration.ProcessorWithExpectedException processor : this.processorsWithExpectedExceptions) {

            wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor.processorType, processor.throwable != null ? processor.throwable : expectedThrownException));

        }
        */


        return wrappedProcessors;

    }

}
