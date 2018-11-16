package io.toolisticon.compiletesting.testcases;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Test annotation processor. Does nothing.
 */

@SupportedAnnotationTypes("io.toolisticon.compiletesting.TestAnnotation")
public class TestAnnotationProcessorWithMissingNoArgConstructor extends AbstractProcessor {

    public TestAnnotationProcessorWithMissingNoArgConstructor(String x) {

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
