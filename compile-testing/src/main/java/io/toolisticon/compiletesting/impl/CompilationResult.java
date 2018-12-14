package io.toolisticon.compiletesting.impl;

import javax.tools.DiagnosticCollector;

/**
 * Compilation result.
 *
 * Allows access to DiagnosticCollector and FileManager used during compilation test.
 *
 */
public class CompilationResult {

    /** Compilation succeeded? */
    private final Boolean compilationSucceeded;
    /** Diagnostics instance - allows checks for specific compilation messages */
    private final DiagnosticCollector diagnostics;
    /** The file manager used during the compilation - allows comparing of generated files */
    private final CompileTestFileManager compileTestFileManager;

    /**
     * Constructor.
     *
     * @param compilationSucceeded should compilation succeed
     * @param diagnostics the DiagnosticsController instance to use
     * @param compileTestFileManager the file manager used during compilation
     */
    public CompilationResult(Boolean compilationSucceeded,
                             DiagnosticCollector diagnostics,
                             CompileTestFileManager compileTestFileManager) {

        this.compilationSucceeded = compilationSucceeded;
        this.diagnostics = diagnostics;
        this.compileTestFileManager = compileTestFileManager;

    }

    public Boolean getCompilationSucceeded() {
        return compilationSucceeded;
    }

    public DiagnosticCollector getDiagnostics() {
        return diagnostics;
    }

    public CompileTestFileManager getCompileTestFileManager() {
        return compileTestFileManager;
    }
}
