package io.toolisticon.cute.common;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExceptionThrowerProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        throw new IllegalStateException("WHOOPS!!!");

    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {

        return new HashSet<String>(Arrays.asList(ExceptionThrowerAnnotation.class.getCanonicalName()));

    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return this.processingEnv;
    }
}