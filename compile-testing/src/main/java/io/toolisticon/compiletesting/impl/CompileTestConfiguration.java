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

        private final Class<? extends Processor> processorType;
        private final Class<? extends Throwable> throwable;

        public ProcessorWithExpectedException(Class<? extends Processor> processorType, Class<? extends Throwable> throwable) {
            this.processorType = processorType;
            this.throwable = throwable;
        }

        public Class<? extends Processor> getProcessorType() {
            return processorType;
        }

        public Class<? extends Throwable> getThrowable() {
            return throwable;
        }

        @Override
        public int hashCode() {
            return (processorType != null ? processorType.hashCode() : 0)
                    + (throwable != null ? throwable.hashCode() : 0);
        }

        @Override
        public boolean equals(Object obj) {

            if (obj != null && ProcessorWithExpectedException.class.isAssignableFrom(obj.getClass())) {

                ProcessorWithExpectedException otherObj = (ProcessorWithExpectedException) obj;

                // compare processorType
                if ((this.getProcessorType() == null && otherObj.getProcessorType() != null)
                        || (this.getProcessorType() != null && otherObj.getProcessorType() == null)) {

                    return false;

                } else if ((this.getProcessorType() != null && otherObj.getProcessorType() != null)) {
                    if (!this.getProcessorType().equals(otherObj.getProcessorType())) {
                        return false;
                    }
                }

                // compare throwable
                if ((this.getThrowable() == null && otherObj.getThrowable() != null)
                        || (this.getThrowable() != null && otherObj.getThrowable() == null)) {

                    return false;

                } else if ((this.getThrowable() != null && otherObj.getThrowable() != null)) {
                    if (!this.getThrowable().equals(otherObj.getThrowable())) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        @Override
        public String toString() {
            return "ProcessorWithExpectedException{" +
                    "\n\t\tprocessorType=" + processorType +
                    ",\n\t\t throwable=" + throwable +
                    "\n\t" +
                    '}';
        }
    }

    public enum FileObjectCheckType {
        EXISTS,
        DOESNT_EXIST
    }

    public static class GeneratedJavaFileObjectCheck {

        private final FileObjectCheckType checkType;
        private final JavaFileManager.Location location;
        private final String className;
        private final JavaFileObject.Kind kind;

        private final JavaFileObject expectedJavaFileObject;
        private final GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher;

        private GeneratedJavaFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String className, JavaFileObject.Kind kind, JavaFileObject expectedJavaFileObject, GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher) {

            this.checkType = checkType;
            this.location = location;
            this.className = className;
            this.kind = kind;

            this.expectedJavaFileObject = expectedJavaFileObject;
            this.generatedFileObjectMatcher = generatedFileObjectMatcher;

        }


        public GeneratedJavaFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String className, JavaFileObject.Kind kind, JavaFileObject expectedJavaFileObject) {
            this(checkType, location, className, kind, expectedJavaFileObject, null);
        }

        public GeneratedJavaFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String className, JavaFileObject.Kind kind, GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher) {
            this(checkType, location, className, kind, null, generatedFileObjectMatcher);
        }

        public FileObjectCheckType getCheckType() {
            return checkType;
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

        @Override
        public int hashCode() {
            return (checkType != null ? checkType.hashCode() : 0)
                    + (location != null ? location.hashCode() : 0)
                    + (className != null ? className.hashCode() : 0)
                    + (kind != null ? kind.hashCode() : 0)
                    + (expectedJavaFileObject != null ? expectedJavaFileObject.hashCode() : 0)
                    + (generatedFileObjectMatcher != null ? generatedFileObjectMatcher.hashCode() : 0);
        }

        @Override
        public boolean equals(Object obj) {

            if (obj != null && GeneratedJavaFileObjectCheck.class.isAssignableFrom(obj.getClass())) {

                GeneratedJavaFileObjectCheck otherObj = (GeneratedJavaFileObjectCheck) obj;

                // compare checkType
                if ((this.getCheckType() == null && otherObj.getCheckType() != null)
                        || (this.getCheckType() != null && otherObj.getCheckType() == null)) {

                    return false;

                } else if ((this.getCheckType() != null && otherObj.getCheckType() != null)) {
                    if (!this.getCheckType().equals(otherObj.getCheckType())) {
                        return false;
                    }
                }

                // compare location
                if ((this.getLocation() == null && otherObj.getLocation() != null)
                        || (this.getLocation() != null && otherObj.getLocation() == null)) {

                    return false;

                } else if ((this.getLocation() != null && otherObj.getLocation() != null)) {
                    if (!this.getLocation().equals(otherObj.getLocation())) {
                        return false;
                    }
                }

                // compare className
                if ((this.getClassName() == null && otherObj.getClassName() != null)
                        || (this.getClassName() != null && otherObj.getClassName() == null)) {

                    return false;

                } else if ((this.getClassName() != null && otherObj.getClassName() != null)) {
                    if (!this.getClassName().equals(otherObj.getClassName())) {
                        return false;
                    }
                }

                // compare kind
                if ((this.getKind() == null && otherObj.getKind() != null)
                        || (this.getKind() != null && otherObj.getKind() == null)) {

                    return false;

                } else if ((this.getKind() != null && otherObj.getKind() != null)) {
                    if (!this.getKind().equals(otherObj.getKind())) {
                        return false;
                    }
                }

                // compare expectedJavaFileObject
                if ((this.getExpectedJavaFileObject() == null && otherObj.getExpectedJavaFileObject() != null)
                        || (this.getExpectedJavaFileObject() != null && otherObj.getExpectedJavaFileObject() == null)) {

                    return false;

                } else if ((this.getExpectedJavaFileObject() != null && otherObj.getExpectedJavaFileObject() != null)) {
                    if (!this.getExpectedJavaFileObject().equals(otherObj.getExpectedJavaFileObject())) {
                        return false;
                    }
                }

                // compare generatedFileObjectMatchers
                if ((this.getGeneratedFileObjectMatcher() == null && otherObj.getGeneratedFileObjectMatcher() != null)
                        || (this.getGeneratedFileObjectMatcher() != null && otherObj.getGeneratedFileObjectMatcher() == null)) {

                    return false;

                } else if ((this.getGeneratedFileObjectMatcher() != null && otherObj.getGeneratedFileObjectMatcher() != null)) {
                    if (!this.getGeneratedFileObjectMatcher().equals(otherObj.getGeneratedFileObjectMatcher())) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }


        @Override
        public String toString() {
            return "GeneratedJavaFileObjectCheck{" +
                    "\n\t\tcheckType=" + checkType.name() +
                    ",\n\t\tlocation=" + location +
                    ",\n\t\t className='" + className + '\'' +
                    ",\n\t\t kind=" + kind +
                    ",\n\t\t expectedJavaFileObject=" + expectedJavaFileObject +
                    ",\n\t\t generatedFileObjectMatchers=" + generatedFileObjectMatcher +
                    "\n\t" +
                    '}';
        }
    }


    public static class GeneratedFileObjectCheck {

        private final FileObjectCheckType checkType;
        private final JavaFileManager.Location location;
        private final String packageName;
        private final String relativeName;

        // either expectedFileObject or generatedFileObjectMatchers may be set not null
        private final FileObject expectedFileObject;
        private final GeneratedFileObjectMatcher<FileObject>[] generatedFileObjectMatchers;

        private GeneratedFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String packageName, String relativeName, FileObject expectedFileObject, GeneratedFileObjectMatcher<FileObject>[] generatedFileObjectMatchers) {

            this.checkType = checkType;
            this.location = location;
            this.packageName = packageName;
            this.relativeName = relativeName;

            this.expectedFileObject = expectedFileObject;
            this.generatedFileObjectMatchers = generatedFileObjectMatchers;

        }


        public GeneratedFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String packageName, String relativeName, FileObject expectedFileObject) {
            this(checkType, location, packageName, relativeName, expectedFileObject, null);
        }

        public GeneratedFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String packageName, String relativeName, GeneratedFileObjectMatcher<FileObject>[] generatedFileObjectMatchers) {
            this(checkType, location, packageName, relativeName, null, generatedFileObjectMatchers);
        }

        public FileObjectCheckType getCheckType() {
            return checkType;
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

        public GeneratedFileObjectMatcher<FileObject>[] getGeneratedFileObjectMatchers() {
            return generatedFileObjectMatchers;
        }

        @Override
        public int hashCode() {
            return (checkType != null ? checkType.hashCode() : 0)
                    + (location != null ? location.hashCode() : 0)
                    + (packageName != null ? packageName.hashCode() : 0)
                    + (relativeName != null ? relativeName.hashCode() : 0)
                    + (expectedFileObject != null ? expectedFileObject.hashCode() : 0)
                    + (generatedFileObjectMatchers != null ? generatedFileObjectMatchers.hashCode() : 0);
        }

        @Override
        public boolean equals(Object obj) {

            if (obj != null && GeneratedFileObjectCheck.class.isAssignableFrom(obj.getClass())) {

                GeneratedFileObjectCheck otherObj = (GeneratedFileObjectCheck) obj;

                // compare checkType
                if ((this.getCheckType() == null && otherObj.getCheckType() != null)
                        || (this.getCheckType() != null && otherObj.getCheckType() == null)) {

                    return false;

                } else if ((this.getCheckType() != null && otherObj.getCheckType() != null)) {
                    if (!this.getCheckType().equals(otherObj.getCheckType())) {
                        return false;
                    }
                }

                // compare location
                if ((this.getLocation() == null && otherObj.getLocation() != null)
                        || (this.getLocation() != null && otherObj.getLocation() == null)) {

                    return false;

                } else if ((this.getLocation() != null && otherObj.getLocation() != null)) {
                    if (!this.getLocation().equals(otherObj.getLocation())) {
                        return false;
                    }
                }

                // compare packageName
                if ((this.getPackageName() == null && otherObj.getPackageName() != null)
                        || (this.getPackageName() != null && otherObj.getPackageName() == null)) {

                    return false;

                } else if ((this.getPackageName() != null && otherObj.getPackageName() != null)) {
                    if (!this.getPackageName().equals(otherObj.getPackageName())) {
                        return false;
                    }
                }

                // compare relativeName
                if ((this.getRelativeName() == null && otherObj.getRelativeName() != null)
                        || (this.getRelativeName() != null && otherObj.getRelativeName() == null)) {

                    return false;

                } else if ((this.getRelativeName() != null && otherObj.getRelativeName() != null)) {
                    if (!this.getRelativeName().equals(otherObj.getRelativeName())) {
                        return false;
                    }
                }

                // compare expectedFileObject
                if ((this.getExpectedFileObject() == null && otherObj.getExpectedFileObject() != null)
                        || (this.getExpectedFileObject() != null && otherObj.getExpectedFileObject() == null)) {

                    return false;

                } else if ((this.getExpectedFileObject() != null && otherObj.getExpectedFileObject() != null)) {
                    if (!this.getExpectedFileObject().equals(otherObj.getExpectedFileObject())) {
                        return false;
                    }
                }

                // compare generatedFileObjectMatchers
                if ((this.getGeneratedFileObjectMatchers() == null && otherObj.getGeneratedFileObjectMatchers() != null)
                        || (this.getGeneratedFileObjectMatchers() != null && otherObj.getGeneratedFileObjectMatchers() == null)) {

                    return false;

                } else if ((this.getGeneratedFileObjectMatchers() != null && otherObj.getGeneratedFileObjectMatchers() != null)) {
                    if (!this.getGeneratedFileObjectMatchers().equals(otherObj.getGeneratedFileObjectMatchers())) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        @Override
        public String toString() {
            return "GeneratedFileObjectCheck{" +
                    "\n\t\tcheckType=" + checkType.name() +
                    ",\n\t\tlocation=" + location +
                    ",\n\t\t packageName='" + packageName + '\'' +
                    ",\n\t\t relativeName='" + relativeName + '\'' +
                    ",\n\t\t expectedFileObject=" + expectedFileObject +
                    ",\n\t\t generatedFileObjectMatchers=" + generatedFileObjectMatchers +
                    "\n\t" +
                    '}';
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
     * The processor types to use.
     */
    private final Set<Class<? extends Processor>> processorTypes = new HashSet<Class<? extends Processor>>();

    /**
     * The processors to use with an expected exception raised by this specific processor.
     */
    private final Set<ProcessorWithExpectedException> processorsWithExpectedExceptions = new HashSet<ProcessorWithExpectedException>();

    /**
     * This is a cache for all wrapped processors and must be reset after processors are added.
     */
    private Set<AnnotationProcessorWrapper> wrappedProcessors = null;

    /**
     * Global processor independant expected exceptions.
     */
    private Class<? extends Throwable> expectedThrownException = null;

    /**
     * Modules used in Java >= 9 environments
     */
    private Set<String> modules = null;

    /**
     * Compilation succeeded or not
     */
    private Boolean compilationShouldSucceed;

    // message checks by severity
    private final Set<String> noteMessageCheck = new HashSet<String>();
    private final Set<String> warningMessageCheck = new HashSet<String>();
    private final Set<String> mandatoryWarningMessageCheck = new HashSet<String>();
    private final Set<String> errorMessageCheck = new HashSet<String>();


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
     *
     * @param source the source configuration to clone froms
     */
    CompileTestConfiguration(CompileTestConfiguration source) {

        this.sourceFiles.addAll(source.getSourceFiles());
        this.processors.addAll(source.getProcessors());
        this.processorTypes.addAll(source.getProcessorTypes());
        this.processorsWithExpectedExceptions.addAll(source.processorsWithExpectedExceptions);
        this.expectedThrownException = source.getExpectedThrownException();

        if (source.getModules() != null) {
            this.modules = new HashSet<String>();
            this.modules.addAll(source.getModules());
        } else {
            this.modules = null;
        }

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

    /**
     * This method should only be used for unit compile tests.
     * Sharing instance between test runs can cause undeterministic behavior.
     *
     * @param processors the processors to use
     */
    public void addProcessors(Processor... processors) {

        // reset cache
        this.wrappedProcessors = null;

        if (processors != null) {
            this.processors.addAll(Arrays.asList(processors));
            this.processors.remove(null);
        }
    }

    public void addProcessorTypes(Class<? extends Processor>... processorTypes) {

        // reset cache
        this.wrappedProcessors = null;

        if (processorTypes != null) {
            this.processorTypes.addAll(Arrays.asList(processorTypes));
            this.processorTypes.remove(null);
        }
    }

    public void addProcessorWithExpectedException(Class<? extends Processor> processorType, Class<? extends Throwable> e) {

        // reset cache
        this.wrappedProcessors = null;

        this.processorsWithExpectedExceptions.add(new ProcessorWithExpectedException(processorType, e));

    }


    public void addModules(String... modules) {
        if (modules != null) {
            if (this.modules == null) {
                this.modules = new HashSet<String>();
            }
            this.modules.addAll(Arrays.asList(modules));
            this.modules.remove(null);
        } else {
            this.modules = null;
        }
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


    public void addGeneratedJavaFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String className, JavaFileObject.Kind kind, JavaFileObject javaFileObject) {
        this.generatedJavaFileObjectChecks.add(new GeneratedJavaFileObjectCheck(checkType, location, className, kind, javaFileObject));
    }

    public void addGeneratedJavaFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String className, JavaFileObject.Kind kind, GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher) {
        this.generatedJavaFileObjectChecks.add(new GeneratedJavaFileObjectCheck(checkType, location, className, kind, generatedFileObjectMatcher));
    }

    public void addGeneratedFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String packageName, String relativeName, FileObject javaFileObject) {
        this.generatedFileObjectChecks.add(new GeneratedFileObjectCheck(checkType, location, packageName, relativeName, javaFileObject));
    }

    public void addGeneratedFileObjectCheck(FileObjectCheckType checkType, JavaFileManager.Location location, String packageName, String relativeName, GeneratedFileObjectMatcher<FileObject>... generatedFileObjectMatcher) {
        this.generatedFileObjectChecks.add(new GeneratedFileObjectCheck(checkType, location, packageName, relativeName, generatedFileObjectMatcher));
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

    public Set<Class<? extends Processor>> getProcessorTypes() {
        return processorTypes;
    }

    public Set<ProcessorWithExpectedException> getProcessorsWithExpectedExceptions() {
        return processorsWithExpectedExceptions;
    }


    public Set<AnnotationProcessorWrapper> getWrappedProcessors() {

        // return cached wrapped processors if available
        if (wrappedProcessors != null) {
            return wrappedProcessors;
        }


        Set<AnnotationProcessorWrapper> wrappedProcessors = new HashSet<AnnotationProcessorWrapper>();

        for (Processor processor : this.processors) {

            if (this.expectedThrownException != null) {
                wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, expectedThrownException));
            } else {
                wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, expectedThrownException));
            }

        }

        for (Class<? extends Processor> processorType : this.processorTypes) {

            try {
                Processor processor = (Processor) processorType.getDeclaredConstructor().newInstance();

                if (this.expectedThrownException != null) {
                    wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, expectedThrownException));
                } else {
                    wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor, expectedThrownException));
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Passed processor " + processorType.getCanonicalName() + " cannot be instantiated.", e);
            }

        }

        for (ProcessorWithExpectedException processor : this.processorsWithExpectedExceptions) {

            wrappedProcessors.add(AnnotationProcessorWrapper.wrapProcessor(processor.processorType, processor.throwable != null ? processor.throwable : expectedThrownException));

        }

        // save cached wrapped processors
        this.wrappedProcessors = wrappedProcessors;

        return wrappedProcessors;

    }

    public Set<String> getModules() {
        return modules;
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

    @Override
    public String toString() {
        return "CompileTestConfiguration{\n" +
                "\n\tsourceFiles=" + sourceFiles +
                ",\n\t processors=" + processors +
                ",\n\t processorTypes=" + processorTypes +
                ",\n\t processorsWithExpectedExceptions=" + processorsWithExpectedExceptions +
                ",\n\t expectedThrownException=" + expectedThrownException +
                ",\n\t modules=" + modules +
                ",\n\t compilationShouldSucceed=" + compilationShouldSucceed +
                ",\n\t warningMessageCheck=" + warningMessageCheck +
                ",\n\t mandatoryWarningMessageCheck=" + mandatoryWarningMessageCheck +
                ",\n\t errorMessageCheck=" + errorMessageCheck +
                ",\n\t noteMessageCheck=" + noteMessageCheck +
                ",\n\t generatedJavaFileObjectChecks=" + generatedJavaFileObjectChecks +
                ",\n\t generatedFileObjectChecks=" + generatedFileObjectChecks +
                "\n" +
                '}';
    }
}
