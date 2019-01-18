package io.toolisticon.compiletesting.impl;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * Utility class to generate debug output
 */
final class DebugOutputGenerator {

    /**
     * Hidden constructor.
     */
    private DebugOutputGenerator() {

    }

    static String getDebugOutput(CompilationResult compilationResult, CompileTestConfiguration compileTestConfiguration, FailingAssertionException failingAssertionException) {

        StringBuilder stringBuilder = new StringBuilder();

        // unexpected exception
        if (failingAssertionException != null && failingAssertionException.getCause() != null) {

            StringWriter stacktraceStringWriter = new StringWriter();
            PrintWriter stacktracePrintWriter = new PrintWriter(stacktraceStringWriter);
            failingAssertionException.getCause().printStackTrace(stacktracePrintWriter);

            String message = failingAssertionException.getCause().getMessage();

            stringBuilder.append(getDebugOutputHeader("UNEXPECTED EXCEPTION"))
                    .append("MESSAGE : ").append(message != null ? message : "<NO MESSSAGE>").append("\n")
                    .append("STACKTRACE : ").append(stacktraceStringWriter.toString()).append("\n");
        }

        if (compilationResult != null) {
            // Error and warning messages
            stringBuilder.append(getDebugMessages(compilationResult, Diagnostic.Kind.ERROR));
            stringBuilder.append(getDebugMessages(compilationResult, Diagnostic.Kind.MANDATORY_WARNING));
            stringBuilder.append(getDebugMessages(compilationResult, Diagnostic.Kind.WARNING));


            // Generated File objects
            stringBuilder.append(getDebugOutputHeader("GENERATED FILEOBJECTS")).append(getGeneratedFileOverview(compilationResult));

        }

        // Compile test configuration
        stringBuilder.append(getDebugOutputHeader("COMPILE TEST CONFIGURATION")).append(compileTestConfiguration.toString());

        return stringBuilder.toString();

    }

    private static String getDebugOutputHeader(String headerString) {
        return String.format("\n-------------------------------\n-- %s: \n-------------------------------\n", headerString.toUpperCase());
    }

    private static String getDebugMessages(CompilationResult compilationResult, Diagnostic.Kind kind) {
        StringBuilder stringBuilder = new StringBuilder();

        Set<Diagnostic> filteredDiagnostics = CompileTestUtilities.getDiagnosticByKind(compilationResult.getDiagnostics(), kind);
        if (!filteredDiagnostics.isEmpty()) {
            stringBuilder.append(getDebugOutputHeader(kind.toString() + " MESSAGES"));

            int i = 1;
            for (Diagnostic diagnostics : filteredDiagnostics) {


                stringBuilder.append("[").append(i).append("]'")
                        .append(diagnostics)
                        .append("'\n");
                i++;
            }

        }

        return stringBuilder.toString();
    }

    private static String getGeneratedFileOverview(CompilationResult compilationResult) {

        StringBuilder stringBuilder = new StringBuilder();


        stringBuilder
                .append("{\n")
                .append("  'GENERATED JAVA FILE OBJECTS' : ")
                .append(createGeneratedFileObjectOverview(compilationResult.getCompileTestFileManager().getGeneratedJavaFileObjects()))
                .append(",\n  'GENERATED FILE OBJECTS' :")
                .append(createGeneratedFileObjectOverview(compilationResult.getCompileTestFileManager().getGeneratedFileObjects()))
                .append("\n}");


        return stringBuilder.toString();

    }

    private static  <FILE_OBJECT extends FileObject> String createGeneratedFileObjectOverview(List<FILE_OBJECT> fileObjects) {

        StringBuilder stringBuilder = new StringBuilder();

        if (fileObjects.isEmpty()) {
            stringBuilder.append("'No files were generated !!!'");
        } else {

            stringBuilder.append("[\n");

            for (FILE_OBJECT fileObject : fileObjects) {

                stringBuilder.append("    '" + fileObject.toUri().toString() + "'").append(", \n");

            }

            stringBuilder.append("  ]");
        }


        return stringBuilder.toString();

    }

}
