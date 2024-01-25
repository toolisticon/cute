package io.toolisticon.annotationprocessortoolkit.testhelper.compiletest;

import io.toolisticon.cute.PassIn;
import io.toolisticon.cute.TestAnnotation;

@TestAnnotation
public class PassInTestClass {

    @PassIn
    @TestAnnotation
    public static class InnerTestClass {

    }

}