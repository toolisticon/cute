package io.toolisticon.compiletesting.integrationtest;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import io.toolisticon.compiletesting.common.SimpleTestProcessor1;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Integration test to test if expected compiled classes and generated files exists.
 */
public class CompiledClassesAndGeneratedFilesTest {


    @Test
    public void testCompiledClassesExist() {

        CompileTestBuilder.compilationTest()
                .addProcessors(SimpleTestProcessor1.class)
                .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .compilationShouldSucceed()
                .expectedJavaFileObjectExists(StandardLocation.CLASS_OUTPUT, "io.toolisticon.compiletesting.integrationtest.CompiledClassesAndGeneratedFilesExistTestcase", JavaFileObject.Kind.CLASS)
                .testCompilation();


    }


    public static class FileGeneratorProcessor extends SimpleTestProcessor1 {


        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

            if (roundEnv.processingOver()) {
                try {

                    FileObject out = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "/META-INF/jupp.txt");


                    BufferedWriter writer = new BufferedWriter(out.openWriter());

                    writer.write("XXX");

                    writer.flush();
                    writer.close();

                } catch (IOException e) {
                    throw new RuntimeException("ERROR! " + e.getMessage(), e);
                }

            }

            return false;
        }


    }

    @Test
    public void testCompiledResourceExist() {

        CompileTestBuilder.compilationTest()
                .addProcessors(FileGeneratorProcessor.class)
                .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .compilationShouldSucceed()
                .expectedFileObjectExists(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt", JavaFileObjectUtils.readFromString( "XXX"))
                .testCompilation();


    }

    @Test
    public void testCompiledResourceNotExist() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(FileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectedFileObjectExists(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt", JavaFileObjectUtils.readFromString("XXX!!!"))
                    .testCompilation();
        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("exists but doesn't match expected"));
            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }


    public static class JavaFileGeneratorProcessor extends SimpleTestProcessor1 {

        public final static String PACKAGE_NAME = "io.toolisticon.compiletesting.integrationtest";
        public final static String CLASS_NAME = "CreatedSourceFile";


        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

            if (roundEnv.processingOver()) {
                try {

                    FileObject out = processingEnv.getFiler().createSourceFile(PACKAGE_NAME + "." + CLASS_NAME);


                    BufferedWriter writer = new BufferedWriter(out.openWriter());

                    writer.write("package " + PACKAGE_NAME + ";\n");
                    writer.write("public class " + CLASS_NAME + "{\n}");

                    writer.flush();
                    writer.close();

                } catch (IOException e) {
                    throw new RuntimeException("ERROR! " + e.getMessage(), e);
                }

            }

            return false;
        }


    }

    @Test
    public void testCompiledJavaFileObjectExist() {

        CompileTestBuilder.compilationTest()
                .addProcessors(JavaFileGeneratorProcessor.class)
                .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .compilationShouldSucceed()
                .expectedJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.SOURCE)
                .expectedJavaFileObjectExists(StandardLocation.CLASS_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.CLASS)

                .testCompilation();


    }

    @Test
    public void testCompiledJavaFileObjectNotExist() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(FileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectedJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + ".Murks", JavaFileObject.Kind.SOURCE)
                    .testCompilation();
        } catch (AssertionError e) {

            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("doesn't exist"));
            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

}
