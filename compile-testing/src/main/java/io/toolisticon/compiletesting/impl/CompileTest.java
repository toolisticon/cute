package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.InvalidTestConfigurationException;
import io.toolisticon.compiletesting.extension.api.AssertionSpiServiceLocator;
import io.toolisticon.compiletesting.impl.java9.ModuleFinderWrapper;
import io.toolisticon.compiletesting.impl.java9.StandardJavaFileManagerBridge;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a compile test.
 */
public class CompileTest {

    // Messages
    public final static String MESSAGE_COMPILATION_SHOULD_SUCCEED_AND_ERROR_MESSAGE_EXPECTED = "Test configuration error : Compilation should succeed but error messages is expected too !!!";
    public final static String MESSAGE_COMPILATION_SHOULD_HAVE_SUCCEEDED_BUT_FAILED = "Compilation should have succeeded but failed";
    public final static String MESSAGE_COMPILATION_SHOULD_HAVE_FAILED_BUT_SUCCEEDED = "Compilation should have failed but succeeded";

    public final static String MESSAGE_JFO_DOESNT_EXIST = "Expected generated JavaFileObject (%s) doesn't exist.";
    public final static String MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_FO = "Expected generated JavaFileObject (%s) exists but doesn't match expected JavaFileObject.";
    public final static String MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_MATCHER = "Expected generated JavaFileObject (%s) exists but doesn't match passed GeneratedFileObjectMatcher.";

    public final static String MESSAGE_FO_DOESNT_EXIST = "Expected generated FileObject (%s) doesn't exist.";
    public final static String MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_FO = "Expected generated FileObject (%s) exists but doesn't match expected FileObject.";
    public final static String MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_MATCHER = "Expected generated FileObject (%s) exists but doesn't match passed GeneratedFileObjectMatcher.";

    public final static String MESSAGE_PROCESSOR_HASNT_BEEN_APPLIED = "Annotation processor %s hasn't been applied on a class";
    public final static String MESSAGE_HAVENT_FOUND_MESSSAGE = "Haven't found expected message string '%s' of kind %s. Got messages %s";

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

        CompilationResult compilationResult = null;


        try {

            // Do tests now
            compilationResult = compile(compileTestConfiguration);


            // Check if all processors have been applied
            checkIfProcessorsHaveBeenApplied(compilationResult.getDiagnostics());

            // check if error messages and shouldSucceed aren't set contradictionary
            if (compileTestConfiguration.getCompilationShouldSucceed() != null
                    && compileTestConfiguration.getCompilationShouldSucceed()
                    && compileTestConfiguration.getErrorMessageCheck().size() > 0) {
                throw new InvalidTestConfigurationException(MESSAGE_COMPILATION_SHOULD_SUCCEED_AND_ERROR_MESSAGE_EXPECTED);
            }


            // Check if compilation succeeded
            if (compileTestConfiguration.getCompilationShouldSucceed() != null && !compileTestConfiguration.getCompilationShouldSucceed().equals(compilationResult.getCompilationSucceeded())) {

                throw new FailingAssertionException(compileTestConfiguration.getCompilationShouldSucceed() ? MESSAGE_COMPILATION_SHOULD_HAVE_SUCCEEDED_BUT_FAILED + "\nERRORS:\n" + CompileTestUtilities.getMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.ERROR) : MESSAGE_COMPILATION_SHOULD_HAVE_FAILED_BUT_SUCCEEDED);

            }


            // Check messages
            checkMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.ERROR, compileTestConfiguration.getErrorMessageCheck());
            checkMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.WARNING, compileTestConfiguration.getWarningMessageCheck());
            checkMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.MANDATORY_WARNING, compileTestConfiguration.getMandatoryWarningMessageCheck());
            checkMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.NOTE, compileTestConfiguration.getNoteMessageCheck());


            for (CompileTestConfiguration.GeneratedJavaFileObjectCheck generatedJavaFileObjectCheck : this.compileTestConfiguration.getGeneratedJavaFileObjectChecks()) {

                if (!compilationResult.getCompileTestFileManager().existsExpectedJavaFileObject(generatedJavaFileObjectCheck.getLocation(), generatedJavaFileObjectCheck.getClassName(), generatedJavaFileObjectCheck.getKind())) {
                    throw new FailingAssertionException(String.format(MESSAGE_JFO_DOESNT_EXIST, getJavaFileObjectInfoString(generatedJavaFileObjectCheck)));
                } else {

                    try {

                        JavaFileObject foundJavaFileObject = compilationResult.getCompileTestFileManager().getJavaFileForInput(generatedJavaFileObjectCheck.getLocation(), generatedJavaFileObjectCheck.getClassName(), generatedJavaFileObjectCheck.getKind());

                        // check for equality
                        if (generatedJavaFileObjectCheck.getExpectedJavaFileObject() != null) {

                            if (!CompileTestFileManager.contentEquals(
                                    foundJavaFileObject.openInputStream(),
                                    generatedJavaFileObjectCheck.getExpectedJavaFileObject().openInputStream())) {


                                throw new FailingAssertionException(String.format(MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_FO, getJavaFileObjectInfoString(generatedJavaFileObjectCheck)));

                            }

                        }

                        // check with passed matcher
                        if (generatedJavaFileObjectCheck.getGeneratedFileObjectMatcher() != null) {

                            if (!generatedJavaFileObjectCheck.getGeneratedFileObjectMatcher().check(foundJavaFileObject)) {
                                throw new FailingAssertionException(String.format(MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_MATCHER, getJavaFileObjectInfoString(generatedJavaFileObjectCheck)));
                            }

                        }

                    } catch (IOException e) {
                        // ignore
                    }


                }


            }

            for (CompileTestConfiguration.GeneratedFileObjectCheck generatedFileObjectCheck : this.compileTestConfiguration.getGeneratedFileObjectChecks()) {

                if (!compilationResult.getCompileTestFileManager().existsExpectedFileObject(generatedFileObjectCheck.getLocation(), generatedFileObjectCheck.getPackageName(), generatedFileObjectCheck.getRelativeName())) {
                    throw new FailingAssertionException(String.format(MESSAGE_FO_DOESNT_EXIST, getFileObjectInfoString(generatedFileObjectCheck)));
                } else {

                    try {

                        FileObject foundFileObject = compilationResult.getCompileTestFileManager().getFileForInput(generatedFileObjectCheck.getLocation(), generatedFileObjectCheck.getPackageName(), generatedFileObjectCheck.getRelativeName());

                        // check for equality
                        if (generatedFileObjectCheck.getExpectedFileObject() != null) {

                            if (!CompileTestFileManager.contentEquals(
                                    foundFileObject.openInputStream(),
                                    generatedFileObjectCheck.getExpectedFileObject().openInputStream())) {

                                throw new FailingAssertionException(String.format(MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_FO, getFileObjectInfoString(generatedFileObjectCheck)));

                            }

                        }

                        // check with passed matcher
                        if (generatedFileObjectCheck.getGeneratedFileObjectMatcher() != null) {

                            if (!generatedFileObjectCheck.getGeneratedFileObjectMatcher().check(foundFileObject)) {
                                throw new FailingAssertionException(String.format(MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_MATCHER, getFileObjectInfoString(generatedFileObjectCheck)));
                            }

                        }

                    } catch (IOException e) {
                        // ignore
                    }


                }


            }

        } catch (FailingAssertionException e) {

            // now trigger failing assertion, but also enrich message with debug output
            AssertionSpiServiceLocator.locate().fail(e.getMessage() + "\n" + DebugOutputGenerator.getDebugOutput(compilationResult, compileTestConfiguration, e));

        } catch (RuntimeException e) {

            if (e.getCause() != null && FailingAssertionException.class.isAssignableFrom(e.getCause().getClass())) {
                // now trigger failing assertion, but also enrich message with debug output
                AssertionSpiServiceLocator.locate().fail(e.getCause().getMessage() + "\n" + DebugOutputGenerator.getDebugOutput(compilationResult, compileTestConfiguration, (FailingAssertionException) e.getCause()));
                throw (FailingAssertionException) (e.getCause());
            }

            throw e;


        }

    }


    protected static String getJavaFileObjectInfoString(CompileTestConfiguration.GeneratedJavaFileObjectCheck generatedJavaFileObjectCheck) {
        return generatedJavaFileObjectCheck.getLocation() + "; " + generatedJavaFileObjectCheck.getClassName() + "; " + generatedJavaFileObjectCheck.getKind();
    }

    protected static String getFileObjectInfoString(CompileTestConfiguration.GeneratedFileObjectCheck generatedFileObjectCheck) {
        return generatedFileObjectCheck.getLocation() + "; " + generatedFileObjectCheck.getPackageName() + "; " + generatedFileObjectCheck.getRelativeName();
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

        StandardJavaFileManager stdJavaFileManager = compiler.getStandardFileManager(diagnostics, null, null);


        Map<String, File> moduleToJarMap = new HashMap<String, File>();

        // Set java 9 module path if modules have been set - do it via reflection to be compatible with older java version
        if (compileTestConfiguration.getModules() != null) {
            try {


                List<File> files = CompileTestUtilities.getJarsFromClasspath();

                //StandardJavaFileManagerBridge.setLocation(stdJavaFileManager, (JavaFileManager.Location) StandardLocation.valueOf("MODULE_PATH"), files);



                for (File file : files) {





                    //JavaFileManager.Location modulePathLocation = (JavaFileManager.Location) StandardLocation.valueOf("MODULE_PATH"); //

                    //StandardJavaFileManagerBridge.setLocation(stdJavaFileManager, (JavaFileManager.Location) StandardLocation.valueOf("MODULE_PATH"), files);


                    String moduleName = ModuleFinderWrapper.getModuleForJarFile(file);

                    if (moduleName != null) {
                        moduleToJarMap.put(moduleName, file);
                    }

/*-
                    if (moduleName != null) {
                        JavaFileManager.Location modulePathLocation = StandardJavaFileManagerBridge.getGetLocationForModule(stdJavaFileManager, (JavaFileManager.Location) StandardLocation.valueOf("MODULE_PATH"), moduleName);
                        System.out.println("MODULE_PATH_LOCATION : " + modulePathLocation.toString());
                        //StandardJavaFileManagerBridge.setLocationForModule(stdJavaFileManager, modulePathLocation, moduleName, FileBrigde.toPath(file.getParentFile()));
                        StandardJavaFileManagerBridge.setLocation(stdJavaFileManager, modulePathLocation, Arrays.asList(file));
                    } else {
                        System.out.println("DIDN'T FOUND MODULE NAME FOR : " + file.getAbsolutePath());
                    }
                    */


                }

/*-
                System.out.println("MODULES....");

                for (Set<JavaFileManager.Location> locations : StandardJavaFileManagerBridge.listLocationsForModules​(stdJavaFileManager, (JavaFileManager.Location) StandardLocation.valueOf("MODULE_PATH"))) {

                    for (JavaFileManager.Location location : locations) {

                        System.out.println(location.toString() + " := " + StandardJavaFileManagerBridge.inferModuleName(stdJavaFileManager, location));

                    }

                }
                */

            } catch (Exception e) {
                // ignore => only thrown for java <9
                e.printStackTrace();
            }


        }


        // setLocationForModule​(JavaFileManager.Location location,String moduleName,Collection<? extends Path> paths)
/*-
        if (compileTestConfiguration.getModules() != null) {
            try {
                File file = new File("/Users/tobiasstamann/.m2/repository/org/apache/commons/commons-lang3/3.8.1/commons-lang3-3.8.1.jar");


                // will throw IllegalArgumentException for < Java 9
                Method method = StandardJavaFileManager.class.getMethod("setLocationForModule", JavaFileManager.Location.class, String.class, Collection.class);
                Method toPathMethod = File.class.getMethod("toPath");
                method.invoke(stdJavaFileManager, (JavaFileManager.Location) StandardLocation.valueOf("MODULE_PATH"), "org.apache.commons.lang3", Arrays.asList(toPathMethod.invoke(file)));
            } catch (Exception e) {
                // ignore => only thrown for java <9
                e.printStackTrace();
            }
        }
        */


        CompileTestFileManager javaFileManager = new CompileTestFileManager(stdJavaFileManager);


        JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, javaFileManager, diagnostics, null, null, compileTestConfiguration.getSourceFiles());

        compilationTask.setProcessors(compileTestConfiguration.getWrappedProcessors());

        // handle java 9 modules via reflection to maintain backward compatibility
        if (compileTestConfiguration.getModules() != null) {
            try {

                List<File> files = new ArrayList<File>();
                for (String module : compileTestConfiguration.getModules()) {

                    File moduleFile = moduleToJarMap.get(module);
                    if (moduleFile != null) {
                        files.add(moduleFile);
                    }

                }
                StandardJavaFileManagerBridge.setLocation(stdJavaFileManager, (JavaFileManager.Location) StandardLocation.valueOf("MODULE_PATH"), files);



                System.out.println("SET ADD_MODULES : " + compileTestConfiguration.getModules().toString());
                Method method = JavaCompiler.CompilationTask.class.getMethod("addModules", Iterable.class);
                method.invoke(compilationTask, compileTestConfiguration.getModules());

            } catch (Exception e) {
                // method not found or access issues ==> java version <9
                // ignore => only thrown for java <9
                e.printStackTrace();
            }
        }

        Boolean compilationSucceeded = compilationTask.call();

        return new

                CompilationResult(compilationSucceeded, diagnostics, javaFileManager);

    }

    /**
     * Allows checking if annotation processor has been applied during the compilation test.
     *
     * @param diagnostics the DiagnosticCollector instance
     */
    protected void checkIfProcessorsHaveBeenApplied(DiagnosticCollector<JavaFileObject> diagnostics) {

        Set<String> messages = CompileTestUtilities.getMessages(diagnostics, Diagnostic.Kind.NOTE);

        outer:
        for (AnnotationProcessorWrapper processor : compileTestConfiguration.getWrappedProcessors()) {

            for (String message : messages) {
                if (message.equals(processor.getProcessorWasAppliedMessage())) {
                    continue outer;
                }
            }

            throw new FailingAssertionException(String.format(MESSAGE_PROCESSOR_HASNT_BEEN_APPLIED, processor.getWrappedProcessor().getClass().getCanonicalName()));

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

        Set<String> messages = CompileTestUtilities.getMessages(diagnostics, kind);

        outer:
        for (String messageToCheck : messsagesToCheck) {

            for (String message : messages) {

                if (message.contains(messageToCheck)) {
                    continue outer;
                }

            }

            // Not found ==> assertion fails
            throw new FailingAssertionException(String.format(MESSAGE_HAVENT_FOUND_MESSSAGE, messageToCheck, kind.name(), messages.toString()));

        }

    }


}
