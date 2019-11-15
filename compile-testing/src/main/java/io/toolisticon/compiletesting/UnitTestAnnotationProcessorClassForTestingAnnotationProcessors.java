package io.toolisticon.compiletesting;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

/**
 * Base annotation processor for unit testing of initialized annotation processors.
 */
class UnitTestAnnotationProcessorClassForTestingAnnotationProcessors<UNIT_PROCESSOR extends Processor> extends AbstractProcessor {

    private static final Set<String> SUPPORTED_ANNOTATION_TYPES = new HashSet<String>();

    static {
        SUPPORTED_ANNOTATION_TYPES.add(TestAnnotation.class.getCanonicalName());
    }

    /**
     * The unit test processor instance to use.
     */
    private final UnitTestProcessorForTestingAnnotationProcessors<UNIT_PROCESSOR> unitTestProcessorForTestingAnnotationProcessors;
    private final UNIT_PROCESSOR processorUnderTest;


    public UnitTestAnnotationProcessorClassForTestingAnnotationProcessors(UNIT_PROCESSOR processorUnderTest, UnitTestProcessorForTestingAnnotationProcessors<UNIT_PROCESSOR> unitTestProcessorForTestingAnnotationProcessors) {
        this.processorUnderTest = processorUnderTest;
        this.unitTestProcessorForTestingAnnotationProcessors = unitTestProcessorForTestingAnnotationProcessors;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        // call init method of annotation processor under test
        processorUnderTest.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return SUPPORTED_ANNOTATION_TYPES;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!roundEnv.processingOver()) {

            Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(TestAnnotation.class);

            if (set.size() == 1) {

                unitTestProcessorForTestingAnnotationProcessors.unitTest(processorUnderTest, this.processingEnv, (TypeElement) set.iterator().next());

            } else {

                throw new AssertionError("PRECONDITION: Expected to find exactly one element annotated with TestAnnotation in unit test");

            }

        }

        return false;
    }

    protected void triggerError(String message) {

        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);

    }

}
