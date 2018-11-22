package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.InvalidTestConfigurationException;
import io.toolisticon.compiletesting.extension.api.AssertionSpiServiceLocator;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a compile test.
 */
public class CompileTest {

    private final CompileTestConfiguration compileTestConfiguration;

    /**
     * Main constructor.
     *
     * @param compileTestConfiguration the configuration used during tests.
     */
    public CompileTest(final CompileTestConfiguration compileTestConfiguration) {
        this.compileTestConfiguration = compileTestConfiguration;
    }

    /**
     * The compile test execution main method.
     */
    public void executeTest() {


        // Do tests now
        CompilationResult compilationResult = compile(compileTestConfiguration);

        // Check if all processors have been applied
        checkIfProcessorsHaveBeenApplied(compilationResult.getDiagnostics());

        // check if error messages and shouldSucceed aren't set contradictionary
        if (compileTestConfiguration.getCompilationShouldSucceed() != null
                && compileTestConfiguration.getCompilationShouldSucceed()
                && compileTestConfiguration.getErrorMessageCheck().size() > 0) {
            throw new InvalidTestConfigurationException("Test configuration error : Compilation should succeed but error messages is expected too !!!");
        }


        // Check if compilation succeeded
        if (compileTestConfiguration.getCompilationShouldSucceed() != null && !compileTestConfiguration.getCompilationShouldSucceed().equals(compilationResult.getCompilationSucceeded())) {

            AssertionSpiServiceLocator.locate().fail(compileTestConfiguration.getCompilationShouldSucceed() ? "Compilation should have succeeded but failed" : "Compilation should have failed but succeeded");

        }


        // Check messages
        checkMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.ERROR, compileTestConfiguration.getErrorMessageCheck());
        checkMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.WARNING, compileTestConfiguration.getWarningMessageCheck());
        checkMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.MANDATORY_WARNING, compileTestConfiguration.getMandatoryWarningMessageCheck());
        checkMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.NOTE, compileTestConfiguration.getNoteMessageCheck());


        for (CompileTestConfiguration.GeneratedJavaFileObjectCheck generatedJavaFileObjectCheck : this.compileTestConfiguration.getGeneratedJavaFileObjectChecks()) {

            if (!compilationResult.getCompileTestFileManager().existsExpectedJavaFileObject(generatedJavaFileObjectCheck.getLocation(), generatedJavaFileObjectCheck.getClassName(), generatedJavaFileObjectCheck.getKind())) {
                AssertionSpiServiceLocator.locate().fail("Generated JavaFileObject (" + generatedJavaFileObjectCheck.getLocation() + "; " + generatedJavaFileObjectCheck.getClassName() + "; " + generatedJavaFileObjectCheck.getKind() + ") doesn't exist.\n" + getDebugOutput(compilationResult, compileTestConfiguration));
            } else {

                try {

                    JavaFileObject foundJavaFileObject = compilationResult.getCompileTestFileManager().getJavaFileForInput(generatedJavaFileObjectCheck.getLocation(), generatedJavaFileObjectCheck.getClassName(), generatedJavaFileObjectCheck.getKind());

                    // check for equality
                    if (generatedJavaFileObjectCheck.getExpectedJavaFileObject() != null) {

                        if (!CompileTestFileManager.contentEquals(
                                foundJavaFileObject.openInputStream(),
                                generatedJavaFileObjectCheck.getExpectedJavaFileObject().openInputStream())) {

                            AssertionSpiServiceLocator.locate().fail("Generated JavaFileObject (" + generatedJavaFileObjectCheck.getLocation() + "; " + generatedJavaFileObjectCheck.getClassName() + "; " + generatedJavaFileObjectCheck.getKind() + ") exists but doesn't match expected JavaFileObject.\n" + getDebugOutput(compilationResult, compileTestConfiguration));

                        }

                    }

                    // check with passed matcher
                    if (generatedJavaFileObjectCheck.getGeneratedFileObjectMatcher() != null) {

                        if (!generatedJavaFileObjectCheck.getGeneratedFileObjectMatcher().check(foundJavaFileObject)) {
                            AssertionSpiServiceLocator.locate().fail("Generated JavaFileObject (" + generatedJavaFileObjectCheck.getLocation() + "; " + generatedJavaFileObjectCheck.getClassName() + "; " + generatedJavaFileObjectCheck.getKind() + ") exists but doesn't match passed GeneratedFileObjectMatcher.\n" + getDebugOutput(compilationResult, compileTestConfiguration));
                        }

                    }

                } catch (IOException e) {
                    // ignore
                }


            }


        }

        for (CompileTestConfiguration.GeneratedFileObjectCheck generatedFileObjectCheck : this.compileTestConfiguration.getGeneratedFileObjectChecks()) {

            if (!compilationResult.getCompileTestFileManager().existsExpectedFileObject(generatedFileObjectCheck.getLocation(), generatedFileObjectCheck.getPackageName(), generatedFileObjectCheck.getRelativeName())) {
                AssertionSpiServiceLocator.locate().fail("Generated FileObject (" + generatedFileObjectCheck.getLocation() + "; " + generatedFileObjectCheck.getPackageName() + "; " + generatedFileObjectCheck.getRelativeName() + ") doesn't exist.\n" + getDebugOutput(compilationResult, compileTestConfiguration));
            } else {

                try {

                    FileObject foundFileObject = compilationResult.getCompileTestFileManager().getFileForInput(generatedFileObjectCheck.getLocation(), generatedFileObjectCheck.getPackageName(), generatedFileObjectCheck.getRelativeName());

                    // check for equality
                    if (generatedFileObjectCheck.getExpectedFileObject() != null) {

                        if (!CompileTestFileManager.contentEquals(
                                foundFileObject.openInputStream(),
                                generatedFileObjectCheck.getExpectedFileObject().openInputStream())) {

                            AssertionSpiServiceLocator.locate().fail("Generated FileObject (" + generatedFileObjectCheck.getLocation() + "; " + generatedFileObjectCheck.getPackageName() + "; " + generatedFileObjectCheck.getRelativeName() + ") exists but doesn't match expected FileObject.\n" + getDebugOutput(compilationResult, compileTestConfiguration));

                        }

                    }

                    // check with passed matcher
                    if (generatedFileObjectCheck.getGeneratedFileObjectMatcher() != null) {

                        if (!generatedFileObjectCheck.getGeneratedFileObjectMatcher().check(foundFileObject)) {
                            AssertionSpiServiceLocator.locate().fail("Generated FileObject (" + generatedFileObjectCheck.getLocation() + "; " + generatedFileObjectCheck.getPackageName() + "; " + generatedFileObjectCheck.getRelativeName() + ") exists but doesn't match passed GeneratedFileObjectMatcher.\n" + getDebugOutput(compilationResult, compileTestConfiguration));
                        }

                    }

                } catch (IOException e) {
                    // ignore
                }


            }


        }

    }

    /**
     * Init the compilation and compile.
     *
     * @param compileTestConfiguration the compile test configuration to use
     * @return the compilation result
     */
    public static CompilationResult compile(CompileTestConfiguration compileTestConfiguration) {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        CompileTestFileManager javaFileManager = new CompileTestFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, javaFileManager, diagnostics, null, null, compileTestConfiguration.getSourceFiles());

        compilationTask.setProcessors(compileTestConfiguration.getWrappedProcessors());
        Boolean compilationSucceeded = compilationTask.call();

        return new CompilationResult(compilationSucceeded, diagnostics, javaFileManager);
    }

    /**
     * Allows checking if annotation processor has been applied during the compilation test.
     *
     * @param diagnostics
     */
    protected void checkIfProcessorsHaveBeenApplied(DiagnosticCollector<JavaFileObject> diagnostics) {

        Set<String> messages = getMessages(diagnostics, Diagnostic.Kind.NOTE);

        outer:
        for (AnnotationProcessorWrapper processor : compileTestConfiguration.getWrappedProcessors()) {

            for (String message : messages) {
                if (message.equals(processor.getProcessorWasAppliedMessage())) {
                    continue outer;
                }
            }

            AssertionSpiServiceLocator.locate().fail("Annotation processor " + processor.getWrappedProcessor().getClass().getCanonicalName() + " hasn't been applied on a class");

        }

    }


    /**
     * Method to check for specific messages.
     *
     * @param diagnostics      the compilations diagnostics result
     * @param kind             the kind of the messages to check
     * @param messsagesToCheck a set containing the messages to check
     */
    protected static void checkMessages(DiagnosticCollector<JavaFileObject> diagnostics, Diagnostic.Kind kind, Set<String> messsagesToCheck) {

        Set<String> messages = getMessages(diagnostics, kind);

        outer:
        for (String messageToCheck : messsagesToCheck) {

            for (String message : messages) {

                if (message.contains(messageToCheck)) {
                    continue outer;
                }

            }

            // Not found ==> assertion fails
            AssertionSpiServiceLocator.locate().fail("Haven't found expected message string '" + messageToCheck + "' of kind " + kind.name() + ". Got messages " + messages.toString());

        }

    }

    /**
     * Gets all messages of a specific kind.
     *
     * @param diagnostics the compilations diagnostics result
     * @param kind        the kind of the messages to return
     * @return
     */
    protected static Set<String> getMessages(DiagnosticCollector<JavaFileObject> diagnostics, Diagnostic.Kind kind) {

        Set<String> messages = new HashSet<String>();

        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
            if (kind == diagnostic.getKind()) {
                messages.add(diagnostic.getMessage(null));
            }
        }

        return messages;

    }

    private static String getDebugOutput(CompilationResult compilationResult, CompileTestConfiguration compileTestConfiguration) {
        return "\n-------------------------------\n-- GENERATED FILEOBJECTS: \n-------------------------------\n" + compilationResult.getCompileTestFileManager().getGeneratedFileOverview() + "\n-------------------------------\n-- COMPILE TEST CONFIGURATION: \n-------------------------------\n" + compileTestConfiguration.toString();
    }

}
