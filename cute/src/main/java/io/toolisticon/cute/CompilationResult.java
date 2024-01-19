package io.toolisticon.cute;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * Compilation result.
 * <p>
 * Allows access to DiagnosticCollector and FileManager used during compilation test.
 */
class CompilationResult {

    /**
     * Compilation succeeded?
     */
    private final Boolean compilationSucceeded;
    /**
     * Diagnostics instance - allows checks for specific compilation messages
     */
    private final DiagnosticCollector<JavaFileObject> diagnostics;
    /**
     * The file manager used during the compilation - allows comparing of generated files
     */
    private final CompileTestFileManager compileTestFileManager;

    /**
     * Constructor.
     *
     * @param compilationSucceeded   should compilation succeed
     * @param diagnostics            the DiagnosticsController instance to use
     * @param compileTestFileManager the file manager used during compilation
     */
    CompilationResult(Boolean compilationSucceeded,
                      DiagnosticCollector<JavaFileObject> diagnostics,
                      CompileTestFileManager compileTestFileManager) {

        this.compilationSucceeded = compilationSucceeded;
        this.diagnostics = diagnostics;
        this.compileTestFileManager = compileTestFileManager;

    }

    Boolean getCompilationSucceeded() {
        return compilationSucceeded;
    }

    DiagnosticCollector<JavaFileObject> getDiagnostics() {
        return diagnostics;
    }

    CompileTestFileManager getCompileTestFileManager() {
        return compileTestFileManager;
    }
}
