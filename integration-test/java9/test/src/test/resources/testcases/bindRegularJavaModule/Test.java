package io.toolisticon.compiletesting.integrationtest.java9;

import io.toolisticon.compiletesting.integrationtest.java9.regularmodule.RegularModuleTestClass;

import javax.annotation.processing.Processor;

public class Test {

    public static void testCall(Processor processor) {

    }

    public static void secondTestCall(RegularModuleTestClass testClass) {
        testClass.testMethod();
    }


}

