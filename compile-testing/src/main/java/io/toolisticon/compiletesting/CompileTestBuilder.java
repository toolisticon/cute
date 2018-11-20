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
     * Base builder class to configure message evaluations.
     *
     * @param <T>
     */
    public static class MessageEvaluation<T extends BasicBuilder<T>> {

        private final CompileTestConfiguration compileTestConfiguration;
        private final T returnInstance;

        private MessageEvaluation(T returnInstance, CompileTestConfiguration compileTestConfiguration) {

            this.compileTestConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            this.returnInstance = returnInstance;
        }

        /**
         * Adds some warning checks.
         *
         * @param warningChecks the warning checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public MessageEvaluation<T> addWarningChecks(String... warningChecks) {

            CompileTestConfiguration nextConfig = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (warningChecks != null) {
                nextConfig.addWarningMessageCheck(warningChecks);
            }
            return new MessageEvaluation<T>(returnInstance, nextConfig);

        }

        /**
         * Adds some mandatory warning checks.
         *
         * @param mandatoryWarningChecks the mandatory warning checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public MessageEvaluation<T> addMandatoryWarningChecks(String... mandatoryWarningChecks) {
            CompileTestConfiguration nextConfig = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (mandatoryWarningChecks != null) {
                compileTestConfiguration.addMandatoryWarningMessageCheck(mandatoryWarningChecks);
            }
            return new MessageEvaluation<T>(returnInstance, compileTestConfiguration);
        }

        /**
         * Adds some error checks.
         *
         * @param errorChecksToSet the error checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public MessageEvaluation<T> addErrorChecks(String... errorChecksToSet) {
            CompileTestConfiguration nextConfig = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (errorChecksToSet != null) {
                compileTestConfiguration.addErrorMessageCheck(errorChecksToSet);
            }
            return new MessageEvaluation<T>(returnInstance, compileTestConfiguration);
        }

        /**
         * Adds some notes checks.
         *
         * @param noteChecksToSet the notes checks to set, null values will be ignored.
         * @return the next builder instance
         */
        public MessageEvaluation<T> addNoteChecks(String... noteChecksToSet) {
            CompileTestConfiguration nextConfig = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            if (noteChecksToSet != null) {
                compileTestConfiguration.addNoteMessageCheck(noteChecksToSet);
            }
            return new MessageEvaluation<T>(returnInstance, compileTestConfiguration);
        }

        /**
         * Finishes message validation builder.
         *
         * @return the basic builder instance
         */
        public T finishAddMessageChecks() {
            CompileTestConfiguration nextConfig = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
            return returnInstance.createNextInstance(nextConfig);
        }

    }

    /**
     * Abstract base builder class.
     * Contains common configurations.
     *
     * @param <T>
     */
    public static abstract class BasicBuilder<T extends BasicBuilder<T>> {

        protected final CompileTestConfiguration compileTestConfiguration;

        protected BasicBuilder(CompileTestConfiguration compileTestConfiguration) {
            this.compileTestConfiguration = CompileTestConfiguration.cloneConfiguration(compileTestConfiguration);
        }

        /**
         * Compilation is expected to be successful.
         *
         * @return the next builder instance
         */
        public T compilationShouldSucceed() {
            compileTestConfiguration.setCompilationShouldSucceed(true);
            return createNextInstance(compileTestConfiguration);
        }

        /**
         * Compilation is expected to be failing.
         *
         * @return the next builder instance
         */
        public T compilationShouldFail() {
            compileTestConfiguration.setCompilationShouldSucceed(false);
            return createNextInstance(compileTestConfiguration);
        }


        public T addGeneratedFileObjectExistsCheck(JavaFileManager.Location location, String packageName, String relativeName) {
            return addGeneratedFileObjectExistsCheck(location, packageName, relativeName, (FileObject) null);
        }

        public T addGeneratedFileObjectExistsCheck(JavaFileManager.Location location, String packageName, String relativeName, FileObject expectedFileObject) {
            compileTestConfiguration.addExpectedGeneratedFileObjectCheck(location, packageName, relativeName, expectedFileObject);
            return createNextInstance(compileTestConfiguration);
        }

        public T addGeneratedFileObjectExistsCheck(JavaFileManager.Location location, String packageName, String relativeName, GeneratedFileObjectMatcher<FileObject> generatedFileObjectMatcher) {
            compileTestConfiguration.addExpectedGeneratedFileObjectCheck(location, packageName, relativeName, generatedFileObjectMatcher);
            return createNextInstance(compileTestConfiguration);
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
            compileTestConfiguration.addExpectedGeneratedJavaFileObjectCheck(location, className, kind, expectedJavaFileObject);
            return createNextInstance(compileTestConfiguration);
        }

        /**
         * Adds a check if a specific generated JavaFileObject exists.
         * Additionally checks file object matches with passed matcher.
         *
         * @param location
         * @param className
         * @param kind
         * @param generatedJavaFileObjectCheck
         * @return the next builder instance
         */
        public T addGeneratedJavaFileObjectExistsCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, GeneratedFileObjectMatcher<JavaFileObject> generatedJavaFileObjectCheck) {
            compileTestConfiguration.addExpectedGeneratedJavaFileObjectCheck(location, className, kind, generatedJavaFileObjectCheck);
            return createNextInstance(compileTestConfiguration);
        }


        /**
         * Add some message checks.
         *
         * @return A MessageEvaluation builder instance
         */
        public MessageEvaluation<T> addMessageChecks() {
            return new MessageEvaluation<T>(createNextInstance(compileTestConfiguration), compileTestConfiguration);
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
         * Adds processors
         *
         * @param processors
         * @return
         */
        public CompileTimeTestBuilder useProcessors(Processor... processors) {
            if (processors != null) {
                compileTestConfiguration.addProcessors(processors);
            }
            return createNextInstance(compileTestConfiguration);
        }

        public CompileTimeTestBuilder useProcessorAndExpectException(Processor processor, Class<? extends Throwable> exception) {
            if (processor == null) {
                throw new IllegalArgumentException("Passed processor must not be null");
            }
            if (exception == null) {
                throw new IllegalArgumentException("Passed exception must not be null");
            }
            compileTestConfiguration.addProcessorWithExpectedException(processor, exception);
            return createNextInstance(compileTestConfiguration);
        }

        public CompileTimeTestBuilder addSources(JavaFileObject... sources) {
            if (sources != null) {
                compileTestConfiguration.addSourceFiles(sources);
            }
            return createNextInstance(compileTestConfiguration);
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

            // remove existing processor
            compileTestConfiguration.getProcessors().clear();
            compileTestConfiguration.addProcessors(processor);

            return createNextInstance(compileTestConfiguration);
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

            // remove existing processor
            compileTestConfiguration.getProcessors().clear();
            compileTestConfiguration.addProcessors(new UnitTestAnnotationProcessorClass(unitTestProcessor));

            return createNextInstance(compileTestConfiguration);
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

            // clear existing sources
            compileTestConfiguration.getSourceFiles().clear();
            compileTestConfiguration.addSourceFiles(source);

            return createNextInstance(compileTestConfiguration);
        }

        /**
         * Sets an expected exception thrown in the unit test case.
         */
        public UnitTestBuilder expectedThrownException(Class<? extends Throwable> expectedException) {
            if (expectedException != null) {
                compileTestConfiguration.setExpectedThrownException(expectedException);
            }
            return createNextInstance(compileTestConfiguration);
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
