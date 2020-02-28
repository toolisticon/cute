package io.toolisticon.compiletesting;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Base annotation processor for unit testing of initialized annotation processors.
 *
 * @param <UNIT_PROCESSOR> the unit test processor to use
 * @param <ELEMENT_TYPE>   The expected type of the processed element
 */
class UnitTestAnnotationProcessorClassForTestingAnnotationProcessors<UNIT_PROCESSOR extends Processor, ELEMENT_TYPE extends Element> extends AbstractProcessor {

    private final Set<String> supportedAnnotationTypes = new HashSet<>();


    /**
     * The annotation type to search for.
     */
    private Class<? extends Annotation> annotationTypeToUse;

    /**
     * The unit test processor instance to use.
     */
    private final UnitTestForTestingAnnotationProcessors<UNIT_PROCESSOR, ELEMENT_TYPE> unitTestForTestingAnnotationProcessors;
    private final UNIT_PROCESSOR processorUnderTest;


    public UnitTestAnnotationProcessorClassForTestingAnnotationProcessors(UNIT_PROCESSOR processorUnderTest, Class<? extends Annotation> annotationTypeToUse, UnitTestForTestingAnnotationProcessors<UNIT_PROCESSOR, ELEMENT_TYPE> unitTestForTestingAnnotationProcessors) {
        this.processorUnderTest = processorUnderTest;
        this.annotationTypeToUse = annotationTypeToUse;
        this.supportedAnnotationTypes.add(annotationTypeToUse.getCanonicalName());
        this.unitTestForTestingAnnotationProcessors = unitTestForTestingAnnotationProcessors;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        // call init method of annotation processor under test
        processorUnderTest.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // just try to execute tests if annotation is processed == annotations size is 1
        if (!roundEnv.processingOver() && annotations.size() == 1) {

            Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(annotationTypeToUse);

            if (set.size() == 1) {

                try {
                    unitTestForTestingAnnotationProcessors.unitTest(processorUnderTest, this.processingEnv, (ELEMENT_TYPE) set.iterator().next());
                } catch (ClassCastException e) {
                    throw new FailingAssertionException(Constants.Messages.UNIT_TEST_PRECONDITION_INCOMPATIBLE_ELEMENT_TYPE.produceMessage());
                }

            } else {

                throw new AssertionError(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT.produceMessage(annotationTypeToUse.getCanonicalName()));

            }

        }

        return false;
    }

    protected void triggerError(String message) {

        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);

    }

}
