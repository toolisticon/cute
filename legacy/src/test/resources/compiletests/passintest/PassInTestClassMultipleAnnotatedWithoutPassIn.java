package io.toolisticon.annotationprocessortoolkit.testhelper.compiletest;

import io.toolisticon.cute.PassIn;
import io.toolisticon.cute.TestAnnotation;

@PassIn
public class PassInTestClassMultipleAnnotatedWithoutPassIn {

    @PassIn
    public static class InnerTestClass {

    }

}