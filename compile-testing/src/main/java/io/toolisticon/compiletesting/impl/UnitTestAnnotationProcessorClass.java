package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.TestAnnotation;
import io.toolisticon.compiletesting.UnitTestProcessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

/**
 * Base annotation processor for unit tests.
 */
public class UnitTestAnnotationProcessorClass extends AbstractProcessor {

    private static final Set<String> SUPPORTED_ANNOTATION_TYPES = new HashSet<String>();

    static {
        SUPPORTED_ANNOTATION_TYPES.add(TestAnnotation.class.getCanonicalName());
    }

    /**
     * The unit test processor instance to use.
     */
    private final UnitTestProcessor unitTestProcessor;


    public UnitTestAnnotationProcessorClass(UnitTestProcessor unitTestProcessor) {
        this.unitTestProcessor = unitTestProcessor;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return SUPPORTED_ANNOTATION_TYPES;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(TestAnnotation.class);

        if (set.size() == 1) {

            unitTestProcessor.unitTest(this.processingEnv, (TypeElement) set.iterator().next());
        }

        return false;
    }

    protected void triggerError(String message) {

        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);

    }
}



