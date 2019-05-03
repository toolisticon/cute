package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.impl.java9.ModuleFinderWrapper;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Random;
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

        if (compileTestConfiguration.getModules() != null) {
            stringBuilder.append(getDebugOutputHeader("MODULE PATH"));

            int i = 0;
            for (File file : CompileTestUtilities.getJarsFromClasspath()) {
                stringBuilder.append("[")
                        .append(i++)
                        .append("|")
                        .append(ModuleFinderWrapper.getModuleForJarFile(file))
                        .append("] := '")
                        .append(file.getAbsolutePath())
                        .append("'\n");
            }

        }


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





    private static <FILE_OBJECT extends FileObject> String createGeneratedFileObjectOverview(List<FILE_OBJECT> fileObjects) {

        final String prefix = "target/compileTesting_failingUnitTests/" + CommonUtilities.getRandomString(10);

        StringBuilder stringBuilder = new StringBuilder();

        if (fileObjects.isEmpty()) {
            stringBuilder.append("'No files were generated !!!'");
        } else {

            stringBuilder.append("[\n");

            for (FILE_OBJECT fileObject : fileObjects) {

                stringBuilder.append("    '" + fileObject.toUri().toString() + "'").append(" := '").append(writeFile(prefix, fileObject)).append("', \n");

            }

            stringBuilder.append("  ]");
        }


        return stringBuilder.toString();

    }

    private static String writeFile(String pathPrefix, FileObject fileObject) {

        File outputFile = new File(pathPrefix + fileObject.toUri().getPath());
        outputFile.getParentFile().mkdirs();

        FileOutputStream fos = null;
        InputStream is = null;

        try {
            // create the output file
            outputFile.createNewFile();

            // open input and output stream for file copy
            fos = new FileOutputStream(outputFile);
            is = fileObject.openInputStream();

            byte[] buffer = new byte[20000];

            int ch = is.read(buffer);
            while (-1 != ch) {

                fos.write(buffer, 0, ch);
                ch = is.read(buffer);

            }

            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }

            if (fos != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }

        return outputFile.getAbsolutePath();


    }


}
