package io.toolisticon.compiletesting.impl;

import javax.tools.DiagnosticCollector;

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
    private final DiagnosticCollector diagnostics;
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
                      DiagnosticCollector diagnostics,
                      CompileTestFileManager compileTestFileManager) {

        this.compilationSucceeded = compilationSucceeded;
        this.diagnostics = diagnostics;
        this.compileTestFileManager = compileTestFileManager;

    }

    Boolean getCompilationSucceeded() {
        return compilationSucceeded;
    }

    DiagnosticCollector getDiagnostics() {
        return diagnostics;
    }

    CompileTestFileManager getCompileTestFileManager() {
        return compileTestFileManager;
    }
}
