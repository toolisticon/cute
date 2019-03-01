package io.toolisticon.compiletesting.integrationtest.java9;

import io.toolisticon.compiletesting.integrationtest.java9.unnamednonmodule.UnnamedAutomaticModuleTestClass;

import javax.annotation.processing.Processor;

public class Test {

    public static void testCall(Processor processor) {

    }

    public static void secondTestCall(UnnamedAutomaticModuleTestClass testClass) {
        testClass.testMethod();
    }


}

