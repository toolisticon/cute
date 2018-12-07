package io.toolisticon.compiletesting;

import io.toolisticon.compiletesting.common.SimpleTestProcessor1;
import io.toolisticon.compiletesting.common.SimpleTestProcessor2;
import io.toolisticon.compiletesting.impl.CompileTestConfiguration;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class CompileTestBuilderTest {

    @Test
    public void test_UnitTest_successfullCompilation_build() {

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = JavaFileObjectUtils.readFromString("Jupp.txt", "TATA!");
        CompileTestBuilder
                .unitTest()
                .useProcessor(
                        new UnitTestProcessor() {
                            @Override
                            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "MANDATORY_WARNING");
                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "WARNING");
                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "NOTE");


                                try {
                                    FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", typeElement);
                                    Writer writer = fileObject.openWriter();
                                    writer.write("TATA!");
                                    writer.close();


                                } catch (IOException e) {

                                }

                            }
                        })
                .expectedWarningMessages("WARNING")
                .expectedMandatoryWarningMessages("MANDATORY_WARNING")
                .expectedNoteMessages("NOTE")
                .compilationShouldSucceed()
                .testCompilation();


    }

    @Test
    public void test_UnitTest_failingCompilation_build() {

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder
                .unitTest()
                .useProcessor(new UnitTestProcessor() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR");


                    }
                })
                .expectedErrorMessages("ERROR")
                .compilationShouldFail()
                .testCompilation();


    }


    @Test
    public void test_addWarningChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectedWarningMessages("WARN1");

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getWarningMessageCheck(), Matchers.containsInAnyOrder("WARN1"));

        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectedWarningMessages("WARN2");

        MatcherAssert.assertThat(builder2
                .createCompileTestConfiguration()
                .getWarningMessageCheck(), Matchers.containsInAnyOrder("WARN1", "WARN2"));

        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectedWarningMessages()
                .expectedWarningMessages(null);

        MatcherAssert.assertThat(builder3
                .createCompileTestConfiguration()
                .getWarningMessageCheck(), Matchers.containsInAnyOrder("WARN1", "WARN2"));


    }

    public void test_addMandatoryWarningChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectedMandatoryWarningMessages("MWARN1");

        MatcherAssert.assertThat(builder.createCompileTestConfiguration()
                .getMandatoryWarningMessageCheck(), Matchers.containsInAnyOrder("MWARN1"));

        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectedMandatoryWarningMessages("MWARN2");

        MatcherAssert.assertThat(builder2
                .createCompileTestConfiguration()
                .getMandatoryWarningMessageCheck(), Matchers.containsInAnyOrder("MWARN1", "MWARN2"));

        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectedMandatoryWarningMessages()
                .expectedMandatoryWarningMessages(null);

        MatcherAssert.assertThat(builder3
                .createCompileTestConfiguration()
                .getMandatoryWarningMessageCheck(), Matchers.containsInAnyOrder("MWARN1", "MWARN2"));


    }

    public void test_addNoteChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectedNoteMessages("NOTE1");

        MatcherAssert.assertThat(builder.createCompileTestConfiguration()
                .getNoteMessageCheck(), Matchers.containsInAnyOrder("NOTE1"));

        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectedNoteMessages("NOTE2");

        MatcherAssert.assertThat(builder2
                .createCompileTestConfiguration()
                .getNoteMessageCheck(), Matchers.containsInAnyOrder("NOTE1", "NOTE2"));

        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectedNoteMessages()
                .expectedNoteMessages(null);

        MatcherAssert.assertThat(builder3
                .createCompileTestConfiguration()
                .getNoteMessageCheck(), Matchers.containsInAnyOrder("NOTE1", "NOTE2"));


    }

    public void test_addErrorChecks() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .expectedErrorMessages("ERROR1");

        MatcherAssert.assertThat(builder.createCompileTestConfiguration()
                .getErrorMessageCheck(), Matchers.containsInAnyOrder("ERROR1"));

        CompileTestBuilder.CompilationTestBuilder builder2 = builder
                .expectedErrorMessages("ERROR2");

        MatcherAssert.assertThat(builder2
                .createCompileTestConfiguration()
                .getErrorMessageCheck(), Matchers.containsInAnyOrder("ERROR1", "ERROR2"));

        CompileTestBuilder.CompilationTestBuilder builder3 = builder2
                .expectedErrorMessages()
                .expectedErrorMessages(null);

        MatcherAssert.assertThat(builder3
                .createCompileTestConfiguration()
                .getErrorMessageCheck(), Matchers.containsInAnyOrder("ERROR1", "ERROR2"));


    }

    @Test
    public void test_compilationShouldSucceeed() {

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest();

        MatcherAssert.assertThat(builder.compilationShouldSucceed().createCompileTestConfiguration().getCompilationShouldSucceed(), Matchers.is(Boolean.TRUE));
        MatcherAssert.assertThat(builder.compilationShouldFail().createCompileTestConfiguration().getCompilationShouldSucceed(), Matchers.is(Boolean.FALSE));


    }

    @Test
    public void test_addSource() {

        JavaFileObject testSource1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject testSource2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addSources(testSource1)
                .addSources(testSource2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.containsInAnyOrder(testSource1, testSource2));


    }


    @Test
    public void test_useProcessors() {

        Class<? extends Processor> testProcessor1 = SimpleTestProcessor1.class;
        Class<? extends Processor> testProcessor2 = SimpleTestProcessor2.class;

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addProcessors(testProcessor1)
                .addProcessors(testProcessor2)
                .addProcessors((Class<? extends Processor>) null);


        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getProcessorTypes(), Matchers.containsInAnyOrder(testProcessor1, testProcessor2));


    }

    @Test
    public void test_useProcessorAndExpectException() {

        Class<? extends Processor> testProcessor1 = SimpleTestProcessor1.class;
        Class<? extends Processor> testProcessor2 = SimpleTestProcessor2.class;

        CompileTestBuilder.CompilationTestBuilder builder = CompileTestBuilder
                .compilationTest()
                .addProcessorWithExpectedException(testProcessor1, IllegalArgumentException.class)
                .addProcessorWithExpectedException(testProcessor2, IllegalStateException.class);


        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getProcessorsWithExpectedExceptions(), Matchers.<CompileTestConfiguration.ProcessorWithExpectedException>hasSize(2));


    }

    @Test
    public void test_useSource_addSingleSource() {

        JavaFileObject javaFileObject = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource(javaFileObject);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.contains(javaFileObject));

    }

    @Test
    public void test_useSource_addSourceTwice_onlySecondSourceShouldBeUsed() {

        JavaFileObject javaFileObject1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject javaFileObject2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource(javaFileObject1)
                .useSource(javaFileObject2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.contains(javaFileObject2));

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useSource_addNullValuedSource() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useSource(null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_addNullValuedProcessor() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useProcessor((Processor) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_addNullValuedUnitTestProcessor() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder
                .unitTest()
                .useProcessor((UnitTestProcessor) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedProcessor() {

        CompileTestBuilder
                .compilationTest()
                .addProcessorWithExpectedException(null, IllegalStateException.class);


    }

    private static class SimpleTestProcessor extends AbstractProcessor {
        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedException() {

        CompileTestBuilder
                .compilationTest()
                .addProcessorWithExpectedException(SimpleTestProcessor.class, null);


    }


}
