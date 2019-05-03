package io.toolisticon.compiletesting.integrationtest.java9;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import io.toolisticon.compiletesting.integrationtest.java9.namednonmodule.NamedAutomaticModuleTestClass;
import org.junit.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
                .useModules("io.toolisticon.compiletesting.integrationtest.java9.regularmodule")
                .compilationShouldSucceed()
                .testCompilation();

    }

    @Test
    public void testBindNamedAutomaticJavaModule() {

        System.out.println("MODULE NAME: " + NamedAutomaticModuleTestClass.class.getModule().getName());

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/bindNamedAutomaticJavaModule/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/bindNamedAutomaticJavaModule/module-info.java"))
                .useModules("integration.test.java9.namedautomaticmodule")
                .compilationShouldSucceed()
                .testCompilation();

    }

    @Test
    public void testBindUnnamedAutomaticJavaModule() {

        System.out.println("MODULE NAME: " + NamedAutomaticModuleTestClass.class.getModule().getName());

        CompileTestBuilder
                .compilationTest()
                .addSources(
                        JavaFileObjectUtils.readFromResource("/testcases/bindUnnamedAutomaticJavaModule/Test.java"),
                        JavaFileObjectUtils.readFromResource("/testcases/bindUnnamedAutomaticJavaModule/module-info.java"))
                .useModules("integration.test.java9.unnamedautomaticmodule")
                .compilationShouldSucceed()
                .testCompilation();

    }


    @Test
    public void testJava9Stuff() {

        String automaticJar = "/Users/tobiasstamann/Projects/Opensource/compile-testing/compile-testing/target/compiletesting-0.3.1-SNAPSHOT.jar";
        File automaticJarFile = new File(automaticJar);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        StandardJavaFileManager stdJavaFileManager = compiler.getStandardFileManager(diagnostics, null, null);


        Set<ModuleReference> moduleReferenceSet = ModuleFinder.of(automaticJarFile.getParentFile().toPath()).findAll();
        System.out.println("!!!!!!!!!! Found modules :" + moduleReferenceSet.size());
        for (ModuleReference moduleReference : moduleReferenceSet) {
            System.out.println(moduleReference.descriptor().name() + " := " + moduleReference.location().toString());
        }

        List list = Arrays.asList(automaticJarFile.getParentFile().toPath());

        try {
            stdJavaFileManager.setLocationForModule(StandardLocation.MODULE_PATH, moduleReferenceSet.iterator().next().descriptor().name(), list);
            JavaFileManager.Location location = stdJavaFileManager.getLocationForModule(StandardLocation.MODULE_PATH, moduleReferenceSet.iterator().next().descriptor().name());

            System.out.println("Module location: " + location.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
