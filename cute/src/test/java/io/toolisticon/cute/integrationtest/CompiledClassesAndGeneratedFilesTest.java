package io.toolisticon.cute.integrationtest;

import io.toolisticon.cute.Constants;
import io.toolisticon.cute.Cute;
import io.toolisticon.cute.JavaFileObjectUtils;
import io.toolisticon.cute.TestUtilities;
import io.toolisticon.cute.common.SimpleTestProcessor1;
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

        Cute.blackBoxTest()
                .given().processor(SimpleTestProcessor1.class)
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .whenCompiled()
                .thenExpectThat().compilationSucceeds()
                .andThat().javaFileObject(StandardLocation.CLASS_OUTPUT, "io.toolisticon.cute.integrationtest.CompiledClassesAndGeneratedFilesExistTestcase", JavaFileObject.Kind.CLASS).exists()
                .executeTest();


    }

    @Test
    public void testCompiledClassesExist_withProcessorCollection() {

        Cute.blackBoxTest()
                .given().processor(SimpleTestProcessor1.class)
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .whenCompiled()
                .thenExpectThat().compilationSucceeds()
                .andThat().javaFileObject(StandardLocation.CLASS_OUTPUT, "io.toolisticon.cute.integrationtest.CompiledClassesAndGeneratedFilesExistTestcase", JavaFileObject.Kind.CLASS).exists()
                .executeTest();


    }

    @Test
    public void testCompiledClassesExist_withSingleProcessor() {

        Cute.blackBoxTest()
                .given().processor(SimpleTestProcessor1.class)
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .whenCompiled()
                .thenExpectThat().compilationSucceeds()
                .andThat().javaFileObject(StandardLocation.CLASS_OUTPUT, "io.toolisticon.cute.integrationtest.CompiledClassesAndGeneratedFilesExistTestcase", JavaFileObject.Kind.CLASS).exists()
                .executeTest();


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
            Cute.blackBoxTest()
                    .given().processor(FileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled()
                    .thenExpectThat().compilationSucceeds()
                    .andThat().fileObject(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt").doesntExist()
                    .executeTest();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_FO_EXISTS_BUT_SHOULD_BE_NON_EXISTENT.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

    @Test
    public void testCompiledResourceExist_byFileObject() {

        Cute.blackBoxTest()
                .given().processor(FileGeneratorProcessor.class)
                .andSourceFiles("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java")
                .whenCompiled()
                .thenExpectThat().compilationSucceeds()
                .andThat().fileObject(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt").equals(JavaFileObjectUtils.readFromString("XXX"))
                .executeTest();


    }

    @Test
    public void testCompiledResourceExist_ByMatcher() {

        Cute.blackBoxTest()
                .given().processor(FileGeneratorProcessor.class)
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .whenCompiled()
                .thenExpectThat().compilationSucceeds()
                .andThat().fileObject(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt").matches(fileObject -> true)
                .executeTest();


    }

    @Test
    public void testCompiledResourceNotExistButShould_byFileObject() {

        boolean assertionErrorWasThrown = false;

        try {
            Cute.blackBoxTest()
                    .given().processor(FileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled()
                    .thenExpectThat().compilationSucceeds()
                    .andThat().fileObject(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt").equals(JavaFileObjectUtils.readFromString("XXX!!!"))
                    .executeTest();
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
            Cute.blackBoxTest()
                    .given().processor(FileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled().thenExpectThat().compilationSucceeds()
                    .andThat().fileObject(StandardLocation.SOURCE_OUTPUT, "/META-INF", "jupp.txt").matches(fileObject -> false)
                    .executeTest();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_MATCHER.getMessagePattern());
            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }


    public static class JavaFileGeneratorProcessor extends SimpleTestProcessor1 {

        public final static String PACKAGE_NAME = "io.toolisticon.cute.integrationtest";
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

        Cute.blackBoxTest()
                .given().processor(JavaFileGeneratorProcessor.class)
                .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                .whenCompiled().thenExpectThat().compilationSucceeds()
                .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.SOURCE).exists()
                .andThat().javaFileObject(StandardLocation.CLASS_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.CLASS).exists()
                .executeTest();


    }


    @Test
    public void testCompiledJavaFileObjectNotExistButShould_byJavaFileObject() {

        boolean assertionErrorWasThrown = false;

        try {
            Cute.blackBoxTest()
                    .given().processor(JavaFileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled().thenExpectThat().compilationSucceeds()
                    .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + ".Murks", JavaFileObject.Kind.SOURCE).exists()
                    .executeTest();
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
            Cute.blackBoxTest()
                    .given().processor(JavaFileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled()
                    .thenExpectThat().compilationSucceeds()
                    .andThat().generatedSourceFile(JavaFileGeneratorProcessor.PACKAGE_NAME + ".Murks").exists()
                    .executeTest();
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
            Cute.blackBoxTest()
                    .given().processor(JavaFileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled()
                    .thenExpectThat().compilationSucceeds()
                    .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.SOURCE).doesntExist()
                    .executeTest();
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
            Cute.blackBoxTest()
                    .given().processor(JavaFileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled()
                    .thenExpectThat().compilationSucceeds()
                    .andThat().generatedSourceFile(JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME).doesntExist()
                    .executeTest();
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
            Cute.blackBoxTest()
                    .given().processor(JavaFileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled()
                    .thenExpectThat().compilationSucceeds()
                    .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.SOURCE).equals(JavaFileObjectUtils.readFromString("XXX!!"))
                    .executeTest();
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
            Cute.blackBoxTest()
                    .given().processor(JavaFileGeneratorProcessor.class)
                    .andSourceFiles(JavaFileObjectUtils.readFromResource("/integrationtest/CompiledClassesAndGeneratedFilesExistTestcase.java"))
                    .whenCompiled().thenExpectThat().compilationSucceeds()
                    .andThat().javaFileObject(StandardLocation.SOURCE_OUTPUT, JavaFileGeneratorProcessor.PACKAGE_NAME + "." + JavaFileGeneratorProcessor.CLASS_NAME, JavaFileObject.Kind.SOURCE).matches(fileObject -> false)
                    .executeTest();
        } catch (AssertionError e) {

            TestUtilities.assertAssertionMessageContainsMessageTokensAssertion(e, Constants.Messages.MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_MATCHER.getMessagePattern());

            assertionErrorWasThrown = true;

        }

        MatcherAssert.assertThat("AssertError should have  been thrown", assertionErrorWasThrown);

    }

}
