package io.toolisticon.cute;

import io.toolisticon.cute.CuteApi;
import io.toolisticon.cute.Cute;
import io.toolisticon.cute.UnitTest;
import io.toolisticon.cute.UnitTestForTestingAnnotationProcessors;
import io.toolisticon.cute.common.SimpleTestProcessor1;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Unit Test for checking correctness of compiler messages checks.
 */
public class CompilerMessageCheckTest {

    CuteApi.UnitTestRootInterface builder = Cute.unitTest();

    @Test
    public void testComplexCompilerMessageCheck_findMessage_withAll() {

        builder.when()
                .passInElement().<TypeElement>fromSourceFile("/AnnotationProcessorUnitTestTestClass.java")
                .intoUnitTest(new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
                    }
                })
                .thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().atSource("/AnnotationProcessorUnitTestTestClass.java").atLine(13).atColumn(8).equals("ABC")
                .executeTest();
    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongSource() {

        builder.when().passInElement().<TypeElement>fromSourceFile("/AnnotationProcessorUnitTestTestClass.java")
                .intoUnitTest(new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
                    }
                })
                .thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().atSource("/XYZ.java").atLine(15).atColumn(8).equals("ABC")
                .executeTest();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongLine() {

        builder.when().passInElement().<TypeElement>fromSourceFile("/AnnotationProcessorUnitTestClass.java")
                .intoUnitTest(new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
                    }
                }).thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLine(3).atColumn(8).equals("ABC")
                .executeTest();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongColumn() {

        builder.when().passInElement().<TypeElement>fromSourceFile("/AnnotationProcessorUnitTestClass.java")
                .intoUnitTest(new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
                    }
                }).thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLine(13).atColumn(7).equals("ABC")

                .executeTest();

    }

    @Test(expected = AssertionError.class)
    public void testComplexCompilerMessageCheck_dontFindMessage_withAll_wrongMessage() {

        builder.when().passInElement().<TypeElement>fromSourceFile("/AnnotationProcessorUnitTestClass.java")
                .intoUnitTest(new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
                    }
                }).thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLine(13).atColumn(8).equals("BC")

                .executeTest();

    }

    @Test
    public void testComplexCompilerMessageCheck_findMessageSubstring_withAll() {

        builder.when().passInElement().<TypeElement>fromSourceFile("/AnnotationProcessorUnitTestTestClass.java")
                .intoUnitTest(new UnitTest<TypeElement>() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement element) {
                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABC", element);
                    }
                }).thenExpectThat().compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().atSource("/AnnotationProcessorUnitTestTestClass.java").atLine(13).atColumn(8).contains("BC")
                .executeTest();

    }

    public void xyx(){
        builder.when()
                .passInProcessor(SimpleTestProcessor1.class)
                .andPassInElement().<TypeElement>fromSourceFile("/AnnotationProcessorUnitTestClass.java")
                .intoUnitTest(new UnitTestForTestingAnnotationProcessors<SimpleTestProcessor1, TypeElement>() {
                    @Override
                    public void unitTest(SimpleTestProcessor1 unit, ProcessingEnvironment processingEnvironment, TypeElement element) {

                    }
                })
                .thenExpectThat()
                .compilationSucceeds()
                .andThat().compilerMessage().ofKindWarning().atSource("/AnnotationProcessorUnitTestClass.java").atLine(13).atColumn(8).contains("BC")
                .executeTest();
    }

}
