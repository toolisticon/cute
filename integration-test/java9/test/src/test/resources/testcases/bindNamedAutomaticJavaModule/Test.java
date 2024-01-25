package io.toolisticon.cute.integrationtest.javanine;

import io.toolisticon.cute.integrationtest.javanine.namednonmodule.NamedAutomaticModuleTestClass;

import javax.annotation.processing.Processor;

public class Test {

    public static void testCall(Processor processor) {

    }

    public static void secondTestCall(NamedAutomaticModuleTestClass testClass) {
        testClass.testMethod();
    }


}

