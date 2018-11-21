package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;

import javax.annotation.processing.Processor;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The compile test configuration to use for compilation test.
 */
public class CompileTestConfiguration {

    public static class ProcessorWithExpectedException {

        private final Processor processor;
        private final Class<? extends Throwable> throwable;

        public ProcessorWithExpectedException(Processor processor, Class<? extends Throwable> throwable) {
            this.processor = processor;
            this.throwable = throwable;
        }

        public Processor getProcessor() {
            return processor;
        }

        public Class<? extends Throwable> getThrowable() {
            return throwable;
        }
    }

    public static class GeneratedJavaFileObjectCheck {
        private final JavaFileManager.Location location;
        private final String className;
        private final JavaFileObject.Kind kind;

        private final JavaFileObject expectedJavaFileObject;
        private final GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher;

        private GeneratedJavaFileObjectCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, JavaFileObject expectedJavaFileObject, GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher) {

            this.location = location;
            this.className = className;
            this.kind = kind;

            this.expectedJavaFileObject = expectedJavaFileObject;
            this.generatedFileObjectMatcher = generatedFileObjectMatcher;

        }


        public GeneratedJavaFileObjectCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, JavaFileObject expectedJavaFileObject) {
            this(location, className, kind, expectedJavaFileObject, null);
        }

        public GeneratedJavaFileObjectCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher) {
            this(location, className, kind, null, generatedFileObjectMatcher);
        }


        public JavaFileManager.Location getLocation() {
            return location;
        }

        public String getClassName() {
            return className;
        }

        public JavaFileObject.Kind getKind() {
            return kind;
        }

        public JavaFileObject getExpectedJavaFileObject() {
            return expectedJavaFileObject;
        }

        public GeneratedFileObjectMatcher<JavaFileObject> getGeneratedFileObjectMatcher() {
            return generatedFileObjectMatcher;
        }

    }


    public static class GeneratedFileObjectCheck {
        private final JavaFileManager.Location location;
        private final String packageName;
        private final String relativeName;

        private final FileObject expectedFileObject;
        private final GeneratedFileObjectMatcher<FileObject> generatedFileObjectMatcher;

        private GeneratedFileObjectCheck(JavaFileManager.Location location, String packageName, String relativeName, FileObject expectedFileObject, GeneratedFileObjectMatcher<FileObject> generatedFileObjectMatcher) {

            this.location = location;
            this.packageName = packageName;
            this.relativeName = relativeName;

            this.expectedFileObject = expectedFileObject;
            this.generatedFileObjectMatcher = generatedFileObjectMatcher;

        }


        public GeneratedFileObjectCheck(JavaFileManager.Location location, String packageName, String relativeName, FileObject expectedFileObject) {
            this(location, packageName, relativeName, expectedFileObject, null);
        }

        public GeneratedFileObjectCheck(JavaFileManager.Location location, String packageName, String relativeName, GeneratedFileObjectMatcher<FileObject> generatedFileObjectMatcher) {
            this(location, packageName, relativeName, null, generatedFileObjectMatcher);
        }


        public JavaFileManager.Location getLocation() {
            return location;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getRelativeName() {
            return relativeName;
        }

        public FileObject getExpectedFileObject() {
            return expectedFileObject;
        }

        public GeneratedFileObjectMatcher<FileObject> getGeneratedFileObjectMatcher() {
            return generatedFileObjectMatcher;
        }

    }

    /**
     * The source files to use.
     */
    private final Set<JavaFileObject> sourceFiles = new HashSet<JavaFileObject>();
    /**
     * The processors to use.
     */
    private final Set<Processor> processors = new HashSet<Processor>();
    /**
     * The processors to use with an expected exception raised by this specific processor.
     */
    private final Set<ProcessorWithExpectedException> processorsWithExpectedExceptions = new HashSet<ProcessorWithExpectedException>();

    /**
     * Global processor independant expected exceptions.
     */
    private Class<? extends Throwable> expectedThrownException = null;

    /**
     * Compilation succeeded or not
     */
    private Boolean compilationShouldSucceed;

    // message checks by severity
    private final Set<String> warningMessageCheck = new HashSet<String>();
    private final Set<String> mandatoryWarningMessageCheck = new HashSet<String>();
    private final Set<String> errorMessageCheck = new HashSet<String>();
    private final Set<String> noteMessageCheck = new HashSet<String>();


    /**
     * Checks for generated JavaFileObjects.
     */
    private final Set<GeneratedJavaFileObjectCheck> generatedJavaFileObjectChecks = new HashSet<GeneratedJavaFileObjectCheck>();
    /**
     * Checks for generated FileObjects.
     */
    private final Set<GeneratedFileObjectCheck> generatedFileObjectChecks = new HashSet<GeneratedFileObjectCheck>();

    /**
     * Noarg constructor.
     */
    public CompileTestConfiguration() {

    }

    /**
     * Clone constructor.
     */
    protected CompileTestConfiguration(CompileTestConfiguration source) {

        this.sourceFiles.addAll(source.getSourceFiles());
        this.processors.addAll(source.getProcessors());
        this.processorsWithExpectedExceptions.addAll(source.processorsWithExpectedExceptions);
        this.expectedThrownException = source.getExpectedThrownException();

        this.compilationShouldSucceed = source.getCompilationShouldSucceed();
        this.warningMessageCheck.addAll(source.getWarningMessageCheck());
        this.mandatoryWarningMessageCheck.addAll(source.getMandatoryWarningMessageCheck());
        this.noteMessageCheck.addAll(source.getNoteMessageCheck());
        this.errorMessageCheck.addAll(source.getErrorMessageCheck());

        this.generatedJavaFileObjectChecks.addAll(source.getGeneratedJavaFileObjectChecks());
        this.generatedFileObjectChecks.addAll(source.getGeneratedFileObjectChecks());

    }


    public Boolean getCompilationShouldSucceed() {
        return compilationShouldSucceed;
    }

    public void setCompilationShouldSucceed(Boolean compilationShouldSucceed) {
        this.compilationShouldSucceed = compilationShouldSucceed;
    }

    public void addSourceFiles(JavaFileObject... sourceFiles) {
        if (sourceFiles != null) {
            this.sourceFiles.addAll(Arrays.asList(sourceFiles));
            this.sourceFiles.remove(null);
        }
    }

    public void addProcessors(Processor... processors) {
        if (processors != null) {
            this.processors.addAll(Arrays.asList(processors));
            this.processors.remove(null);
        }
    }

    public void addProcessorWithExpectedException(Processor processors, Class<? extends Throwable> e) {
        this.processorsWithExpectedExceptions.add(new ProcessorWithExpectedException(processors, e));
    }

    public void addWarningMessageCheck(String... warningMessage) {
        if (warningMessage != null) {
            this.warningMessageCheck.addAll(Arrays.asList(warningMessage));
            this.warningMessageCheck.remove(null);
        }
    }

    public void addMandatoryWarningMessageCheck(String... mandatoryWarningMessage) {
        if (mandatoryWarningMessage != null) {
            this.mandatoryWarningMessageCheck.addAll(Arrays.asList(mandatoryWarningMessage));
            this.mandatoryWarningMessageCheck.remove(null);
        }
    }

    public void addErrorMessageCheck(String... errorMessage) {
        if (errorMessage != null) {
            this.errorMessageCheck.addAll(Arrays.asList(errorMessage));
            this.errorMessageCheck.remove(null);
        }
    }

    public void addNoteMessageCheck(String... noteMessage) {
        if (noteMessage != null) {
            this.noteMessageCheck.addAll(Arrays.asList(noteMessage));
            this.noteMessageCheck.remove(null);
        }
    }


    public void addExpectedGeneratedJavaFileObjectCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, JavaFileObject javaFileObject) {
        this.generatedJavaFileObjectChecks.add(new GeneratedJavaFileObjectCheck(location, className, kind, javaFileObject));
    }

    public void addExpectedGeneratedJavaFileObjectCheck(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher) {
        this.generatedJavaFileObjectChecks.add(new GeneratedJavaFileObjectCheck(location, className, kind, generatedFileObjectMatcher));
    }

    public void addExpectedGeneratedFileObjectCheck(JavaFileManager.Location location, String packageName, String relativeName, FileObject javaFileObject) {
        this.generatedFileObjectChecks.add(new GeneratedFileObjectCheck(location, packageName, relativeName, javaFileObject));
    }

    public void addExpectedGeneratedFileObjectCheck(JavaFileManager.Location location, String packageName, String relativeName, GeneratedFileObjectMatcher<FileObject> generatedFileObjectMatcher) {
        this.generatedFileObjectChecks.add(new GeneratedFileObjectCheck(location, packageName, relativeName, generatedFileObjectMatcher));
    }

    public void setExpectedThrownException(Class<? extends Throwable> expectedThrownException) {
        this.expectedThrownException = expectedThrownException;
    }

    public Set<JavaFileObject> getSourceFiles() {
        return sourceFiles;
    }

    public Set<Processor> getProcessors() {
        return processors;
    }

    public Set<ProcessorWithExpectedException> getProcessorsWithExpectedExceptions() {
        return processorsWithExpectedExceptions;
    }

    public Set<AnnotationProcessorWrapper> getWrappedProcessors() {

        Set<AnnotationProcessorWrapper> wrappedProcessors = new HashSet<AnnotationProcessorWrapper>();

        for (Processor processor : this.processors) {

            if (this.expectedThrownException != null) {
                wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, expectedThrownException));
            } else {
                wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, expectedThrownException));
            }

        }

        for (ProcessorWithExpectedException processor : this.processorsWithExpectedExceptions) {

            wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor.processor, processor.throwable != null ? processor.throwable : expectedThrownException));

        }

        return wrappedProcessors;

    }

    public Set<String> getWarningMessageCheck() {
        return warningMessageCheck;
    }

    public Set<String> getMandatoryWarningMessageCheck() {
        return mandatoryWarningMessageCheck;
    }

    public Set<String> getErrorMessageCheck() {
        return errorMessageCheck;
    }

    public Set<String> getNoteMessageCheck() {
        return noteMessageCheck;
    }

    public Set<GeneratedJavaFileObjectCheck> getGeneratedJavaFileObjectChecks() {
        return generatedJavaFileObjectChecks;
    }

    public Set<GeneratedFileObjectCheck> getGeneratedFileObjectChecks() {
        return generatedFileObjectChecks;
    }

    public Class<? extends Throwable> getExpectedThrownException() {
        return expectedThrownException;
    }

    public static CompileTestConfiguration cloneConfiguration(CompileTestConfiguration compileTestConfiguration) {
        return new CompileTestConfiguration(compileTestConfiguration);
    }

}
