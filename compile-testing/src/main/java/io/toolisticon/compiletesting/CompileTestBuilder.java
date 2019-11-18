package io.toolisticon.compiletesting;

import io.toolisticon.compiletesting.impl.CompileTest;
import io.toolisticon.compiletesting.impl.CompileTestConfiguration;

import javax.annotation.processing.Processor;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

/**
 * Compile test builder.
 * Implemented with immutable state / configuration, so it's safe to use this instance f.e. in unit test before classes.
 */
public class CompileTestBuilder {


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
         * Adds some warning checks.
         *
         * @param warningChecks the warning checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public T expectedWarningMessages(String... warningChecks) {
            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (warningChecks != null) {
                nextConfiguration.addWarningMessageCheck(warningChecks);
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
                nextConfiguration.addMandatoryWarningMessageCheck(mandatoryWarningChecks);
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
                nextConfiguration.addErrorMessageCheck(errorChecksToSet);
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
                nextConfiguration.addNoteMessageCheck(noteChecksToSet);
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
         * Adds a check if a specific generated FileObject exists.
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

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addGeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, location, packageName, relativeName, expectedFileObject);
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
        public T expectedFileObjectExists(
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
            nextConfiguration.addGeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.DOESNT_EXIST, location, packageName, relativeName, (FileObject) null);
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
         * Checks if a generated source file exists.
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
         * @param className                    the class name
         * @return the next builder instance
         */
        public T expectedClassFileExists(String className) {
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
                JavaFileObject expectedJavaFileObject) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addGeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, location, className, kind, expectedJavaFileObject);
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
            nextConfiguration.addGeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.DOESNT_EXIST, location, className, kind, (JavaFileObject) null);

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
                throw new IllegalArgumentException("At least one source file has to be added to the compiler test configuration");
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
        public CompilationTestBuilder addProcessors(Class<? extends Processor>... processorTypes) {

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
                throw new IllegalArgumentException("Passed processor must not be null");
            }
            if (exception == null) {
                throw new IllegalArgumentException("Passed exception must not be null");
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
        public CompilationTestBuilder addSources(JavaFileObject... sources) {

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
        public CompilationTestBuilder addSources(String... sources) {

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
                throw new IllegalArgumentException("passed processor must not be null!");
            }

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);

            // remove existing processor
            nextConfiguration.getProcessors().clear();
            nextConfiguration.addProcessors(processor);

            return createNextInstance(nextConfiguration);
        }

        /**
         * Sets the processor to use.
         * The processor should support {@link TestAnnotation} annotation processing if no custom source file is defined.
         * If custom source is used make sure {@link TestAnnotation} is used somewhere in the custom source file to make sure if annotation processor is used.
         *
         * @param unitTestProcessor the processor to use
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         */
        public UnitTestBuilder useProcessor(UnitTestProcessor unitTestProcessor) {

            if (unitTestProcessor == null) {
                throw new IllegalArgumentException("passed unitTestProcessor must not be null!");
            }

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);

            // remove existing processor
            nextConfiguration.getProcessors().clear();
            nextConfiguration.addProcessors(new UnitTestAnnotationProcessorClass(unitTestProcessor));

            return createNextInstance(nextConfiguration);
        }

        /**
         * Sets the processor to use.
         * The processor should support {@link TestAnnotation} annotation processing if no custom source file is defined.
         * If custom source is used make sure {@link TestAnnotation} is used somewhere in the custom source file to make sure if annotation processor is used.
         *
         * @param processorUnderTestClass                         the Processor which should be provided as a
         * @param unitTestProcessorForTestingAnnotationProcessors the processor to use
         * @param <PROCESSOR_UNDER_TEST>                          The processor type under test
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         */

        public <PROCESSOR_UNDER_TEST extends Processor> UnitTestBuilder useProcessor(Class<PROCESSOR_UNDER_TEST> processorUnderTestClass, UnitTestProcessorForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST> unitTestProcessorForTestingAnnotationProcessors) {

            if (unitTestProcessorForTestingAnnotationProcessors == null) {
                throw new IllegalArgumentException("passed unitTestProcessorForTestingAnnotationProcessors must not be null!");
            }

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);

            PROCESSOR_UNDER_TEST processorUnderTest = null;

            try {
                processorUnderTest = processorUnderTestClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("useProcessor: Passed processor class under test " + (processorUnderTestClass == null ? "<NULL>" : processorUnderTestClass.getCanonicalName()) + " cannot be instanciated.");
            }


            // remove existing processor
            nextConfiguration.getProcessors().clear();
            nextConfiguration.addProcessors(new UnitTestAnnotationProcessorClassForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST>(processorUnderTest, unitTestProcessorForTestingAnnotationProcessors));

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
                throw new IllegalArgumentException("passed source file must not be null!");
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
                throw new IllegalArgumentException("At least one processor has to be added to the compiler test configuration");
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
            return JavaFileObjectUtils.readFromResource("/AnnotationProcessorUnitTestClass.java");
        }

        /**
         * {@inheritDoc}
         */
        protected UnitTestBuilder createNextInstance(CompileTestConfiguration compileTestConfiguration) {
            return new UnitTestBuilder(compileTestConfiguration);
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
