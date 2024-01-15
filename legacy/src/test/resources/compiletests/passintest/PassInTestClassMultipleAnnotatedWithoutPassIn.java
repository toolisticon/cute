package io.toolisticon.annotationprocessortoolkit.testhelper.compiletest;

import io.toolisticon.cute.PassIn;
import io.toolisticon.cute.TestAnnotation;

@TestAnnotation
public class PassInTestClassMultipleAnnotatedWithoutPassIn {

    @TestAnnotation
    public static class InnerTestClass {

    }

}