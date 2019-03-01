package io.toolisticon.compiletesting.integrationtest.java9;

import io.toolisticon.compiletesting.integrationtest.java9.namednonmodule.NamedAutomaticModuleTestClass;

import javax.annotation.processing.Processor;

public class Test {

    public static void testCall(Processor processor) {

    }

    public static void secondTestCall(NamedAutomaticModuleTestClass testClass) {
        testClass.testMethod();
    }


}

