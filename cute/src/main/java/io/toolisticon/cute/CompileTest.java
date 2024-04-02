package io.toolisticon.cute;

import io.toolisticon.cute.extension.api.AssertionSpiServiceLocator;
import io.toolisticon.cute.extension.api.ModuleSupportSpi;
import io.toolisticon.cute.extension.api.ModuleSupportSpiServiceLocator;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a compile test.
 */
class CompileTest {

    // Messages


    private final CuteApi.CompilerTestBB compileTestConfiguration;

    private final Set<AnnotationProcessorWrapper> wrappedAnnotationProcessors;

    /**
     * Main constructor.
     *
     * @param compileTestConfiguration the configuration used during tests.
     */
    public CompileTest(final CuteApi.CompilerTestBB compileTestConfiguration) {
        this.compileTestConfiguration = compileTestConfiguration;
        this.wrappedAnnotationProcessors = AnnotationProcessorWrapper.getWrappedProcessors(compileTestConfiguration);

    }

    /**
     * The compile-test execution main method.
     */
    CompilationResult executeTest() {

        CompilationResult compilationResult = null;


        try {

            // Execute tests now
            compilationResult = compile(compileTestConfiguration, wrappedAnnotationProcessors);


            // Check if all processors have been applied
            checkIfProcessorsHaveBeenApplied(compilationResult.getDiagnostics());

            // check if error messages and shouldSucceed aren't set contradictory
            if (compileTestConfiguration.compilationSucceeded() != null
                    && compileTestConfiguration.compilationSucceeded()
                    && compileTestConfiguration.countErrorMessageChecks() > 0) {
                throw new InvalidTestConfigurationException(Constants.Messages.MESSAGE_COMPILATION_SHOULD_SUCCEED_AND_ERROR_MESSAGE_EXPECTED.produceMessage());
            }


            // Check if compilation succeeded
            if (compileTestConfiguration.compilationSucceeded() != null && !compileTestConfiguration.compilationSucceeded().equals(compilationResult.getCompilationSucceeded())) {

                throw new FailingAssertionException(
                        compileTestConfiguration.compilationSucceeded()
                                ? Constants.Messages.MESSAGE_COMPILATION_SHOULD_HAVE_SUCCEEDED_BUT_FAILED.produceMessage() + "\nERRORS:\n" + CompileTestUtilities.getMessages(compilationResult.getDiagnostics(), Diagnostic.Kind.ERROR)
                                : Constants.Messages.MESSAGE_COMPILATION_SHOULD_HAVE_FAILED_BUT_SUCCEEDED.produceMessage()
                );

            }


            // Check messages
            checkMessages(compilationResult.getDiagnostics());

            // Check compiled classes
            for (GeneratedClassesTest generatedClassesTest : this.compileTestConfiguration.getGeneratedClassesTest()) {
                CuteClassLoader cuteClassLoader = new CuteClassLoaderImpl(compilationResult.getCompileTestFileManager());

                try {
                    generatedClassesTest.doTests(cuteClassLoader);
                } catch (AssertionError e) {
                    // make sure AssertionErrors will pass
                    throw e;
                } catch (Exception e) {
                    throw new FailingAssertionException(Constants.Messages.MESSAGE_GOT_UNEXPECTED_EXCEPTION_DURING_CLASS_TEST_ERROR.produceMessage(generatedClassesTest, e.getMessage()));
                }
            }

            // Check generated JavaFileObjects
            for (CuteApi.GeneratedJavaFileObjectCheckBB generatedJavaFileObjectCheck : this.compileTestConfiguration.javaFileObjectChecks()) {
                if (CuteApi.FileObjectCheckType.EXISTS.equals(generatedJavaFileObjectCheck.getCheckType())) {
                    if (!compilationResult.getCompileTestFileManager().existsExpectedJavaFileObject(generatedJavaFileObjectCheck.getLocation(), generatedJavaFileObjectCheck.getClassName(), generatedJavaFileObjectCheck.getKind())) {
                        throw new FailingAssertionException(Constants.Messages.MESSAGE_JFO_DOESNT_EXIST.produceMessage(getJavaFileObjectInfoString(generatedJavaFileObjectCheck)));
                    } else {

                        try {

                            JavaFileObject foundJavaFileObject = compilationResult.getCompileTestFileManager().getJavaFileForInput(generatedJavaFileObjectCheck.getLocation(), generatedJavaFileObjectCheck.getClassName(), generatedJavaFileObjectCheck.getKind());

                            // check with passed matcher
                            if (generatedJavaFileObjectCheck.getGeneratedFileObjectMatcher() != null) {

                                try {
                                    if (!generatedJavaFileObjectCheck.getGeneratedFileObjectMatcher().check(foundJavaFileObject)) {

                                        // Throw Exception as fallback if not done by matcher
                                        throw new FailingAssertionException(Constants.Messages.MESSAGE_FO_COMPARISION_FAILED.produceMessage(generatedJavaFileObjectCheck.getGeneratedFileObjectMatcher().getClass().getCanonicalName()));


                                    }
                                } catch (FailingAssertionException e) {
                                    throw new FailingAssertionException(Constants.Messages.MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_MATCHER.produceMessage(getJavaFileObjectInfoString(generatedJavaFileObjectCheck), e.getMessage()));
                                }

                            }

                            // Check with test
                            if (generatedJavaFileObjectCheck.getGeneratedClassesTest() != null) {
                                CuteClassLoader cuteClassLoader = new CuteClassLoaderImpl(compilationResult.getCompileTestFileManager());

                                try {
                                    generatedJavaFileObjectCheck.getGeneratedClassesTest().doTests(cuteClassLoader.getClass(generatedJavaFileObjectCheck.getClassName()), cuteClassLoader);
                                } catch (AssertionError e) {
                                    throw e;
                                } catch (Exception e) {
                                    throw new FailingAssertionException(Constants.Messages.MESSAGE_GOT_UNEXPECTED_EXCEPTION_DURING_CLASS_TEST_ERROR.produceMessage(getJavaFileObjectInfoString(generatedJavaFileObjectCheck), e.getMessage()));
                                }
                            }


                        } catch (IOException e) {
                            // ignore
                        }


                    }
                } else {
                    if (compilationResult.getCompileTestFileManager().existsExpectedJavaFileObject(generatedJavaFileObjectCheck.getLocation(), generatedJavaFileObjectCheck.getClassName(), generatedJavaFileObjectCheck.getKind())) {
                        throw new FailingAssertionException(Constants.Messages.MESSAGE_JFO_EXISTS_BUT_SHOULD_BE_NON_EXISTENT.produceMessage(getJavaFileObjectInfoString(generatedJavaFileObjectCheck)));
                    }
                }


            }

            for (CuteApi.GeneratedFileObjectCheckBB generatedFileObjectCheck : this.compileTestConfiguration.fileObjectChecks()) {

                if (CuteApi.FileObjectCheckType.EXISTS.equals(generatedFileObjectCheck.getCheckType())) {

                    if (!compilationResult.getCompileTestFileManager().existsExpectedFileObject(generatedFileObjectCheck.getLocation(), generatedFileObjectCheck.getPackageName(), generatedFileObjectCheck.getRelativeName())) {
                        throw new FailingAssertionException(Constants.Messages.MESSAGE_FO_DOESNT_EXIST.produceMessage(getFileObjectInfoString(generatedFileObjectCheck)));
                    } else {

                        try {

                            FileObject foundFileObject = compilationResult.getCompileTestFileManager().getFileForInput(generatedFileObjectCheck.getLocation(), generatedFileObjectCheck.getPackageName(), generatedFileObjectCheck.getRelativeName());

                            // check with passed matcher
                            if (generatedFileObjectCheck.getGeneratedFileObjectMatchers() != null) {

                                try {
                                    for (GeneratedFileObjectMatcher matcher : generatedFileObjectCheck.getGeneratedFileObjectMatchers()) {
                                        if (!matcher.check(foundFileObject)) {
                                            // Throw Exception as fallback if not done by matcher
                                            throw new FailingAssertionException(Constants.Messages.MESSAGE_FO_COMPARISION_FAILED.produceMessage(matcher.getClass().getCanonicalName()));
                                        }
                                    }
                                } catch (FailingAssertionException e) {
                                    throw new FailingAssertionException(Constants.Messages.MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_MATCHER.produceMessage(getFileObjectInfoString(generatedFileObjectCheck), e.getMessage()));
                                }

                            }

                        } catch (IOException e) {
                            throw new FailingAssertionException(Constants.Messages.MESSAGE_TECHNICAL_ERROR.produceMessage(e.getMessage()));
                        }


                    }
                } else {
                    if (compilationResult.getCompileTestFileManager().existsExpectedFileObject(generatedFileObjectCheck.getLocation(), generatedFileObjectCheck.getPackageName(), generatedFileObjectCheck.getRelativeName())) {
                        throw new FailingAssertionException(Constants.Messages.MESSAGE_FO_EXISTS_BUT_SHOULD_BE_NON_EXISTENT.produceMessage(getFileObjectInfoString(generatedFileObjectCheck)));
                    }
                }

            }


        } catch (FailingAssertionException e) {

            // now trigger failing assertion, but also enrich message with debug output
            AssertionSpiServiceLocator.locate().fail(e.getMessage() + "\n" + DebugOutputGenerator.getDebugOutput(compilationResult, compileTestConfiguration, e));

        } catch (RuntimeException e) {

            if (e.getCause() != null && AssertionError.class.isAssignableFrom(e.getCause().getClass())) {
                FailingAssertionException failingAssertionException = new FailingAssertionException(e.getMessage(), e.getCause());
                AssertionSpiServiceLocator.locate().fail(e.getCause().getMessage() + "\n" + DebugOutputGenerator.getDebugOutput(compilationResult, compileTestConfiguration, failingAssertionException));
                throw (failingAssertionException);
            }

            if (e.getCause() != null && FailingAssertionException.class.isAssignableFrom(e.getCause().getClass())) {
                // now trigger failing assertion, but also enrich message with debug output
                AssertionSpiServiceLocator.locate().fail(e.getCause().getMessage() + "\n" + DebugOutputGenerator.getDebugOutput(compilationResult, compileTestConfiguration, (FailingAssertionException) e.getCause()));
                throw (FailingAssertionException) (e.getCause());
            }

            throw e;


        }
        return compilationResult;
    }


    static String getJavaFileObjectInfoString(CuteApi.GeneratedJavaFileObjectCheckBB generatedJavaFileObjectCheck) {
        return generatedJavaFileObjectCheck.getLocation() + "; " + generatedJavaFileObjectCheck.getClassName() + "; " + generatedJavaFileObjectCheck.getKind();
    }

    static String getFileObjectInfoString(CuteApi.GeneratedFileObjectCheckBB generatedFileObjectCheck) {
        return generatedFileObjectCheck.getLocation() + "; " + generatedFileObjectCheck.getPackageName() + "; " + generatedFileObjectCheck.getRelativeName();
    }

    /**
     * Init the compilation and compile.
     *
     * @param compileTestConfiguration the compile-test configuration to use
     * @return the compilation result
     */
    public static CompilationResult compile(CuteApi.CompilerTestBB compileTestConfiguration, Set<AnnotationProcessorWrapper> wrappedAnnotationProcessors) {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager stdJavaFileManager = compiler.getStandardFileManager(diagnostics, null, null);


        // Configure java compilation task
        CompileTestFileManager javaFileManager = new CompileTestFileManager(stdJavaFileManager);

        JavaCompiler.CompilationTask compilationTask = compiler.getTask(
                null,
                javaFileManager,
                diagnostics,
                compileTestConfiguration.compilerOptions().isEmpty() ? null : compileTestConfiguration.getNormalizedCompilerOptions(),
                null,
                compileTestConfiguration.sourceFiles());

        compilationTask.setProcessors(wrappedAnnotationProcessors);

        // handle java 9 module support via SPI to be backward compatible with older Java versions prior to java 9
        if (!Java9SupportCheck.UNSUPPORTED_JAVA_VERSION) {
            ModuleSupportSpi moduleService = ModuleSupportSpiServiceLocator.locate();
            if (moduleService != null) {
                moduleService.applyModulePath(stdJavaFileManager, compilationTask, compileTestConfiguration.modules());
            }
        }

        Boolean compilationSucceeded = compilationTask.call();

        return new CompilationResult(compilationSucceeded, diagnostics, javaFileManager);

    }

    /**
     * Allows checking if annotation processor has been applied during the compilation test.
     *
     * @param diagnostics the DiagnosticCollector instance
     */
    void checkIfProcessorsHaveBeenApplied(DiagnosticCollector<JavaFileObject> diagnostics) {

        Set<String> messages = CompileTestUtilities.getMessages(diagnostics, Diagnostic.Kind.NOTE);

        outer:
        for (AnnotationProcessorWrapper processor : this.wrappedAnnotationProcessors) {

            for (String message : messages) {
                if (message.equals(processor.getProcessorWasAppliedMessage())) {
                    continue outer;
                }
            }

            throw new FailingAssertionException(Constants.Messages.MESSAGE_PROCESSOR_HASNT_BEEN_APPLIED.produceMessage(processor.getWrappedProcessor().getClass().getCanonicalName(), processor.getSupportedAnnotationTypes()));

        }

    }


    /**
     * Method to check for specific messages.
     */
    void checkMessages(DiagnosticCollector<JavaFileObject> diagnostics) {

        // Just check messages of matching kind
        Map<Diagnostic.Kind, List<CuteApi.CompilerMessageCheckBB>> compileMessageChecks = getCompilerMessageCheckByKindMap(compileTestConfiguration);

        for (Map.Entry<Diagnostic.Kind, List<CuteApi.CompilerMessageCheckBB>> entry : compileMessageChecks.entrySet()) {

            Set<Diagnostic<? extends JavaFileObject>> filteredDiagnostics = CompileTestUtilities.getDiagnosticByKind(diagnostics, entry.getKey());

            outer:
            for (CuteApi.CompilerMessageCheckBB messageToCheck : entry.getValue()) {

                for (Diagnostic<?> element : filteredDiagnostics) {

                    String localizedMessage = element.getMessage(messageToCheck.withLocale());


                    // Check message
                    switch (messageToCheck.getComparisonType()) {

                        case EQUALS: {
                            if (!localizedMessage.equals(messageToCheck.getSearchString().get(0))) {
                                continue;
                            }
                            break;
                        }
                        case CONTAINS:
                        default: {
                            if (!messageToCheck.getSearchString().stream().allMatch(localizedMessage::contains)) {
                                continue;
                            }
                        }
                    }

                    // check source
                    if (messageToCheck.atSource() != null && !messageToCheck.atSource().equals(((FileObject) element.getSource()).getName())) {
                        continue;
                    }

                    // check line
                    if (messageToCheck.atLine() != null && element.getLineNumber() != messageToCheck.atLine()) {
                        continue;
                    }

                    // check column
                    if (messageToCheck.atColumn() != null && element.getColumnNumber() != messageToCheck.atColumn()) {
                        continue;
                    }

                    // found it
                    continue outer;

                }

                // Not found ==> assertion fails
                throw new FailingAssertionException(Constants.Messages.MESSAGE_HAVENT_FOUND_MESSSAGE.produceMessage(messageToCheck.getSearchString(), messageToCheck.getKind().name(), messageToCheck.toString()));

            }

        }

    }


    public Map<Diagnostic.Kind, List<CuteApi.CompilerMessageCheckBB>> getCompilerMessageCheckByKindMap(CuteApi.CompilerTestBB compilerTestBB) {
        Map<Diagnostic.Kind, List<CuteApi.CompilerMessageCheckBB>> map = new HashMap<>();

        for (CuteApi.CompilerMessageCheckBB compilerMessageCheck : compilerTestBB.compilerMessageChecks()) {
            map.computeIfAbsent(compilerMessageCheck.getKind().toDiagnosticsKind(),(k) -> new ArrayList<>()).add(compilerMessageCheck);
        }

        return map;
    }



}
