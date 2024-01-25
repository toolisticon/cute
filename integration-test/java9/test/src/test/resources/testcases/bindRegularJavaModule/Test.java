package io.toolisticon.cute.integrationtest.javanine;

import io.toolisticon.cute.integrationtest.javanine.regularmodule.RegularModuleTestClass;

import javax.annotation.processing.Processor;

public class Test {

    public static void testCall(Processor processor) {

    }

    public static void secondTestCall(RegularModuleTestClass testClass) {
        testClass.testMethod();
    }


}

