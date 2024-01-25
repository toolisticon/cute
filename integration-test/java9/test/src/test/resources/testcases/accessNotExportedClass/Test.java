package io.toolisticon.cute.integrationtest.javanine;

import io.toolisticon.cute.integrationtest.javanine.regularmodule.RegularModuleTestClass;
import io.toolisticon.cute.integrationtest.javanine.regularmodule.notexported.NotExportedClass;

import javax.annotation.processing.Processor;

public class Test {

    final static NotExportedClass instance = new NotExportedClass();

    public static void testCall(Processor processor) {

    }

    public static void secondTestCall(RegularModuleTestClass testClass) {
        testClass.testMethod();
    }


}

