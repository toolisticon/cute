package io.toolisticon.cute;

import javax.annotation.processing.Completion;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
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

                // Exception has been found
                expectedExceptionWasThrown = true;

            } else {

                // Got unexpected exception
                throw new FailingAssertionException(
                        Constants.Messages.ASSERTION_GOT_UNEXPECTED_EXCEPTION.produceMessage(
                                e.getClass().getCanonicalName(),
                                e.getMessage() != null ? Constants.Messages.TOKEN__WITH_MESSAGE + e.getMessage() : ""), e);

            }

            return true;

        }

        // check in last round if expected exception has been thrown - in this case trigger assertion error
        if (roundEnv.processingOver() && !expectedExceptionWasThrown && this.expectedThrownException != null) {
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


    static Set<AnnotationProcessorWrapper> getWrappedProcessors(CuteApi.CompilerTestBB compilerTestBB) {

        Set<AnnotationProcessorWrapper> wrappedProcessors = new HashSet<>();

        if (compilerTestBB.testType() == CuteApi.TestType.UNIT && compilerTestBB.unitTest() != null) {
            Processor processor = null;

            // This is kind of difficult...
            // must determine which annotation must be used as trigger and for scanning
            // 1. passed in class - in this case the PassIn or annotationForScan must be used for scanning, DEFAULT ANNOTATION must be used as entry point
            // 2. Pass in by source - in this case annotationForScan isn't used, just PassIn or annotationForScan if set
            // 3. Implicit PassIn - just the default Source and Entry Point is available TestAnnotation Must be used

            if (compilerTestBB.unitTest() instanceof UnitTest) {
                if (compilerTestBB.passInConfiguration() != null && compilerTestBB.passInConfiguration().getPassedInClass() != null) {

                    // This is correct: DEFAULT AS ENTRY POINT AND PASSIN OR ANNOTATIONFORSCAN for scanning
                    processor = new UnitTestAnnotationProcessorClassWithPassIn<>(
                            compilerTestBB.passInConfiguration().getPassedInClass(),
                            compilerTestBB.passInConfiguration().getAnnotationToScanFor() != null ? compilerTestBB.passInConfiguration().getAnnotationToScanFor() : PassIn.class,
                            (UnitTest<?>) compilerTestBB.unitTest());

                } else {
                    // This is the legacy case
                    processor = new UnitTestAnnotationProcessorClass<>(
                            compilerTestBB.passInConfiguration() != null && compilerTestBB.passInConfiguration().getAnnotationToScanFor() != null ? compilerTestBB.passInConfiguration().getAnnotationToScanFor() : (compilerTestBB.passInConfiguration() != null && compilerTestBB.passInConfiguration().getPassInElement() ? PassIn.class : Constants.DEFAULT_ANNOTATION ),
                            (UnitTest<?>) compilerTestBB.unitTest());
                }

            } else if (compilerTestBB.unitTest() instanceof UnitTestWithoutPassIn) {

                processor = new UnitTestAnnotationProcessorClassWithoutPassIn(
                        Constants.DEFAULT_ANNOTATION,
                        (UnitTestWithoutPassIn) compilerTestBB.unitTest());

            } else if (compilerTestBB.unitTest() instanceof UnitTestForTestingAnnotationProcessors) {

                Processor processorUnderTest;
                try {
                    processorUnderTest = compilerTestBB.passInConfiguration().getPassedInProcessor().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_INSTANTIATE_PROCESSOR.produceMessage(compilerTestBB.passInConfiguration().getPassedInProcessor().getCanonicalName()));
                }

                if (compilerTestBB.passInConfiguration() != null && compilerTestBB.passInConfiguration().getPassedInClass() != null) {
                    processor = new UnitTestAnnotationProcessorClassForTestingAnnotationProcessorsWithPassIn<>(
                            processorUnderTest,
                            Constants.DEFAULT_ANNOTATION,
                            compilerTestBB.passInConfiguration().getPassedInClass(),
                            compilerTestBB.passInConfiguration().getAnnotationToScanFor() != null ? compilerTestBB.passInConfiguration().getAnnotationToScanFor() : PassIn.class,
                            (UnitTestForTestingAnnotationProcessors<Processor,Element>) compilerTestBB.unitTest()

                    );
                } else {
                    processor = new UnitTestAnnotationProcessorClassForTestingAnnotationProcessors<>(
                            processorUnderTest,
                            compilerTestBB.passInConfiguration().getAnnotationToScanFor() != null ? compilerTestBB.passInConfiguration().getAnnotationToScanFor() : (compilerTestBB.passInConfiguration().getPassInElement() ? PassIn.class : Constants.DEFAULT_ANNOTATION ),
                            (UnitTestForTestingAnnotationProcessors<Processor,Element>) compilerTestBB.unitTest());
                }


            } else if (compilerTestBB.unitTest() instanceof UnitTestForTestingAnnotationProcessorsWithoutPassIn) {

                Processor processorUnderTest = null;
                try {
                    processorUnderTest = compilerTestBB.passInConfiguration().getPassedInProcessor().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_INSTANTIATE_PROCESSOR.produceMessage(compilerTestBB.passInConfiguration().getPassedInProcessor().getCanonicalName()));
                }

                processor = new UnitTestAnnotationProcessorClassForTestingAnnotationProcessorsWithoutPassIn<>(
                        processorUnderTest,
                        Constants.DEFAULT_ANNOTATION,
                        (UnitTestForTestingAnnotationProcessorsWithoutPassIn<Processor>) compilerTestBB.unitTest());

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
                throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_INSTANTIATE_PROCESSOR.produceMessage(processorType.getCanonicalName()));
            }

        }



        return wrappedProcessors;

    }

}
