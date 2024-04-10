package io.toolisticon.cute.integrationtest.java9;

import io.toolisticon.cute.Cute;
import io.toolisticon.cute.JavaFileObjectUtils;
import io.toolisticon.cute.integrationtest.javanine.namednonmodule.NamedAutomaticModuleTestClass;
import org.junit.Test;

import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Java 9+ related integration tests.
 * Addresses mainly the jigsaw module system.
 */
public class IntegrationTest {

    @Test
    public void testBindRegularJavaModule() {

        Cute
                .blackBoxTest()
                .given().noProcessors()
                .andSourceFiles("/testcases/bindRegularJavaModule/Test.java",
                        "/testcases/bindRegularJavaModule/module-info.java")
                .andUseModules("io.toolisticon.cute.integrationtest.javanine.regularmodule")
                .whenCompiled()
                .thenExpectThat().compilationSucceeds()
                .executeTest();

    }


    @Test
    public void testBindRegularJavaModule_accessNotExportedClass() {

        Cute
                .blackBoxTest()
                .given()
                .noProcessors()
                .andSourceFiles("/testcases/accessNotExportedClass/Test.java","/testcases/accessNotExportedClass/module-info.java")
                .andUseModules("io.toolisticon.cute.integrationtest.javanine.regularmodule")
                .whenCompiled()
                .thenExpectThat()
                .compilationFails()
                .andThat().compilerMessage().ofKindError().contains("io.toolisticon.cute.integrationtest.javanine.regularmodule.notexported", "is not visible")
                .executeTest();

    }

    @Test
    public void testBindNamedAutomaticJavaModule() {

        System.out.println("MODULE NAME: " + NamedAutomaticModuleTestClass.class.getModule().getName());

        Cute
                .blackBoxTest()
                .given()
                .noProcessors()
                .andSourceFiles("/testcases/bindNamedAutomaticJavaModule/Test.java","/testcases/bindNamedAutomaticJavaModule/module-info.java")
                .andUseModules("integration.test.javanine.namedautomaticmodule")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .executeTest();

    }

    @Test
    public void testBindUnnamedAutomaticJavaModule() {

        System.out.println("MODULE NAME: " + NamedAutomaticModuleTestClass.class.getModule().getName());

        Cute
                .blackBoxTest()
                .given()
                .noProcessors()
                .andSourceFiles("/testcases/bindUnnamedAutomaticJavaModule/Test.java","/testcases/bindUnnamedAutomaticJavaModule/module-info.java")
                .andUseModules("integration.test.javanine.unnamedautomaticmodule")
                .whenCompiled()
                .thenExpectThat()
                .compilationSucceeds()
                .executeTest();

    }

    @Test
    public void testUnitTestWithModules() {
        Cute.unitTest().given().useSourceFilesFromFolders("/testcases/unitTest")
                .when()
                .passInElement().<TypeElement>fromGivenSourceFiles()
                .intoUnitTest( ((processingEnvironment, element) -> {
                    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE,"IT WORKED!!!");
                }))
                .thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindNote().equals("IT WORKED!!!" )
                .executeTest();
    }


}
