package io.toolisticon.compiletesting.common;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class SimpleTestProcessor1 extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        return new HashSet<String>(Arrays.asList(SimpleTestAnnotation1.class.getCanonicalName()));

    }
}