package io.toolisticon.compiletesting.integrationtest.java9;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import io.toolisticon.compiletesting.integrationtest.java9.namednonmodule.NamedAutomaticModuleTestClass;
import org.junit.Ignore;
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
                .useModules("io.toolisticon.compiletesting.integrationtest.java9.regularmodule")
                .compilationShouldSucceed()
                .testCompilation();

    }

    @Test
    public void testBindRegularJavaModule_WithoutUseModule() {

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/bindRegularJavaModule/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/bindRegularJavaModule/module-info.java"))
                .useModules()
                .compilationShouldSucceed()
                .testCompilation();

    }

    @Test
    @Ignore
    public void testBindNamedAutomaticJavaModule() {

        System.out.println("MODULE NAME: " + NamedAutomaticModuleTestClass.class.getModule().getName());

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/bindNamedAutomaticJavaModule/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/bindNamedAutomaticJavaModule/module-info.java"))
                .useModules()
                .compilationShouldSucceed()
                .testCompilation();

    }

    @Test
    @Ignore
    public void testBindUnnamedAutomaticJavaModule() {

        System.out.println("MODULE NAME: " + NamedAutomaticModuleTestClass.class.getModule().getName());

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/bindUnnamedAutomaticJavaModule/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/bindUnnamedAutomaticJavaModule/module-info.java"))
                .useModules()
                .compilationShouldSucceed()
                .testCompilation();

    }


}
