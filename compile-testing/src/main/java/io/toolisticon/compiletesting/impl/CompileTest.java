package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.InvalidTestConfigurationException;
import io.toolisticon.compiletesting.extension.api.AssertionSpiServiceLocator;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
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

        // Check generated java source files
        for (JavaFileObject javaFileObject :
                this.compileTestConfiguration.getExpectedGeneratedJavaFileObjectsCheck()) {

            if (!compilationResult.getCompileTestFileManager().containsGeneratedJavaFileObject(javaFileObject)) {
                AssertionSpiServiceLocator.locate().fail("Expected generated JavaFileObject can't be found\n" + compilationResult.getCompileTestFileManager().getGeneratedFileOverview());
            }

        }

        for (FileObject fileObject :
                this.compileTestConfiguration.getExpectedGeneratedFileObjectsCheck()) {

            if (!compilationResult.getCompileTestFileManager().containsGeneratedFileObject(fileObject)) {
                AssertionSpiServiceLocator.locate().fail("Expected generated FileObject can't be found\n" + compilationResult.getCompileTestFileManager().getGeneratedFileOverview());
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


}
