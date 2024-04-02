package io.toolisticon.cute.integrationtest.javanine;

import io.toolisticon.cute.integrationtest.javanine.unnamednonmodule.UnnamedAutomaticModuleTestClass;

import javax.annotation.processing.Processor;

public class Test {

    public static void testCall(Processor processor) {

    }

    public static void secondTestCall(UnnamedAutomaticModuleTestClass testClass) {
        testClass.testMethod();
    }


}

