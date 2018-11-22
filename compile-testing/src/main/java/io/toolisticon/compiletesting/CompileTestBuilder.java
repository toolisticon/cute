package io.toolisticon.compiletesting;

import io.toolisticon.compiletesting.impl.CompileTest;
import io.toolisticon.compiletesting.impl.CompileTestConfiguration;
import io.toolisticon.compiletesting.impl.UnitTestAnnotationProcessorClass;

import javax.annotation.processing.Processor;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

/**
 * Compile test builder.
 * Implemented with immutable state / configuration, so it's safe to use this instance f.e. in unit test before classes.
 */
public class CompileTestBuilder {


    /**
     * Abstract base builder class.
     * Contains common configurations.
     *
     * @param <T>
     */
    public static abstract class BasicBuilder<T extends BasicBuilder<T>> {

        protected final CompileTestConfiguration compileTestConfiguration;

        protected BasicBuilder(CompileTestConfiguration compileTestConfiguration) {
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
         * Adds some warning checks.
         *
         * @param warningChecks the warning checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public T addWarningChecks(String... warningChecks) {
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
        public T addMandatoryWarningChecks(String... mandatoryWarningChecks) {

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
        public T addErrorChecks(String... errorChecksToSet) {

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
        public T addNoteChecks(String... noteChecksToSet) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (noteChecksToSet != null) {
                nextConfiguration.addNoteMessageCheck(noteChecksToSet);
            }
            return createNextInstance(nextConfiguration);

        }


        /**
         * Adds a check if a specific generated FileObject exists.
         *
         * @param location
         * @param packageName
         * @param relativeName
         * @return the next builder instance
         */
        public T addGeneratedFileObjectExistsCheck(JavaFileManager.Location location, String packageName, String relativeName) {
            return addGeneratedFileObjectExistsCheck(location, packageName, relativeName, (FileObject) null);
        }

        /**
         * Adds a check if a specific generated FileObject exists.
         * Additionally checks if files are equal if passed expectedFileObject is not null.
         *
         * @param location
         * @param packageName
         * @param relativeName
         * @param expectedFileObject
         * @return the next builder instance
         */
        public T addGeneratedFileObjectExistsCheck(JavaFileManager.Location location, String packageName, String relativeName, FileObject expectedFileObject) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addExpectedGeneratedFileObjectCheck(location, packageName, relativeName, expectedFileObject);
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds a check if a specific generated FileObject exists.
         * Additionally checks if file object matches with passed matcher.
         *
         * @param location
         * @param packageName
         * @param relativeName
         * @param generatedFileObjectMatcher
         * @return the next builder instance
         */
        public T addGeneratedFileObjectExistsCheck(JavaFileManager.Location location, String packageName, String relativeName, GeneratedFileObjectMatcher<FileObject> generatedFileObjectMatcher) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addExpectedGeneratedFileObjectCheck(location, packageName, relativeName, generatedFileObjectMatcher);
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds a check if a specific generated JavaFileObject exists.
         *
         * @param location
         * @param className
         * @param kind
         * @return the next builder instance
         */
        public T addGeneratedJavaFileObjectExistsCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) {
            return addGeneratedJavaFileObjectExistsCheck(location, className, kind, (JavaFileObject) null);
        }

        /**
         * Adds a check if a specific generated JavaFileObject exists.
         * Additionally checks if files are equal if passed expectedJavaFileObject is not null.
         *
         * @param location
         * @param className
         * @param kind
         * @param expectedJavaFileObject
         * @return the next builder instance
         */
        public T addGeneratedJavaFileObjectExistsCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, JavaFileObject expectedJavaFileObject) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addExpectedGeneratedJavaFileObjectCheck(location, className, kind, expectedJavaFileObject);
            return createNextInstance(nextConfiguration);

        }

        /**
         * Adds a check if a specific generated JavaFileObject exists.
         * Additionally checks if java file object matches with passed matcher.
         *
         * @param location
         * @param className
         * @param kind
         * @param generatedJavaFileObjectCheck
         * @return the next builder instance
         */
        public T addGeneratedJavaFileObjectExistsCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, GeneratedFileObjectMatcher<JavaFileObject> generatedJavaFileObjectCheck) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            nextConfiguration.addExpectedGeneratedJavaFileObjectCheck(location, className, kind, generatedJavaFileObjectCheck);
            return createNextInstance(nextConfiguration);

        }


        /**
         * Created the compile test configuration instance.
         *
         * @return the configuration instance
         */
        protected CompileTestConfiguration createCompileTestConfiguration() {
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
    public static class CompileTimeTestBuilder extends BasicBuilder<CompileTimeTestBuilder> {

        /**
         * Forwarding constructor.
         * Clones passed compileTestConfiguration.
         *
         * @param compileTestConfiguration
         */
        private CompileTimeTestBuilder(CompileTestConfiguration compileTestConfiguration) {
            super(compileTestConfiguration);
        }

        /**
         * Adds processors.
         *
         * @param processorTypes
         * @return
         */
        public CompileTimeTestBuilder useProcessors(Class<? extends Processor>... processorTypes) {

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
         * @param processor
         * @param exception
         * @return
         */
        public CompileTimeTestBuilder useProcessorAndExpectException(Class<? extends Processor> processor, Class<? extends Throwable> exception) {

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

        public CompileTimeTestBuilder addSources(JavaFileObject... sources) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (sources != null) {
                nextConfiguration.addSourceFiles(sources);
            }
            return createNextInstance(nextConfiguration);

        }

        protected CompileTimeTestBuilder createNextInstance(CompileTestConfiguration compileTestConfiguration) {
            return new CompileTimeTestBuilder(compileTestConfiguration);
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
         * Sets an expected exception thrown in the unit test case.
         */
        public UnitTestBuilder expectedThrownException(Class<? extends Throwable> expectedException) {

            CompileTestConfiguration nextConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (expectedException != null) {
                nextConfiguration.setExpectedThrownException(expectedException);
            }
            return createNextInstance(nextConfiguration);

        }


        public void testCompilation() {

            if (compileTestConfiguration.getProcessors().size() == 0) {
                throw new IllegalArgumentException("At least one processor has to be added to the compiler test configuration");
            }

            super.testCompilation();

        }

        /**
         * Internal constructor
         *
         * @param compileTestConfiguration
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

        protected UnitTestBuilder createNextInstance(CompileTestConfiguration compileTestConfiguration) {
            return new UnitTestBuilder(compileTestConfiguration);
        }

    }

    public static class TestTypeBuilder {

        /**
         * Does a compilation test.
         * You can freely  choose sources to compile and processors to use.
         *
         * @return the builder
         */
        public CompileTimeTestBuilder compilationTest() {
            return new CompileTimeTestBuilder(new CompileTestConfiguration());
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


    public static TestTypeBuilder createCompileTestBuilder() {
        return new TestTypeBuilder();
    }

}
