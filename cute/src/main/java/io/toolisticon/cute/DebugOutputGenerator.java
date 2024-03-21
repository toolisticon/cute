package io.toolisticon.cute;

import io.toolisticon.cute.extension.api.ModuleSupportSpi;
import io.toolisticon.cute.extension.api.ModuleSupportSpiServiceLocator;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    static String getDebugOutput(CompilationResult compilationResult, CuteApi.CompilerTestBB compileTestConfiguration, FailingAssertionException failingAssertionException) {

        StringBuilder stringBuilder = new StringBuilder();

        // unexpected exception
        if (failingAssertionException != null && failingAssertionException.getCause() != null) {

            StringWriter stacktraceStringWriter = new StringWriter();
            PrintWriter stacktracePrintWriter = new PrintWriter(stacktraceStringWriter);
            failingAssertionException.getCause().printStackTrace(stacktracePrintWriter);

            String message = failingAssertionException.getCause().getMessage();

            stringBuilder.append(getDebugOutputHeader("UNEXPECTED EXCEPTION"))
                    .append("MESSAGE : ").append(message != null ? message : "<NO MESSSAGE>").append("\n")
                    .append("STACKTRACE : ").append(stacktraceStringWriter).append("\n");
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

        if (!Java9SupportCheck.UNSUPPORTED_JAVA_VERSION && compileTestConfiguration.modules() != null) {

            ModuleSupportSpi moduleSupportSpi = ModuleSupportSpiServiceLocator.locate();
            if (moduleSupportSpi != null) {
                stringBuilder.append(getDebugOutputHeader("MODULE PATH"));
                moduleSupportSpi.writeModuleDebugOutput(stringBuilder);
            }

        }


        return stringBuilder.toString();

    }

    private static String getDebugOutputHeader(String headerString) {
        return String.format("\n-------------------------------\n-- %s: \n-------------------------------\n", headerString.toUpperCase());
    }

    /**
     * Used to determine the build folder.
     * <p>
     * Maybe "target" for Maven builds and a folder containing "build" in its name for gradle.
     * Defaults to "target".
     *
     * @return the build folder name
     */
    private static String determineBuildFolder() {

        if (new File("target").isDirectory()) {
            return "target";
        } else if (new File("build").isDirectory()) {
            return "build";
        } else {
            // a folder containing build in its name
            File[] possibleBuildDirectories = new File(".").listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && pathname.getName().contains("build");
                }
            });

            if (possibleBuildDirectories.length > 0) {
                return possibleBuildDirectories[0].getName();
            }
        }

        // default => back to target
        return "target";

    }


    private static String getDebugMessages(CompilationResult compilationResult, Diagnostic.Kind kind) {
        StringBuilder stringBuilder = new StringBuilder();

        Set<Diagnostic<? extends JavaFileObject>> filteredDiagnostics = CompileTestUtilities.getDiagnosticByKind(compilationResult.getDiagnostics(), kind);
        if (!filteredDiagnostics.isEmpty()) {
            stringBuilder.append(getDebugOutputHeader(kind.toString() + " MESSAGES"));

            int i = 1;
            for (Diagnostic<? extends JavaFileObject> diagnostics : filteredDiagnostics) {


                stringBuilder.append("[").append(i)
                        //.append(diagnostics.getSource() != null ? "|s:'" + ((FileObject)diagnostics.getSource()).getName() + "'" : "")
                        //.append(diagnostics.getLineNumber() != Diagnostic.NOPOS ? "|l:'" + diagnostics.getLineNumber() + "'" : "")
                        //.append(diagnostics.getColumnNumber() != Diagnostic.NOPOS ? "|c:'" + diagnostics.getColumnNumber() + "'" : "")
                        .append("]'")
                        .append(diagnostics)
                        .append("'\n");
                i++;
            }

        }

        return stringBuilder.toString();
    }

    private static String getGeneratedFileOverview(CompilationResult compilationResult) {

       return "{\n" +
                "  'GENERATED JAVA FILE OBJECTS' : " +
                createGeneratedFileObjectOverview(compilationResult.getCompileTestFileManager().getGeneratedJavaFileObjects()) +
                ",\n  'GENERATED FILE OBJECTS' :" +
                createGeneratedFileObjectOverview(compilationResult.getCompileTestFileManager().getGeneratedFileObjects()) +
                "\n}";

    }


    private static <FILE_OBJECT extends FileObject> String createGeneratedFileObjectOverview(List<FILE_OBJECT> fileObjects) {

        final String prefix = determineBuildFolder() + "/cute_failingUnitTests/" + CommonUtilities.getRandomString(10);

        StringBuilder stringBuilder = new StringBuilder();

        if (fileObjects.isEmpty()) {
            stringBuilder.append("'No files were generated !!!'");
        } else {

            stringBuilder.append("[\n");

            for (FILE_OBJECT fileObject : fileObjects) {

                stringBuilder.append("    '").append(fileObject.toUri().toString()).append("'").append(" := '").append(writeFile(prefix, fileObject)).append("', \n");

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
                    // ignore it
                }
            }

            if (fos != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore it
                }
            }
        }

        return outputFile.getAbsolutePath();


    }


}
