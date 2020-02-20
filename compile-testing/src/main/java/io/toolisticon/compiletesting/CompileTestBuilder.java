package io.toolisticon.compiletesting;

import io.toolisticon.compiletesting.impl.CompileTest;
import io.toolisticon.compiletesting.impl.CompileTestConfiguration;
import io.toolisticon.compiletesting.matchers.CoreGeneratedFileObjectMatchers;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.lang.annotation.Annotation;
import java.util.Locale;

/**
 * Compile test builder.
 * Implemented with immutable state / configuration, so it's safe to create a base configuration in test class and to further specify the tests in the unit test method.
 */
public class CompileTestBuilder {

    /**
     * Configures how FileObjects should be compared.
     */
    public enum ExpectedFileObjectMatcherKind {
        /**
         * Does binary comparision.
         * Be careful: tests using binary comparision may fail because of OS depending line-endings.
         */
        BINARY {
            @Override
            public <T extends FileObject> GeneratedFileObjectMatcher<T> createMatcher(T expectedFileObject) {
                return CoreGeneratedFileObjectMatchers.createBinaryMatcher(expectedFileObject);
            }
        },
        /**
         * Textual comparision line by line by ignoring the OS depending line-endings.
         */
        TEXT_IGNORE_LINE_ENDINGS {
            @Override
            public <T extends FileObject> GeneratedFileObjectMatcher<T> createMatcher(T expectedFileObject) {
                return CoreGeneratedFileObjectMatchers.createIgnoreLineEndingsMatcher(expectedFileObject);
            }
        };

        /**
         * Creates a matcher for FileObjects for enum value.
         *
         * @param expectedFileObject the expected FileObject
         * @param <T>                The type of FileObject
         * @return a GeneratedFileObjectMatcher instance that can be used to compare FileObjects
         */
        protected abstract <T extends FileObject> GeneratedFileObjectMatcher<T> createMatcher(T expectedFileObject);
    }


    /**
     * Abstract base builder class.
     * Contains common configurations.
     *
     * @param <T> The implementing type passed to base class
     */
    public static abstract class BasicBuilder<T extends BasicBuilder<T>> {

        /**
         * the current immutable CompileTestConfiguration
         */
        final CompileTestConfiguration compileTestConfiguration;

        BasicBuilder(CompileTestConfiguration compileTestConfiguration) {
            this.compileTestConfiguration = compileTestConfiguration;
        }

        /**
         * Compilation is expected to be successful.
         *
         * @return the next builder instance
         */
        public T compilationShouldSucceed() {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.setCompilationShouldSucceed(true);
            return createNextInstance(nextConfiguration);
        }

        /**
         * Compilation is expected to be failing.
         *
         * @return the next builder instance
         */
        public T compilationShouldFail() {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.setCompilationShouldSucceed(false);
            return createNextInstance(nextConfiguration);
        }

        /**
         * Use compiler options.
         * Options with parameters can, but must not be split over two consecutive Strings.
         * Those options can be put in one single String (e.g. "-source 1.7" or "-target 1.7").
         *
         * @param compilerOptions the options to use
         * @return the next builder instance
         */
        public T useCompilerOptions(String... compilerOptions) {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addCompilerOptions(compilerOptions);
            return createNextInstance(nextConfiguration);
        }


        /**
         * Defines modules used during compilation.
         * This configuration will be ignored for Java versions &lt; 9.
         *
         * @param modules The modules to use during compilation
         * @return the next builder instance
         */
        public T useModules(String... modules) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);

            if (modules != null) {

                nextConfiguration.addModules(modules);

            }

            return createNextInstance(nextConfiguration);
        }

        /**
         * Starts a sub builder for adding a check for an error compiler message.
         *
         * @return an immutable builder instance for creating a complex compiler message check
         */
        public CompileMessageCheckBuilder<T> expectErrorMessage() {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            return new CompileMessageCheckBuilder<T>(createNextInstance(nextConfiguration), Diagnostic.Kind.ERROR);
        }

        /**
         * Starts a sub builder for adding a check for a mandatory warning compiler message.
         *
         * @return an immutable builder instance for creating a complex compiler message check
         */
        public CompileMessageCheckBuilder<T> expectMandatoryWarning() {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            return new CompileMessageCheckBuilder<T>(createNextInstance(nextConfiguration), Diagnostic.Kind.MANDATORY_WARNING);
        }

        /**
         * Starts a sub builder for adding a check for a warning compiler message.
         *
         * @return an immutable builder instance for creating a complex compiler message check
         */
        public CompileMessageCheckBuilder<T> expectWarning() {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            return new CompileMessageCheckBuilder<T>(createNextInstance(nextConfiguration), Diagnostic.Kind.WARNING);
        }

        /**
         * Starts a sub builder for adding a check for a note compiler message.
         *
         * @return an immutable builder instance for creating a complex compiler message check
         */
        public CompileMessageCheckBuilder<T> expectNote() {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            return new CompileMessageCheckBuilder<T>(createNextInstance(nextConfiguration), Diagnostic.Kind.NOTE);
        }

        /**
         * Adds some warning checks.
         *
         * @param warningChecks the warning checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public T expectedWarningMessages(String... warningChecks) {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (warningChecks != null) {
                nextConfiguration.addWarningMessageCheck(CompileTestConfiguration.ComparisionKind.CONTAINS, warningChecks);
            }
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds some mandatory warning checks.
         *
         * @param mandatoryWarningChecks the mandatory warning checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public T expectedMandatoryWarningMessages(String... mandatoryWarningChecks) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (mandatoryWarningChecks != null) {
                nextConfiguration.addMandatoryWarningMessageCheck(CompileTestConfiguration.ComparisionKind.CONTAINS, mandatoryWarningChecks);
            }
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds some error checks.
         *
         * @param errorChecksToSet the error checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public T expectedErrorMessages(String... errorChecksToSet) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (errorChecksToSet != null) {
                nextConfiguration.addErrorMessageCheck(CompileTestConfiguration.ComparisionKind.CONTAINS, errorChecksToSet);
            }
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds some notes checks.
         *
         * @param noteChecksToSet the notes checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public T expectedNoteMessages(String... noteChecksToSet) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (noteChecksToSet != null) {
                nextConfiguration.addNoteMessageCheck(CompileTestConfiguration.ComparisionKind.CONTAINS, noteChecksToSet);
            }
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds a CompilerMessageCheck.
         *
         * @param compilerMessageCheck The Compiler Message check
         * @return The next immutable builder instance
         */
        T addCompilerMessageCheck(CompileTestConfiguration.CompilerMessageCheck compilerMessageCheck) {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (compilerMessageCheck != null) {
                nextConfiguration.addCompilerMessageCheck(compilerMessageCheck);
            }
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds a check if a specific generated FileObject exists.
         *
         * @param location     the location (usually a {@link javax.tools.StandardLocation})
         * @param packageName  the package name
         * @param relativeName the relative name to the passed package
         * @return the next builder instance
         */
        public T expectedFileObjectExists(JavaFileManager.Location location, String packageName, String relativeName) {
            return expectedFileObjectExists(location, packageName, relativeName, (FileObject) null);
        }

        /**
         * Adds a check if a specific generated FileObject exists (uses binary comparision).
         * Additionally checks if files are equal if passed expectedFileObject is not null.
         *
         * @param location           the location (usually from javax.tools.StandardLocation)
         * @param packageName        the package name
         * @param relativeName       the package relative name
         * @param expectedFileObject the file used for comparision of content
         * @return the next builder instance
         */
        public T expectedFileObjectExists(
                JavaFileManager.Location location,
                String packageName,
                String relativeName,
                FileObject expectedFileObject) {

            return expectedFileObjectExists(location, packageName, relativeName, ExpectedFileObjectMatcherKind.BINARY, expectedFileObject);

        }

        /**
         * Adds a check if a specific generated FileObject exists.
         * Additionally checks if files are equal if passed expectedFileObject is not null.
         * Comparision algorithm can be selected by matcherKind parameter
         *
         * @param location           the location (usually from javax.tools.StandardLocation)
         * @param packageName        the package name
         * @param relativeName       the package relative name
         * @param matcherKind        the matcher kind
         * @param expectedFileObject the file used for comparision of content
         * @return the next builder instance
         */
        public T expectedFileObjectExists(
                JavaFileManager.Location location,
                String packageName,
                String relativeName,
                ExpectedFileObjectMatcherKind matcherKind,
                FileObject expectedFileObject) {

            if (matcherKind == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("matcherKind"));
            }

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            GeneratedFileObjectMatcher<FileObject>[] matchers = null;
            if (expectedFileObject != null) {
                matchers = new GeneratedFileObjectMatcher[1];
                matchers[0] = matcherKind.createMatcher(expectedFileObject);
            }
            nextConfiguration.addGeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, location, packageName, relativeName, matchers);
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds a check if a specific generated FileObject exists.
         * Additionally checks if file object matches with passed matcher.
         *
         * @param location                   the location (usually from javax.tools.StandardLocation)
         * @param packageName                the package name
         * @param relativeName               the package relative name
         * @param generatedFileObjectMatcher the matcher to use
         * @return the next builder instance
         */
        @SafeVarargs
        public final T expectedFileObjectExists(
                JavaFileManager.Location location,
                String packageName,
                String relativeName,
                GeneratedFileObjectMatcher<FileObject>... generatedFileObjectMatcher) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addGeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, location, packageName, relativeName, generatedFileObjectMatcher);
            return createNextInstance(nextConfiguration);

        }


        /**
         * Adds a check if a specific generated FileObject doesn't exists.
         *
         * @param location     the location (usually from javax.tools.StandardLocation)
         * @param packageName  the package name
         * @param relativeName the package relative name
         * @return the next builder instance
         */
        public T expectFileObjectNotToExist(
                JavaFileManager.Location location,
                String packageName,
                String relativeName) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addGeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.DOESNT_EXIST, location, packageName, relativeName, null);
            return createNextInstance(nextConfiguration);

        }


        /**
         * Checks if a generated source file exists.
         *
         * @param className the full qualified name of the class
         * @return the next builder instance
         */
        public T expectedGeneratedSourceFileExists(String className) {
            return expectedJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, className, JavaFileObject.Kind.SOURCE);
        }

        /**
         * Checks if a generated source file exists and if it matches passed JavaFileObject by using binary comparision.
         * Additionally checks if files are equal if passed expectedJavaFileObject is not null.
         *
         * @param className              the full qualified name of the class
         * @param expectedJavaFileObject the file used for comparision of content
         * @return the next builder instance
         */
        public T expectedGeneratedSourceFileExists(String className, JavaFileObject expectedJavaFileObject) {
            return expectedJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, className, JavaFileObject.Kind.SOURCE, expectedJavaFileObject);
        }

        /**
         * Adds a check if a specific class file exists.
         *
         * @param className the class name
         * @return the next builder instance
         */
        public T expectedGeneratedClassExists(String className) {
            return expectedJavaFileObjectExists(StandardLocation.CLASS_OUTPUT, className, JavaFileObject.Kind.CLASS);
        }

        /**
         * Adds a check if a specific generated source file exists.
         * Additionally checks if java file object matches with passed matcher.
         *
         * @param className                    the class name
         * @param generatedJavaFileObjectCheck the matcher to use
         * @return the next builder instance
         */
        public T expectedGeneratedSourceFileExists(String className, GeneratedFileObjectMatcher<JavaFileObject> generatedJavaFileObjectCheck) {
            return expectedJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, className, JavaFileObject.Kind.SOURCE, generatedJavaFileObjectCheck);
        }

        /**
         * Adds a check if a specific JavaFileObject doesn't exist.
         *
         * @param className the class name
         * @return the next builder instance
         */
        public T expectGeneratedSourceFileNotToExist(String className) {
            return expectJavaFileObjectNotToExist(StandardLocation.SOURCE_OUTPUT, className, JavaFileObject.Kind.SOURCE);
        }

        /**
         * Adds a check if a specific JavaFileObject exists.
         *
         * @param location  the location (usually from javax.tools.StandardLocation)
         * @param className the class name
         * @param kind      the kind of the JavaFileObject
         * @return the next builder instance
         */
        public T expectedJavaFileObjectExists(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) {
            return expectedJavaFileObjectExists(location, className, kind, (JavaFileObject) null);
        }


        /**
         * Adds a check if a specific generated JavaFileObject exists (uses binary comparision).
         * Additionally checks if files are equal if passed expectedJavaFileObject is not null.
         *
         * @param location               the location (usually from javax.tools.StandardLocation)
         * @param className              the class name
         * @param kind                   the kind of the JavaFileObject
         * @param expectedJavaFileObject the file used for comparision of content
         * @return the next builder instance
         */
        public T expectedJavaFileObjectExists(
                JavaFileManager.Location location,
                String className,
                JavaFileObject.Kind kind,
                JavaFileObject expectedJavaFileObject) {

            return expectedJavaFileObjectExists(location, className, kind, ExpectedFileObjectMatcherKind.BINARY, expectedJavaFileObject);

        }

        /**
         * Adds a check if a specific generated JavaFileObject exists.
         * Additionally checks if files are equal if passed expectedJavaFileObject is not null.
         *
         * @param location               the location (usually from javax.tools.StandardLocation)
         * @param className              the class name
         * @param kind                   the kind of the JavaFileObject
         * @param expectedJavaFileObject the file used for comparision of content
         * @return the next builder instance
         */
        public T expectedJavaFileObjectExists(
                JavaFileManager.Location location,
                String className,
                JavaFileObject.Kind kind,
                ExpectedFileObjectMatcherKind expectedFileObjectMatcherKind,
                JavaFileObject expectedJavaFileObject) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addGeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, location, className, kind, expectedJavaFileObject != null ? expectedFileObjectMatcherKind.createMatcher(expectedJavaFileObject) : null);
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds a check if a specific generated JavaFileObject exists.
         * Additionally checks if java file object matches with passed matcher.
         *
         * @param location                     the location (usually a {@link javax.tools.StandardLocation})
         * @param className                    the class name
         * @param kind                         the kind of the JavaFileObject
         * @param generatedJavaFileObjectCheck the matcher to use
         * @return the next builder instance
         */
        public T expectedJavaFileObjectExists(
                JavaFileManager.Location location,
                String className,
                JavaFileObject.Kind kind,
                GeneratedFileObjectMatcher<JavaFileObject> generatedJavaFileObjectCheck) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addGeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, location, className, kind, generatedJavaFileObjectCheck);
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds a check if a specific JavaFileObject doesn't exist.
         *
         * @param location  the location (usually from javax.tools.StandardLocation)
         * @param className the class name
         * @param kind      the kind of the JavaFileObject
         * @return the next builder instance
         */
        public T expectJavaFileObjectNotToExist(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addGeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.DOESNT_EXIST, location, className, kind, null);

            return createNextInstance(nextConfiguration);
        }

        /**
         * Created the compile test configuration instance.
         *
         * @return the configuration instance
         */
        CompileTestConfiguration createCompileTestConfiguration() {
            return CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
        }

        /**
         * Executes the compilation tests.
         *
         * @throws IllegalStateException if there's some invalid configuration
         */
        public void testCompilation() {
            if (compileTestConfiguration.getSourceFiles().size() == 0) {
                throw new IllegalStateException(Constants.Messages.ISE_MUST_CONFIGURE_AT_LEAST_ONE_SOURCE_FILE.produceMessage());
            }

            new CompileTest(createCompileTestConfiguration()).executeTest();
        }


        /**
         * Creates the next immutable builder instance
         *
         * @param compileTestConfiguration the configuration to be used
         * @return the next builder instance
         */
        protected abstract T createNextInstance(CompileTestConfiguration compileTestConfiguration);


    }

    /**
     * Builder class used to create compile tests (== Integration tests).
     * Class is designed to produce immutable builder instances in it's fluent api.
     */
    public static class CompilationTestBuilder extends BasicBuilder<CompilationTestBuilder> {

        /**
         * Forwarding constructor.
         * Clones passed compileTestConfiguration.
         *
         * @param compileTestConfiguration the compile test configuration
         */
        private CompilationTestBuilder(CompileTestConfiguration compileTestConfiguration) {
            super(compileTestConfiguration);
        }

        /**
         * Adds processors.
         *
         * @param processorTypes the processor types to use, processors must have a noarg constructor
         * @return the CompilationTestBuilder instance
         */
        @SafeVarargs
        public final CompilationTestBuilder addProcessors(Class<? extends Processor>... processorTypes) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (processorTypes != null) {
                nextConfiguration.addProcessorTypes(processorTypes);
            }
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds processors and links them with an expected exception check.
         * Be aware that sharing a processor instance between tests might lead to undetermined behavior!!
         * <p>
         * This method might be removed soon due to the potential issues.
         *
         * @param processor the processor type to use, processors must have a noarg constructor
         * @param exception the expected exception thrown by the passed processor
         * @return the CompilationTestBuilder instance
         */
        public CompilationTestBuilder addProcessorWithExpectedException(Class<? extends Processor> processor, Class<? extends Throwable> exception) {

            if (processor == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("processor"));
            }
            if (exception == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("exception"));
            }
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addProcessorWithExpectedException(processor, exception);
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds source files to compile to compilation test.
         *
         * @param sources the sources to use
         * @return the CompilationTestBuilder instance
         */
        public final CompilationTestBuilder addSources(JavaFileObject... sources) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (sources != null) {
                nextConfiguration.addSourceFiles(sources);
            }
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds source files to compile to compilation test.
         * Sources will be read from resources.
         * Source file names must either end with ".java" or ".java.ct".
         *
         * @param sources the sources to use
         * @return the CompilationTestBuilder instance
         */
        public final CompilationTestBuilder addSources(String... sources) {

            return addSources(JavaFileObjectUtils.readFromResources(sources));

        }

        /**
         * {@inheritDoc}
         */
        protected CompilationTestBuilder createNextInstance(CompileTestConfiguration compileTestConfiguration) {
            return new CompilationTestBuilder(compileTestConfiguration);
        }

    }

    public static class UnitTestBuilder extends BasicBuilder<UnitTestBuilder> {

        /**
         * Sets the processor to use.
         * The processor should support {@link TestAnnotation} annotation processing if no custom source file is defined.
         * If custom source is used you need to define a processor that is going to process the source file.
         *
         * @param processor the processor to use
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         */
        public UnitTestBuilder useProcessor(Processor processor) {

            if (processor == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("processor"));
            }

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);

            // remove existing processor
            nextConfiguration.getProcessors().clear();
            nextConfiguration.addProcessors(processor);

            return createNextInstance(nextConfiguration);
        }

        /**
         * Allows writing of unit tests.
         * You can pass in a {@link UnitTestProcessor} instance that contains your test code in it's unitTest method.
         * <p>
         * The {@link javax.annotation.processing.ProcessingEnvironment} and an Element of type ELEMENT_TYPE will passed to the UnitTestProcessor.unitTest method.
         * <p>
         * The {@link TestAnnotation} will be used to look up this Element during.
         * <p>
         * So please make sure that the {@link TestAnnotation} is used exactly once, when you are using a custom source files
         *
         * @param unitTestProcessor the processor to use
         * @param <ELEMENT_TYPE>    The expected element type (Must be TypeElement, if no custom source files are used)
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        public <ELEMENT_TYPE extends Element> UnitTestBuilder useProcessor(UnitTestProcessor<ELEMENT_TYPE> unitTestProcessor) {
            return useProcessor(Constants.DEFAULT_ANNOTATION, unitTestProcessor);
        }

        /**
         * Allows writing of unit tests.
         * You can pass in a {@link UnitTestProcessor} instance that contains your test code in it's unitTest method.
         * <p>
         * The {@link javax.annotation.processing.ProcessingEnvironment} and an Element of type ELEMENT_TYPE will passed to the UnitTestProcessor.unitTest method.
         * <p>
         * The {@link TestAnnotation} will be used to look up this Element during.
         * <p>
         * So please make sure that the {@link TestAnnotation} is used exactly once, when you are using a custom source files
         *
         * @param customAnnotationType the annotation type to search the element for
         * @param unitTestProcessor    the processor to use
         * @param <ELEMENT_TYPE>       The expected element type (Must be TypeElement, if no custom source files are used)
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        public <ELEMENT_TYPE extends Element> UnitTestBuilder useProcessor(Class<? extends Annotation> customAnnotationType, UnitTestProcessor<ELEMENT_TYPE> unitTestProcessor) {

            if (unitTestProcessor == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("unitTestProcessor"));
            }

            if (customAnnotationType == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("customAnnotationType"));
            }

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);

            // remove existing processor
            nextConfiguration.getProcessors().clear();
            nextConfiguration.addProcessors(new UnitTestAnnotationProcessorClass<ELEMENT_TYPE>(customAnnotationType, unitTestProcessor));

            return createNextInstance(nextConfiguration);
        }

        /**
         * Allows unit
         * Provides a specific processor instance that can be used for unit testing.
         * Additionally it provides an element
         * The passed processor won't be used as an annotation processor during compilation.
         * <p>
         * It will internally use a generic processor that
         * The processor should support {@link TestAnnotation} annotation processing if no custom source file is defined.
         * If custom source is used make sure {@link TestAnnotation} is used somewhere in the custom source file to make sure if annotation processor is used.
         *
         * @param processorUnderTestClass                         the Processor which should be provided as a
         * @param unitTestProcessorForTestingAnnotationProcessors the processor to use
         * @param <PROCESSOR_UNDER_TEST>                          The processor type under test
         * @param <ELEMENT_TYPE>                                  The expected element type to be processed
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */

        public <PROCESSOR_UNDER_TEST extends Processor, ELEMENT_TYPE extends Element> UnitTestBuilder useProcessor(Class<PROCESSOR_UNDER_TEST> processorUnderTestClass, UnitTestProcessorForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST, ELEMENT_TYPE> unitTestProcessorForTestingAnnotationProcessors) {
            return useProcessor(processorUnderTestClass, Constants.DEFAULT_ANNOTATION, unitTestProcessorForTestingAnnotationProcessors);
        }

        /**
         * Sets the processor to use.
         * The processor should support annotation processing of passed annotation type if no custom source file is defined.
         * Please make sure to add a custom source files in which the customAnnotationType annotation is used exactly once.
         *
         * @param processorUnderTestClass                         the Processor type
         * @param customAnnotationType                            the custom annotation used to search the element to pass be passed in
         * @param unitTestProcessorForTestingAnnotationProcessors the processor to use
         * @param <PROCESSOR_UNDER_TEST>                          The processor type under test
         * @param <ELEMENT_TYPE>                                  The expected element type to be processed
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor or customAnnotationType is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */

        public <PROCESSOR_UNDER_TEST extends Processor, ELEMENT_TYPE extends Element> UnitTestBuilder useProcessor(Class<PROCESSOR_UNDER_TEST> processorUnderTestClass, Class<? extends Annotation> customAnnotationType, UnitTestProcessorForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST, ELEMENT_TYPE> unitTestProcessorForTestingAnnotationProcessors) {

            if (unitTestProcessorForTestingAnnotationProcessors == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("unitTestProcessorForTestingAnnotationProcessors"));
            }

            if (customAnnotationType == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("customAnnotationType"));
            }

            if (processorUnderTestClass == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("processorUnderTestClass"));
            }

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);

            PROCESSOR_UNDER_TEST processorUnderTest = null;

            try {
                processorUnderTest = processorUnderTestClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_INSTANTIATE_PROCESSOR.produceMessage(processorUnderTestClass.getCanonicalName()));
            }


            // remove existing processor
            nextConfiguration.getProcessors().clear();
            nextConfiguration.addProcessors(new UnitTestAnnotationProcessorClassForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST, ELEMENT_TYPE>(processorUnderTest, customAnnotationType, unitTestProcessorForTestingAnnotationProcessors));

            return createNextInstance(nextConfiguration);
        }

        /**
         * Sets the source file used to apply processor on.
         * The source file must contain an annotation that is processed by the processor.
         *
         * @param source The source file to use
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed source is null.
         */
        public UnitTestBuilder useSource(JavaFileObject source) {


            if (source == null) {
                throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("source"));
            }

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);

            // clear existing sources
            nextConfiguration.getSourceFiles().clear();
            nextConfiguration.addSourceFiles(source);

            return createNextInstance(nextConfiguration);

        }

        /**
         * Sets the source file used to apply processor on.
         * The referenced resource file must contain an annotation that is processed by the processor.
         * The source file name must either end with ".java" or ".java.ct".
         *
         * @param resource The resource file to use
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed source is null.
         */
        public UnitTestBuilder useSource(String resource) {
            return useSource(JavaFileObjectUtils.readFromResource(resource));
        }

        /**
         * Sets an expected exception thrown in the unit test case.
         *
         * @param expectedException the exceptions expected to be thrown
         * @return the UnitTestBuilder instance
         */
        public UnitTestBuilder expectedThrownException(Class<? extends Throwable> expectedException) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (expectedException != null) {
                nextConfiguration.setExpectedThrownException(expectedException);
            }
            return createNextInstance(nextConfiguration);

        }


        /**
         * {@inheritDoc}
         */
        public void testCompilation() {

            if (compileTestConfiguration.getProcessors().size() == 0) {
                throw new IllegalStateException(Constants.Messages.ISE_MUST_CONFIGURE_AT_LEAST_ONE_PROCESSOR.produceMessage());
            }

            super.testCompilation();

        }

        /**
         * Internal constructor
         *
         * @param compileTestConfiguration the compile test configuration
         */
        private UnitTestBuilder(CompileTestConfiguration compileTestConfiguration) {
            super(compileTestConfiguration);
        }


        /**
         * Returns the default Source file object.
         *
         * @return the default source file object
         */
        public static JavaFileObject getDefaultSource() {
            return JavaFileObjectUtils.readFromResource(Constants.DEFAULT_UNIT_TEST_SOURCE_FILE);
        }

        /**
         * {@inheritDoc}
         */
        protected UnitTestBuilder createNextInstance(CompileTestConfiguration compileTestConfiguration) {
            return new UnitTestBuilder(compileTestConfiguration);
        }

    }

    /**
     * Fluent immutable builder for creation of complex compiler message checks.
     *
     * @param <COMPILETESTBUILDER> The enclosing builder.
     */
    public static class CompileMessageCheckBuilder<COMPILETESTBUILDER extends BasicBuilder<COMPILETESTBUILDER>> {

        private final COMPILETESTBUILDER compileTestBuilder;

        private Diagnostic.Kind kind;
        private CompileTestConfiguration.ComparisionKind comparisionKind;
        private String expectedMessage;
        private Locale locale;
        private String source;
        private Long lineNumber;
        private Long columnNumber;

        /**
         * Constructor.
         *
         * @param compileTestBuilder the enclosing builder
         * @param kind               the message kind that needs to be checked
         */
        CompileMessageCheckBuilder(COMPILETESTBUILDER compileTestBuilder, Diagnostic.Kind kind) {
            this.compileTestBuilder = compileTestBuilder;
            this.kind = kind;
        }

        /**
         * The line number to search the compiler message at.
         *
         * @param lineNumber the line number to check for.Line check will be skipped if passed lineNumber is null.
         * @return the next immutable builder instance
         */
        public CompileMessageCheckBuilder<COMPILETESTBUILDER> atLineNumber(Long lineNumber) {
            CompileMessageCheckBuilder<COMPILETESTBUILDER> nextBuilder = createNextBuilder();
            nextBuilder.lineNumber = lineNumber;
            return nextBuilder;
        }

        /**
         * The column number to search the compiler message at.
         *
         * @param columnNumber the column number to check for. Column check will be skipped if passed columnNumber is null.
         * @return the next immutable builder instance
         */
        public CompileMessageCheckBuilder<COMPILETESTBUILDER> atColumnNumber(Long columnNumber) {
            CompileMessageCheckBuilder<COMPILETESTBUILDER> nextBuilder = createNextBuilder();
            nextBuilder.columnNumber = columnNumber;
            return nextBuilder;
        }

        /**
         * Do check for localized compiler message.
         *
         * @param locale the locale to use, or null for default locale.
         * @return the next immutable builder instance
         */
        public CompileMessageCheckBuilder<COMPILETESTBUILDER> withLocale(Locale locale) {
            CompileMessageCheckBuilder<COMPILETESTBUILDER> nextBuilder = createNextBuilder();
            nextBuilder.locale = locale;
            return nextBuilder;
        }

        /**
         * Do check if compiler message is linked for a specific source
         *
         * @param source
         * @return the next immutable builder instance
         */
        public CompileMessageCheckBuilder<COMPILETESTBUILDER> atSource(String source) {
            CompileMessageCheckBuilder<COMPILETESTBUILDER> nextBuilder = createNextBuilder();
            nextBuilder.source = source;
            return nextBuilder;
        }

        /**
         * Check if a compiler message exists that contains the passed message token.
         * May be used for checking for message codes.
         *
         * @param expectedContainedMessageToken the message to search for
         * @return the next immutable builder instance of enclosing builder
         */
        public COMPILETESTBUILDER contains(String expectedContainedMessageToken) {
            CompileMessageCheckBuilder<COMPILETESTBUILDER> nextBuilder = createNextBuilder();

            nextBuilder.comparisionKind = CompileTestConfiguration.ComparisionKind.CONTAINS;
            nextBuilder.expectedMessage = expectedContainedMessageToken;

            CompileTestConfiguration.CompilerMessageCheck compilerMessageCheck = new CompileTestConfiguration.CompilerMessageCheck(nextBuilder.kind, nextBuilder.comparisionKind, nextBuilder.expectedMessage, nextBuilder.locale, nextBuilder.source, nextBuilder.lineNumber, nextBuilder.columnNumber);

            return compileTestBuilder.addCompilerMessageCheck(compilerMessageCheck);
        }

        /**
         * Check if a compiler message matches  the passed message string.
         *
         * @param expectedMessage the message to search for
         * @return the next immutable builder instance of enclosing builder
         */
        public COMPILETESTBUILDER isEqual(String expectedMessage) {
            CompileMessageCheckBuilder<COMPILETESTBUILDER> nextBuilder = createNextBuilder();

            nextBuilder.comparisionKind = CompileTestConfiguration.ComparisionKind.EQUALS;
            nextBuilder.expectedMessage = expectedMessage;

            CompileTestConfiguration.CompilerMessageCheck compilerMessageCheck = new CompileTestConfiguration.CompilerMessageCheck(nextBuilder.kind, nextBuilder.comparisionKind, nextBuilder.expectedMessage, nextBuilder.locale, nextBuilder.source, nextBuilder.lineNumber, nextBuilder.columnNumber);

            return compileTestBuilder.addCompilerMessageCheck(compilerMessageCheck);
        }


        /**
         * Creates the next builder instance.
         *
         * @return the next builder instance
         */
        CompileMessageCheckBuilder<COMPILETESTBUILDER> createNextBuilder() {

            CompileMessageCheckBuilder<COMPILETESTBUILDER> nextBuilder = new CompileMessageCheckBuilder<>(this.compileTestBuilder, this.kind);

            nextBuilder.kind = this.kind;
            nextBuilder.comparisionKind = this.comparisionKind;
            nextBuilder.expectedMessage = this.expectedMessage;
            nextBuilder.locale = this.locale;
            nextBuilder.source = this.source;
            nextBuilder.lineNumber = this.lineNumber;
            nextBuilder.columnNumber = this.columnNumber;

            return nextBuilder;

        }


    }


    /**
     * Internal builder class for unit and compilation tests.
     */
    private static class TestTypeBuilder {

        /**
         * Does a compilation test.
         * You can freely  choose sources to compile and processors to use.
         *
         * @return the builder
         */
        public CompilationTestBuilder compilationTest() {
            return new CompilationTestBuilder(new CompileTestConfiguration());
        }


        /**
         * Do a unit test.
         *
         * @return the UnitTestBuilder instance
         */
        public UnitTestBuilder unitTest() {
            CompileTestConfiguration compileTestConfiguration = new CompileTestConfiguration();
            compileTestConfiguration.addSourceFiles(UnitTestBuilder.getDefaultSource());
            return new UnitTestBuilder(compileTestConfiguration);
        }

    }

    /**
     * Creates a unit test builder instance.
     * <p>
     * Unit tests can be used to test methods that internally rely on the java compile time model
     * or using the tools provided by the processors processing environment.
     *
     * @return the UnitTestBuilderm instance
     */
    public static UnitTestBuilder unitTest() {
        return new TestTypeBuilder().unitTest();
    }

    /**
     * Creates a compilation test builder instance.
     * <p>
     * Compilation tests can be used for all kind of integration tests.
     * F.e. to test behavior and outcome of a processor.
     *
     * @return the CompilationTestBuilder instance
     */
    public static CompilationTestBuilder compilationTest() {
        return new TestTypeBuilder().compilationTest();
    }

}
