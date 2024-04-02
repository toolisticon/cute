package io.toolisticon.annotationprocessortoolkit.testhelper.compiletest;

import io.toolisticon.cute.PassIn;
import io.toolisticon.cute.TestAnnotation;

@PassIn
@TestAnnotation
public class PassInTestClassMultipleAnnotatedWithMultiplePassIn {

    @PassIn
    @TestAnnotation
    public static class InnerTestClass {

    }

}