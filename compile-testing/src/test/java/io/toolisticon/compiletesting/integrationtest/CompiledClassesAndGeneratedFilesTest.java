package io.toolisticon.compiletesting.integrationtest;

import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.Constants;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import io.toolisticon.compiletesting.TestUtilities;
import io.toolisticon.compiletesting.common.SimpleTestProcessor1;
import io.toolisticon.compiletesting.impl.CompileTest;
import org.hamcrest.MatcherAssert;
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
    public void testCompiledResourceExistButShouldnt() {


        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(FileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectFileObjectNotToExist(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt")
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_FO_EXISTS_BUT_SHOULD_BE_NON_EXISTENT.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

    @Test
    public void testCompiledResourceExist_byFileObject() {

        CompileTestBuilder.compilationTest()
                .addProcessors(FileGeneratorProcessor.class)
                .addSources("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java")
                .compilationShouldSucceed()
                .expectedFileObjectExists(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt", JavaFileObjectUtils.readFromString("XXX"))
                .testCompilation();


    }

    @Test
    public void testCompiledResourceExist_ByMatcher() {

        CompileTestBuilder.compilationTest()
                .addProcessors(FileGeneratorProcessor.class)
                .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .compilationShouldSucceed()
                .expectedFileObjectExists(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt", new GeneratedFileObjectMatcher<FileObject>() {
                    @Override
                    public boolean check(FileObject fileObject) throws IOException {
                        return true;
                    }
                })
                .testCompilation();


    }

    @Test
    public void testCompiledResourceNotExistButShould_byFileObject() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(FileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectedFileObjectExists(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt", JavaFileObjectUtils.readFromString("XXX!!!"))
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_MATCHER.getMessagePattern());
            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

    @Test
    public void testCompiledResourceNotExistButShould_byMatcher() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(FileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectedFileObjectExists(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt", new GeneratedFileObjectMatcher<FileObject>() {
                        @Override
                        public boolean check(FileObject fileObject) throws IOException {
                            return false;
                        }
                    })
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_MATCHER.getMessagePattern());
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
    public void testCompiledJavaFileObjectNotExistButShould_byJavaFileObject() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(JavaFileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectedJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + ".Murks", JavaFileObject.Kind.SOURCE)
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_JFO_DOESNT_EXIST.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

    @Test
    public void testCompiledJavaFileObjectNotExistButShould_bySource() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(JavaFileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectedGeneratedSourceFileExists(JavaFileGeneratorProcessor.PACKAGE_NAME + ".Murks")
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_JFO_DOESNT_EXIST.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

    @Test
    public void testCompiledJavaFileObjectExistButShouldnt_byJavaFileObject() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(JavaFileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectJavaFileObjectNotToExist(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.SOURCE)
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_JFO_EXISTS_BUT_SHOULD_BE_NON_EXISTENT.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

    @Test
    public void testCompiledJavaFileObjectExistButShouldnt_bySource() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(JavaFileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectGeneratedSourceFileNotToExist(JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME)
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_JFO_EXISTS_BUT_SHOULD_BE_NON_EXISTENT.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

    @Test
    public void testCompiledJavaFileObjectNotExist_byJavaFileObjectComparision() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(JavaFileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectedJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.SOURCE, JavaFileObjectUtils.readFromString("XXX!!"))
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_MATCHER.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

    @Test
    public void testCompiledJavaFileObjectNotExist_byMatcher() {

        boolean assertionErrorWasThrown = false;

        try {
            CompileTestBuilder.compilationTest()
                    .addProcessors(JavaFileGeneratorProcessor.class)
                    .addSources(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .compilationShouldSucceed()
                    .expectedJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.SOURCE, new GeneratedFileObjectMatcher<JavaFileObject>() {
                        @Override
                        public boolean check(JavaFileObject fileObject) throws IOException {
                            return false;
                        }
                    })
                    .testCompilation();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_MATCHER.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

}
