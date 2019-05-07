package io.toolisticon.compiletesting.integrationtest.java9;

import io.toolisticon.compiletesting.integrationtest.java9.regularmodule.RegularModuleTestClass;
import io.toolisticon.compiletesting.integrationtest.java9.regularmodule.notexported.NotExportedClass;

import javax.annotation.processing.Processor;

public class Test {

    final static NotExportedClass instance = new NotExportedClass();

    public static void testCall(Processor processor) {

    }

    public static void secondTestCall(RegularModuleTestClass testClass) {
        testClass.testMethod();
    }


}

