package io.toolisticon.cute;

import io.toolisticon.cute.CuteApi.BlackBoxTestFinalGivenInterface;
import io.toolisticon.cute.CuteApi.ExecuteCustomAssertionException;
import io.toolisticon.cute.common.SimpleTestProcessor1;
import io.toolisticon.cute.common.SimpleTestProcessor2;
import io.toolisticon.cute.common.SimpleTestProcessor3;
import io.toolisticon.cute.extension.api.AssertionSpiServiceLocator;
import io.toolisticon.cute.matchers.CoreGeneratedFileObjectMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.lang.AssertionError;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Unit test for fluent api {@link CuteApi}.
 */
public class CuteApiTest {

    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // -- Black Box Tests
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    @Test
    public void test_blackbox_addProcessors_chained() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestSourceFilesAndProcessorInterfaceImpl) Cute.blackBoxTest().given().processor(SimpleTestProcessor1.class)
                .andProcessor(SimpleTestProcessor2.class)
                .andProcessor(SimpleTestProcessor3.class)).backingBean;
        MatcherAssert.assertThat(unit.processors(), Matchers.containsInAnyOrder(SimpleTestProcessor1.class, SimpleTestProcessor2.class, SimpleTestProcessor3.class));

    }

    @Test
    public void test_blackbox_addProcessors_viaVarargs() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestSourceFilesInterfaceImpl) Cute.blackBoxTest().given()
                .processors(SimpleTestProcessor1.class, SimpleTestProcessor2.class, SimpleTestProcessor3.class)).backingBean;
        MatcherAssert.assertThat(unit.processors(), Matchers.containsInAnyOrder(SimpleTestProcessor1.class, SimpleTestProcessor2.class, SimpleTestProcessor3.class));

    }

    @Test
    public void test_blackbox_addProcessors_viaIterable() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestSourceFilesInterfaceImpl) Cute.blackBoxTest().given()
                .processors(Arrays.asList(SimpleTestProcessor1.class, SimpleTestProcessor2.class, SimpleTestProcessor3.class))).backingBean;
        MatcherAssert.assertThat(unit.processors(), Matchers.containsInAnyOrder(SimpleTestProcessor1.class, SimpleTestProcessor2.class, SimpleTestProcessor3.class));

    }

    @Test
    public void test_blackbox_noProcessors() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestSourceFilesInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()).backingBean;
        MatcherAssert.assertThat(unit.processors(), Matchers.empty());

    }


    @Test
    public void test_blackbox_addSourceFiles_viaVarArgs() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors().andSourceFiles("/compiletests/TestClass.java", "/compiletests/TestClassWithImplementedInterface.java").andSourceFiles("/compiletests/TestClassWithInnerClasses.java")).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(3));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/compiletests/TestClass.java", "/compiletests/TestClassWithImplementedInterface.java", "/compiletests/TestClassWithInnerClasses.java"));

    }

    @Test
    public void test_blackbox_addSourceFiles_viaString() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFile("io.toolisticon.cute.test.TestClass1", "package io.toolisticon.cute.test;\npublic class TestClass1{}")
                .andSourceFile("io.toolisticon.cute.test.TestClass2", "package io.toolisticon.cute.test;\npublic class TestClass2{}")
        ).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(2));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/io/toolisticon/cute/test/TestClass1.java", "/io/toolisticon/cute/test/TestClass2.java"));

    }

    @Test
    public void test_blackbox_addSourceFiles_viaJavaFileObject() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/compiletests/TestClass.java"), JavaFileObjectUtils.readFromResource("/compiletests/TestClassWithImplementedInterface.java"))
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/compiletests/TestClassWithInnerClasses.java"))
        ).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(3));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/compiletests/TestClass.java", "/compiletests/TestClassWithImplementedInterface.java", "/compiletests/TestClassWithInnerClasses.java"));

    }

    @Test
    public void test_blackbox_addSourceFiles_viaFolder() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFilesFromFolders("/compiletests/compileroptionstest")
                .andSourceFilesFromFolders("/compiletests/exceptionthrown")
        ).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(2));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/compiletests/compileroptionstest/Java8Code.java", "/compiletests/exceptionthrown/ExceptionThrownUsecase.java"));

    }

    @Test
    public void test_blackbox_addCompilerOptions() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .andUseCompilerOptions("ABC","DEF")
                .andUseCompilerOptions("HIJ")
        ).backingBean;
        MatcherAssert.assertThat(unit.compilerOptions(), Matchers.hasSize(3));
        MatcherAssert.assertThat(unit.compilerOptions(), Matchers.containsInAnyOrder("ABC", "DEF", "HIJ"));

    }

    @Test
    public void test_blackbox_addModules() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .andUseModules("ABC","DEF")
                .andUseModules("HIJ")
        ).backingBean;
        MatcherAssert.assertThat(unit.modules(), Matchers.hasSize(3));
        MatcherAssert.assertThat(unit.modules(), Matchers.containsInAnyOrder("ABC", "DEF", "HIJ"));

    }

    @Test
    public void test_blackbox_compilationSucceeds() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
        ).backingBean;
        MatcherAssert.assertThat(unit.compilationSucceeded(), Matchers.is(true));

    }

    @Test
    public void test_blackbox_compilationFails() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationFails()
        ).backingBean;
        MatcherAssert.assertThat(unit.compilationSucceeded(), Matchers.is(false));

    }

    @Test
    public void test_blackbox_exceptionIsThrown() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().exceptionIsThrown(IllegalStateException.class)
        ).backingBean;
        MatcherAssert.assertThat(unit.getExceptionChecks().getExceptionIsThrown(), Matchers.is(IllegalStateException.class));

    }

    @Test
    public void test_blackbox_compilerMessage_error_contains() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindError().atSource("source").atLine(1).atColumn(2).contains("ABC", "DEF")
        ).backingBean;

        CuteApi.CompilerMessageCheckBB compilerMessage = unit.compilerMessageChecks().get(0);

        MatcherAssert.assertThat(compilerMessage.getKind(), Matchers.is(CuteApi.CompilerMessageKind.ERROR));
        MatcherAssert.assertThat(compilerMessage.getComparisonType(), Matchers.is(CuteApi.CompilerMessageComparisonType.CONTAINS));
        MatcherAssert.assertThat(compilerMessage.atSource(), Matchers.is("source"));
        MatcherAssert.assertThat(compilerMessage.atLine(), Matchers.is(1));
        MatcherAssert.assertThat(compilerMessage.atColumn(), Matchers.is(2));
        MatcherAssert.assertThat(compilerMessage.getSearchString(), Matchers.containsInAnyOrder("ABC", "DEF"));

    }

    @Test
    public void test_blackbox_compilerMessage_equals() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindError().atSource("source").atLine(1).atColumn(2).equals("ABC")
        ).backingBean;

        CuteApi.CompilerMessageCheckBB compilerMessage = unit.compilerMessageChecks().get(0);

        MatcherAssert.assertThat(compilerMessage.getKind(), Matchers.is(CuteApi.CompilerMessageKind.ERROR));
        MatcherAssert.assertThat(compilerMessage.getComparisonType(), Matchers.is(CuteApi.CompilerMessageComparisonType.EQUALS));
        MatcherAssert.assertThat(compilerMessage.atSource(), Matchers.is("source"));
        MatcherAssert.assertThat(compilerMessage.atLine(), Matchers.is(1));
        MatcherAssert.assertThat(compilerMessage.atColumn(), Matchers.is(2));
        MatcherAssert.assertThat(compilerMessage.getSearchString(), Matchers.containsInAnyOrder("ABC"));

    }

    @Test
    public void test_blackbox_compilerMessage_multiple() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindError().atSource("source1").atLine(1).atColumn(2).contains("ABC", "DEF")
                .andThat().compilerMessage().ofKindError().atSource("source2").atLine(1).atColumn(2).contains("ABC", "DEF")
        ).backingBean;

        MatcherAssert.assertThat(unit.compilerMessageChecks(), Matchers.hasSize(2));

    }

    @Test
    public void test_blackbox_compilerMessage_warning() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().contains("ABC", "DEF")

        ).backingBean;

        MatcherAssert.assertThat(unit.compilerMessageChecks().get(0).getKind(), Matchers.is(CuteApi.CompilerMessageKind.WARNING));


    }

    @Test
    public void test_blackbox_compilerMessage_mandatoryWarning() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindMandatoryWarning().contains("ABC", "DEF")

        ).backingBean;

        MatcherAssert.assertThat(unit.compilerMessageChecks().get(0).getKind(), Matchers.is(CuteApi.CompilerMessageKind.MANDATORY_WARNING));


    }

    @Test
    public void test_blackbox_compilerMessage_mote() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindNote().contains("ABC", "DEF")

        ).backingBean;

        MatcherAssert.assertThat(unit.compilerMessageChecks().get(0).getKind(), Matchers.is(CuteApi.CompilerMessageKind.NOTE));


    }


    @Test
    public void test_blackbox_javaFileCheck_exists() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, "Abc", JavaFileObject.Kind.SOURCE).exists()

        ).backingBean;

        MatcherAssert.assertThat(unit.javaFileObjectChecks().get(0).getLocation(), Matchers.is(StandardLocation.SOURCE_OUTPUT));
        MatcherAssert.assertThat(unit.javaFileObjectChecks().get(0).getClassName(), Matchers.is("Abc"));
        MatcherAssert.assertThat(unit.javaFileObjectChecks().get(0).getKind(), Matchers.is(JavaFileObject.Kind.SOURCE));
        MatcherAssert.assertThat(unit.javaFileObjectChecks().get(0).getCheckType(), Matchers.is(CuteApi.FileObjectCheckType.EXISTS));


    }

    @Test
    public void test_blackbox_javaFileCheck_doesntExist() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, "Abc", JavaFileObject.Kind.SOURCE).doesntExist()

        ).backingBean;

        MatcherAssert.assertThat(unit.javaFileObjectChecks().get(0).getLocation(), Matchers.is(StandardLocation.SOURCE_OUTPUT));
        MatcherAssert.assertThat(unit.javaFileObjectChecks().get(0).getClassName(), Matchers.is("Abc"));
        MatcherAssert.assertThat(unit.javaFileObjectChecks().get(0).getKind(), Matchers.is(JavaFileObject.Kind.SOURCE));
        MatcherAssert.assertThat(unit.javaFileObjectChecks().get(0).getCheckType(), Matchers.is(CuteApi.FileObjectCheckType.DOESNT_EXIST));


    }

    @Test
    public void test_blackbox_javaFileCheck_multiple() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, "Abc", JavaFileObject.Kind.SOURCE).doesntExist()
                .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, "Abc", JavaFileObject.Kind.SOURCE).doesntExist()

        ).backingBean;

        MatcherAssert.assertThat(unit.javaFileObjectChecks(), Matchers.hasSize(2));


    }

    @Test
    public void test_blackbox_javaFileCheck_matches() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.blackBoxTest().given()
                .noProcessors()
                .andSourceFiles("/compiletests/TestClass.java")
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, "Abc", JavaFileObject.Kind.SOURCE).matches(CoreGeneratedFileObjectMatchers.createRegexMatcher("DEF"))
                .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, "Abc", JavaFileObject.Kind.SOURCE).matches(CoreGeneratedFileObjectMatchers.createRegexMatcher("DEF"))

        ).backingBean;

        MatcherAssert.assertThat(unit.javaFileObjectChecks(), Matchers.hasSize(2));


    }

    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // -- Unit Tests
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    @Test
    public void test_unittest_addSourceFiles_viaVarArgs() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                .useSourceFiles("/compiletests/TestClass.java", "/compiletests/TestClassWithImplementedInterface.java")
                .useSourceFiles("/compiletests/TestClassWithInnerClasses.java")).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(4));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/compiletests/TestClass.java", "/compiletests/TestClassWithImplementedInterface.java", "/compiletests/TestClassWithInnerClasses.java", "/AnnotationProcessorUnitTestClass.java"));

    }

    @Test
    public void test_unittest_addSourceFiles_viaString() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                .useSourceFile("io.toolisticon.cute.test.TestClass1", "package io.toolisticon.cute.test;\npublic class TestClass1{}")
                .useSourceFile("io.toolisticon.cute.test.TestClass2", "package io.toolisticon.cute.test;\npublic class TestClass2{}")
        ).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(3));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/io/toolisticon/cute/test/TestClass1.java", "/io/toolisticon/cute/test/TestClass2.java", "/AnnotationProcessorUnitTestClass.java"));

    }

    @Test
    public void test_unittest_addSourceFiles_viaJavaFileObject() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                .useSourceFile(JavaFileObjectUtils.readFromResource("/compiletests/TestClass.java"))
                .useSourceFile(JavaFileObjectUtils.readFromResource("/compiletests/TestClassWithImplementedInterface.java"))
                .useSourceFile(JavaFileObjectUtils.readFromResource("/compiletests/TestClassWithInnerClasses.java"))
        ).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(4));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/compiletests/TestClass.java", "/compiletests/TestClassWithImplementedInterface.java", "/compiletests/TestClassWithInnerClasses.java", "/AnnotationProcessorUnitTestClass.java"));

    }

    @Test
    public void test_unittest_addSourceFiles_viaResourceChaining() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                .useSourceFile("/compiletests/TestClass.java")
                .useSourceFile("/compiletests/TestClassWithImplementedInterface.java")
                .useSourceFile("/compiletests/TestClassWithInnerClasses.java")
        ).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(4));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/compiletests/TestClass.java", "/compiletests/TestClassWithImplementedInterface.java", "/compiletests/TestClassWithInnerClasses.java", "/AnnotationProcessorUnitTestClass.java"));

    }

    @Test
    public void test_unittest_addSourceFiles_viaFolder() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                .useSourceFilesFromFolders("/compiletests/compileroptionstest")
                .useSourceFilesFromFolders("/compiletests/exceptionthrown")
        ).backingBean;
        MatcherAssert.assertThat(unit.sourceFiles(), Matchers.hasSize(3));
        MatcherAssert.assertThat(unit.sourceFiles().stream().map(JavaFileObject::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("/compiletests/compileroptionstest/Java8Code.java", "/compiletests/exceptionthrown/ExceptionThrownUsecase.java", "/AnnotationProcessorUnitTestClass.java"));

    }

    @Test
    public void test_unittest_addCompilerOptions() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                .useSourceFiles("/compiletests/TestClass.java")
                .useCompilerOptions("ABC","DEF")
                .useCompilerOptions("HIJ")
        ).backingBean;
        MatcherAssert.assertThat(unit.compilerOptions(), Matchers.hasSize(3));
        MatcherAssert.assertThat(unit.compilerOptions(), Matchers.containsInAnyOrder("ABC", "DEF", "HIJ"));

    }

    @Test
    public void test_unittest_addModules() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                .useSourceFiles("/compiletests/TestClass.java")
                .useModules("ABC","DEF")
                .useModules("HIJ")
        ).backingBean;
        MatcherAssert.assertThat(unit.modules(), Matchers.hasSize(3));
        MatcherAssert.assertThat(unit.modules(), Matchers.containsInAnyOrder("ABC", "DEF", "HIJ"));

    }

    @Test
    public void test_unittest_compilationSucceeds() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.unitTest().given()
                .useSourceFiles("/compiletests/TestClass.java")
                .when().unitTestWithoutPassIn( processingEnvironment -> {

                })
                .thenExpectThat().compilationSucceeds()
        ).backingBean;
        MatcherAssert.assertThat(unit.compilationSucceeded(), Matchers.is(true));

    }

    @Test
    public void test_unittest_compilationFails() {

        CuteApi.CompilerTestBB unit = ((Cute.CompilerTestExpectAndThatInterfaceImpl) Cute.unitTest().given()
                .useSourceFiles("/compiletests/TestClass.java")
                .when().unitTestWithoutPassIn( processingEnvironment -> {

                })
                .thenExpectThat().compilationFails()
        ).backingBean;
        MatcherAssert.assertThat(unit.compilationSucceeded(), Matchers.is(false));

    }
    
    @Test
    public void test_unittest_andResourceFiles() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                
        		.useResourceFile("AP", "/compiletests/classpathtest/AR")
        		.useResourceFile("BP", "/compiletests/classpathtest/BR")
        		
        ).backingBean;
        MatcherAssert.assertThat(unit.resourceFiles(), Matchers.hasSize(2));
        
        MatcherAssert.assertThat(unit.resourceFiles().get(0).targetPackageNameOrAbsolutePath(), Matchers.is("AP"));
        MatcherAssert.assertThat(unit.resourceFiles().get(0).resource(), Matchers.is("/compiletests/classpathtest/AR"));
        
        MatcherAssert.assertThat(unit.resourceFiles().get(1).targetPackageNameOrAbsolutePath(), Matchers.is("BP"));
        MatcherAssert.assertThat(unit.resourceFiles().get(1).resource(), Matchers.is("/compiletests/classpathtest/BR"));
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_unittest_andResourceFiles_nonexistingResource() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                
        		.useResourceFile("AP", "/compiletests/classpathtest/ARR")
        		
        ).backingBean;
       
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_unittest_andResourceFiles_directoryResource() {

        CuteApi.CompilerTestBB unit = ((Cute.UnitTestGivenInterfaceImpl) Cute.unitTest().given()
                
        		.useResourceFile("AP", "/compiletests/classpathtest")
        		
        ).backingBean;
       
        
    }
    
    @Test
    public void test_blackboxtest_andResourceFiles() {

        CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
        		.noProcessors()
        		
        		.andResourceFile("AP", "/compiletests/classpathtest/AR")
        		.andResourceFile("BP", "/compiletests/classpathtest/BR")
        		
        ).backingBean;
        MatcherAssert.assertThat(unit.resourceFiles(), Matchers.hasSize(2));
        
        MatcherAssert.assertThat(unit.resourceFiles().get(0).targetPackageNameOrAbsolutePath(), Matchers.is("AP"));
        MatcherAssert.assertThat(unit.resourceFiles().get(0).resource(), Matchers.is("/compiletests/classpathtest/AR"));
        
        MatcherAssert.assertThat(unit.resourceFiles().get(1).targetPackageNameOrAbsolutePath(), Matchers.is("BP"));
        MatcherAssert.assertThat(unit.resourceFiles().get(1).resource(), Matchers.is("/compiletests/classpathtest/BR"));
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_blackboxtest_useResourceFiles_nonexistingResource() {

    	CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
        		.noProcessors()
        		
        		.andResourceFile("AP", "/compiletests/classpathtest/ARR")
        		
        ).backingBean;
       
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_blackboxtest_useResourceFiles_directoryResource() {

    	CuteApi.CompilerTestBB unit = ((Cute.BlackBoxTestFinalGivenInterfaceImpl) Cute.blackBoxTest().given()
        		.noProcessors()
        		
        		.andResourceFile("AP", "/compiletests/classpathtest")
        		
        ).backingBean;
       
        
    }
    
    @Test(expected = AssertionError.class)
    public void test_ifAssertionErrorsInCustomAssertionsAreHandledCorrectly() {

    	 Cute.blackBoxTest().given()
        		.noProcessors()
        		.andSourceFiles("/compiletests/TestClass.java")
        		.executeTest()
        		.executeCustomAssertions(e -> {
        			AssertionSpiServiceLocator.locate().fail("FAILED!!!!!");
        		});
        
    }
    
    @Test(expected = NullPointerException.class)
    public void test_ifExpectionsTriggeredInCustomAnnotationsAreHandledCorrectly() {

    	 Cute.blackBoxTest().given()
        		.noProcessors()
        		.andSourceFiles("/compiletests/TestClass.java")
        		.executeTest()
        		.executeCustomAssertions(e -> {
        			throw new NullPointerException();
        			
        		});
        
    }

}
