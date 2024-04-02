package io.toolisticon.cute;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Base annotation processor for unit testing of initialized annotation processors.
 *
 * @param <UNIT_PROCESSOR> the unit test processor to use
 */
class UnitTestAnnotationProcessorClassForTestingAnnotationProcessorsWithoutPassIn<UNIT_PROCESSOR extends Processor> extends AbstractUnitTestAnnotationProcessorClass {


    /**
     * The unit test processor instance to use.
     */
    private final UnitTestForTestingAnnotationProcessorsWithoutPassIn<UNIT_PROCESSOR> unitTestForTestingAnnotationProcessors;
    private final UNIT_PROCESSOR processorUnderTest;


    public UnitTestAnnotationProcessorClassForTestingAnnotationProcessorsWithoutPassIn(UNIT_PROCESSOR processorUnderTest, Class<? extends Annotation> annotationTypeToUse, UnitTestForTestingAnnotationProcessorsWithoutPassIn<UNIT_PROCESSOR> unitTestForTestingAnnotationProcessors) {
        super(annotationTypeToUse);
        this.processorUnderTest = processorUnderTest;
        this.unitTestForTestingAnnotationProcessors = unitTestForTestingAnnotationProcessors;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        // call init method of annotation processor under test
        processorUnderTest.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // just try to execute tests if annotation is processed == annotations size is 1
        if (!roundEnv.processingOver() && annotations.size() == 1) {

            try {
                unitTestForTestingAnnotationProcessors.unitTest(processorUnderTest, this.processingEnv);
            } catch (ClassCastException e) {
                if (e.getMessage() != null && e.getMessage().contains("com.sun.tools.javac.code.Symbol$ClassSymbol")) {
                    throw new FailingAssertionException(Constants.Messages.UNIT_TEST_PRECONDITION_INCOMPATIBLE_ELEMENT_TYPE.produceMessage());
                } else {
                    throw e;
                }
            }


        }

        return false;
    }


}
