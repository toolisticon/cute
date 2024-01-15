package io.toolisticon.cute.integrationtest.java9;

import io.toolisticon.cute.JavaFileObjectUtils;
import io.toolisticon.cute.integrationtest.javanine.namednonmodule.NamedAutomaticModuleTestClass;
import org.junit.Test;

/**
 * Java 9+ related integration tests.
 * Addresses mainly the jigsaw module system.
 */
public class IntegrationTest {

    @Test
    public void testBindRegularJavaModule() {

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/bindRegularJavaModule/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/bindRegularJavaModule/module-info.java"))
                .useModules("io.toolisticon.cute.integrationtest.javanine.regularmodule")
                .compilationShouldSucceed()
                .executeTest();

    }


    @Test
    public void testBindRegularJavaModule_accessNotExportedClass() {

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/accessNotExportedClass/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/accessNotExportedClass/module-info.java"))
                .useModules("io.toolisticon.cute.integrationtest.javanine.regularmodule")
                .compilationShouldFail()
                .expectErrorMessageThatContains("io.toolisticon.cute.integrationtest.javanine.regularmodule.notexported", "is not visible")
                .executeTest();

    }

    @Test
    public void testBindNamedAutomaticJavaModule() {

        System.out.println("MODULE NAME: " + NamedAutomaticModuleTestClass.class.getModule().getName());

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/bindNamedAutomaticJavaModule/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/bindNamedAutomaticJavaModule/module-info.java"))
                .useModules("integration.test.javanine.namedautomaticmodule")
                .compilationShouldSucceed()
                .executeTest();

    }

    @Test
    public void testBindUnnamedAutomaticJavaModule() {

        System.out.println("MODULE NAME: " + NamedAutomaticModuleTestClass.class.getModule().getName());

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/bindUnnamedAutomaticJavaModule/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/bindUnnamedAutomaticJavaModule/module-info.java"))
                .useModules("integration.test.javanine.unnamedautomaticmodule")
                .compilationShouldSucceed()
                .executeTest();

    }


}
