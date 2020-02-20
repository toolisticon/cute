package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;
import io.toolisticon.compiletesting.common.SimpleTestProcessor1;
import io.toolisticon.compiletesting.common.SimpleTestProcessor2;
import io.toolisticon.compiletesting.common.SimpleTestProcessor3;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.Processor;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Unit test for {@link CompileTestConfiguration}
 */
public class CompileTestConfigurationTest {

    private CompileTestConfiguration unit;

    // compiler options
    private final String compilerOption1 = "verbose";
    private final String compilerOption2 = "source 1.7";


    // source files
    private final JavaFileObject sourceJavaFileObject1 = Mockito.mock(JavaFileObject.class);
    private final JavaFileObject sourceJavaFileObject2 = Mockito.mock(JavaFileObject.class);
    private final JavaFileObject sourceJavaFileObject3 = Mockito.mock(JavaFileObject.class);

    // processors
    private final Processor processor1 = Mockito.mock(Processor.class);
    private final Processor processor2 = Mockito.mock(Processor.class);
    private final Processor processor3 = Mockito.mock(Processor.class);

    // processorsWithExpectedExceptions
    private final CompileTestConfiguration.ProcessorWithExpectedException processorWithExpectedException1 = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalStateException.class);
    private final CompileTestConfiguration.ProcessorWithExpectedException processorWithExpectedException2 = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor2.class, IllegalArgumentException.class);
    private final CompileTestConfiguration.ProcessorWithExpectedException processorWithExpectedException3 = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor3.class, IllegalThreadStateException.class);

    // expectedThrownException
    private final Class<? extends Throwable> expectedThrownException = IllegalStateException.class;

    // compilationShouldSucceed
    private final boolean compilationShouldSucceed = true;

    // message checks
    private final String message1 = "MESSAGE1";
    private final String message2 = "MESSAGE2";
    private final String message3 = "MESSAGE3";

    // FileObjects
    private final JavaFileObject jfo1 = Mockito.mock(JavaFileObject.class);
    private final JavaFileObject jfo2 = Mockito.mock(JavaFileObject.class);
    private final FileObject fo1 = Mockito.mock(FileObject.class);
    private final FileObject fo2 = Mockito.mock(FileObject.class);

    // generatedJavaFileObjectChecks
    private final CompileTestConfiguration.GeneratedJavaFileObjectCheck generatedJavaFileObjectChecks3 = new CompileTestConfiguration.GeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, StandardLocation.SOURCE_OUTPUT,
            "className3", JavaFileObject.Kind.SOURCE, Mockito.mock(GeneratedFileObjectMatcher.class));
    private final CompileTestConfiguration.GeneratedJavaFileObjectCheck generatedJavaFileObjectChecks4 = new CompileTestConfiguration.GeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, StandardLocation.CLASS_OUTPUT,
            "className4", JavaFileObject.Kind.CLASS, Mockito.mock(GeneratedFileObjectMatcher.class));

    // generatedFileObjectChecks
    private final CompileTestConfiguration.GeneratedFileObjectCheck generatedFileObjectChecks3 = new CompileTestConfiguration.GeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, StandardLocation.SOURCE_OUTPUT,
            "package3", "relativeName3", toArray(Mockito.mock(GeneratedFileObjectMatcher.class)));
    private final CompileTestConfiguration.GeneratedFileObjectCheck generatedFileObjectChecks4 = new CompileTestConfiguration.GeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, StandardLocation.CLASS_OUTPUT,
            "package4", "relativeName4", toArray(Mockito.mock(GeneratedFileObjectMatcher.class)));


    @SafeVarargs
    private static <ARRAY_TYPE> ARRAY_TYPE[] toArray(ARRAY_TYPE... elements) {
        return elements;
    }

    @Before
    public void init() {
        unit = new CompileTestConfiguration();
    }


    @Test
    public void compilerOptions_addAndGet() {

        unit.addCompilerOptions(compilerOption1, compilerOption2);

        // do assertion
        assertCompilerOptions(unit);

    }

    private void assertCompilerOptions(CompileTestConfiguration configuration) {
        MatcherAssert.assertThat(configuration.getCompilerOptions(), Matchers.containsInAnyOrder(compilerOption1, compilerOption2));
    }

    @Test
    public void sourceFiles_addAndGet() {

        unit.addSourceFiles(sourceJavaFileObject1, sourceJavaFileObject2, sourceJavaFileObject3);

        // do assertion
        assertSourceFiles(unit);

    }

    private void assertSourceFiles(CompileTestConfiguration configuration) {
        MatcherAssert.assertThat(configuration.getSourceFiles(), Matchers.containsInAnyOrder(sourceJavaFileObject1, sourceJavaFileObject2, sourceJavaFileObject3));
    }


    @Test
    public void processors_addAndGet() {

        unit.addProcessors(processor1, processor2, processor3);

        // do assertion
        assertProcessors(unit);

    }

    private void assertProcessors(CompileTestConfiguration configuration) {
        MatcherAssert.assertThat(configuration.getProcessors(), Matchers.containsInAnyOrder(processor1, processor2, processor3));
    }

    @Test
    public void processorTypes_addAndGet() {

        unit.addProcessorTypes(SimpleTestProcessor1.class, SimpleTestProcessor2.class, SimpleTestProcessor3.class);

        // do assertion
        assertProcessorTypes(unit);

    }

    private void assertProcessorTypes(CompileTestConfiguration configuration) {
        MatcherAssert.assertThat(configuration.getProcessorTypes(), Matchers.containsInAnyOrder(SimpleTestProcessor1.class, (Class<? extends Processor>) SimpleTestProcessor2.class, SimpleTestProcessor3.class));
    }

    @Test
    public void processorsWithExpectedExceptions_addAndGet() {

        unit.addProcessorWithExpectedException(processorWithExpectedException1.getProcessorType(), processorWithExpectedException1.getThrowable());
        unit.addProcessorWithExpectedException(processorWithExpectedException2.getProcessorType(), processorWithExpectedException2.getThrowable());
        unit.addProcessorWithExpectedException(processorWithExpectedException3.getProcessorType(), processorWithExpectedException3.getThrowable());

        // do assertion
        assertProcessorsWithExpectedExceptions(unit);

    }

    private void assertProcessorsWithExpectedExceptions(CompileTestConfiguration configuration) {
        MatcherAssert.assertThat(configuration.getProcessorsWithExpectedExceptions(), Matchers.containsInAnyOrder(processorWithExpectedException1, processorWithExpectedException2, processorWithExpectedException3));
    }

    @Test
    public void modules_addAndGet() {

        unit.addModules("spiap.api", "java.compiler");
        unit.addModules("java.base");


        // do assertion
        assertModules(unit);

    }

    private void assertModules(CompileTestConfiguration configuration) {
        MatcherAssert.assertThat(configuration.getModules(), Matchers.containsInAnyOrder("spiap.api", "java.compiler", "java.base"));
    }

    @Test
    public void expectedThrownException_setAndGet() {

        unit.setExpectedThrownException(expectedThrownException);

        // do assertion
        assertExpectedThrownException(unit, expectedThrownException);

    }

    @Test
    public void expectedThrownException_overwriteValue() {

        expectedThrownException_setAndGet();

        unit.setExpectedThrownException(RuntimeException.class);

        // do assertion
        assertExpectedThrownException(unit, RuntimeException.class);

    }

    private void assertExpectedThrownException(CompileTestConfiguration configuration, Class<? extends Throwable> expectedThrownException) {
        MatcherAssert.assertThat(configuration.getExpectedThrownException(), Matchers.equalTo((Class) expectedThrownException));
    }


    @Test
    public void compilationShouldSucceed_setAndGet() {

        unit.setCompilationShouldSucceed(compilationShouldSucceed);

        // do assertion
        assertCompilationShouldSucceed(unit, compilationShouldSucceed);

    }

    @Test
    public void compilationShouldSucceed_overwriteValue() {

        expectedThrownException_setAndGet();

        unit.setCompilationShouldSucceed(false);

        // do assertion
        assertCompilationShouldSucceed(unit, false);

    }

    private void assertCompilationShouldSucceed(CompileTestConfiguration configuration, Boolean compilationShouldSucceed) {
        MatcherAssert.assertThat(configuration.getCompilationShouldSucceed(), Matchers.equalTo(compilationShouldSucceed));
    }


    @Test
    public void compilerMessageCheck_setAndGet() {

        unit.addNoteMessageCheck(CompileTestConfiguration.ComparisionKind.CONTAINS, message1, message2);
        unit.addNoteMessageCheck(CompileTestConfiguration.ComparisionKind.CONTAINS, message3);

        // do assertion
        assertMessages(unit.getCompilerMessageChecks());

    }


    private void assertMessages(Set<CompileTestConfiguration.CompilerMessageCheck> messageSet) {

        List<String> messages = new ArrayList<>();

        Iterator<CompileTestConfiguration.CompilerMessageCheck> iterator = messageSet.iterator();
        while (iterator.hasNext()) {
            messages.add(iterator.next().getExpectedMessage());
        }

        MatcherAssert.assertThat(messages, Matchers.containsInAnyOrder(message1, message2, message3));
    }


    @Test
    public void generatedJavaFileObjectChecks_addAndGet() {

        unit.addGeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, generatedJavaFileObjectChecks3.getLocation(), generatedJavaFileObjectChecks3.getClassName(), generatedJavaFileObjectChecks3.getKind(), generatedJavaFileObjectChecks3.getGeneratedFileObjectMatcher());
        unit.addGeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, generatedJavaFileObjectChecks4.getLocation(), generatedJavaFileObjectChecks4.getClassName(), generatedJavaFileObjectChecks4.getKind(), generatedJavaFileObjectChecks4.getGeneratedFileObjectMatcher());


        // do assertion
        assertGeneratedJavaFileObjectChecks(unit);
    }

    private void assertGeneratedJavaFileObjectChecks(CompileTestConfiguration configuration) {
        MatcherAssert.assertThat(configuration.getGeneratedJavaFileObjectChecks(), Matchers.containsInAnyOrder(generatedJavaFileObjectChecks3, generatedJavaFileObjectChecks4));
    }


    @Test
    public void generatedFileObjectChecks_addAndGet() {

        unit.addGeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, generatedFileObjectChecks3.getLocation(), generatedFileObjectChecks3.getPackageName(), generatedFileObjectChecks3.getRelativeName(), generatedFileObjectChecks3.getGeneratedFileObjectMatchers());
        unit.addGeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, generatedFileObjectChecks4.getLocation(), generatedFileObjectChecks4.getPackageName(), generatedFileObjectChecks4.getRelativeName(), generatedFileObjectChecks4.getGeneratedFileObjectMatchers());


        // do assertion
        assertGeneratedFileObjectChecks(unit);
    }

    private void assertGeneratedFileObjectChecks(CompileTestConfiguration configuration) {
        MatcherAssert.assertThat(configuration.getGeneratedFileObjectChecks(), Matchers.containsInAnyOrder(generatedFileObjectChecks3, generatedFileObjectChecks4));
    }

    @Test
    public void useModulesChecks() {
        unit.addModules("A", "B", "C");
        unit.addModules("D");


        // do assertion
        MatcherAssert.assertThat(unit.getModules(), Matchers.containsInAnyOrder("A", "B", "C", "D"));
    }


    @Test
    public void cloneConfiguration_cloneConfiguration() {

        sourceFiles_addAndGet();
        processors_addAndGet();
        processorTypes_addAndGet();
        processorsWithExpectedExceptions_addAndGet();
        expectedThrownException_setAndGet();
        compilerMessageCheck_setAndGet();
        generatedJavaFileObjectChecks_addAndGet();
        generatedFileObjectChecks_addAndGet();
        modules_addAndGet();

        CompileTestConfiguration clonedConfiguration = CompileTestConfiguration.cloneConfiguration(unit);

        assertSourceFiles(clonedConfiguration);
        assertProcessors(clonedConfiguration);
        assertProcessorTypes(clonedConfiguration);
        assertProcessorsWithExpectedExceptions(clonedConfiguration);
        assertExpectedThrownException(clonedConfiguration, expectedThrownException);
        assertMessages(clonedConfiguration.getCompilerMessageChecks());
        assertGeneratedJavaFileObjectChecks(clonedConfiguration);
        assertGeneratedFileObjectChecks(clonedConfiguration);
        assertModules(clonedConfiguration);

    }


    @Test
    public void wrappedProcessorCacheIsResetCorrectly_addProcessors() {

        processorTypes_addAndGet();

        Set<AnnotationProcessorWrapper> previousCache = unit.getWrappedProcessors();

        processors_addAndGet();

        Set<AnnotationProcessorWrapper> currentCache = unit.getWrappedProcessors();
        MatcherAssert.assertThat("Cache instances must not match!!!", currentCache != previousCache);


    }

    @Test
    public void wrappedProcessorCacheIsResetCorrectly_addProcessorsTypes() {
        processors_addAndGet();

        Set<AnnotationProcessorWrapper> previousCache = unit.getWrappedProcessors();

        processorTypes_addAndGet();

        Set<AnnotationProcessorWrapper> currentCache = unit.getWrappedProcessors();
        MatcherAssert.assertThat("Cache instances must not match!!!", currentCache != previousCache);

    }

    @Test
    public void wrappedProcessorCacheIsResetCorrectly_addProcessorsTypesWithExpectedExceptions() {
        processors_addAndGet();

        Set<AnnotationProcessorWrapper> previousCache = unit.getWrappedProcessors();

        processorsWithExpectedExceptions_addAndGet();

        Set<AnnotationProcessorWrapper> currentCache = unit.getWrappedProcessors();
        MatcherAssert.assertThat("Cache instances must not match!!!", currentCache != previousCache);

    }

    @Test
    public void wrappedProcessorCacheSubsequentCallsShouldReturnSameCache() {
        processors_addAndGet();

        Set<AnnotationProcessorWrapper> previousCache = unit.getWrappedProcessors();
        Set<AnnotationProcessorWrapper> currentCache = unit.getWrappedProcessors();

        MatcherAssert.assertThat("Subsequent calls to get wrapped processors should return same cache set!!!", currentCache == previousCache);

    }

    @Test
    public void wrappedModulesCacheIsResetCorrectly_addProcessors() {

        processorTypes_addAndGet();

        Set<AnnotationProcessorWrapper> previousCache = unit.getWrappedProcessors();

        processors_addAndGet();

        Set<AnnotationProcessorWrapper> currentCache = unit.getWrappedProcessors();
        MatcherAssert.assertThat("Cache instances must not match!!!", currentCache != previousCache);


    }


    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    // -- STATIC INNER CLASSES TESTS
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------


    // -------------------------------------------------------------------
    // -- ProcessorWithExpectedException
    // -------------------------------------------------------------------

    @Test
    public void processorWithExpectedException_matching() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalStateException.class);
        CompileTestConfiguration.ProcessorWithExpectedException otherObj = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalStateException.class);

        MatcherAssert.assertThat("Objects should be detected as equal", unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should be detected as equal", otherObj.equals(unit));

        MatcherAssert.assertThat("HashCodes should be identical", unit.hashCode() == otherObj.hashCode());

    }

    @Test
    public void processorWithExpectedException_matching_processorIsNull() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(null, IllegalStateException.class);
        CompileTestConfiguration.ProcessorWithExpectedException otherObj = new CompileTestConfiguration.ProcessorWithExpectedException(null, IllegalStateException.class);

        MatcherAssert.assertThat("Objects should be detected as equal", unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should be detected as equal", otherObj.equals(unit));

        MatcherAssert.assertThat("HashCodes should be identical", unit.hashCode() == otherObj.hashCode());

    }

    @Test
    public void processorWithExpectedException_matching_throwableIsNull() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, null);
        CompileTestConfiguration.ProcessorWithExpectedException otherObj = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, null);

        MatcherAssert.assertThat("Objects should be detected as equal", unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should be detected as equal", otherObj.equals(unit));

        MatcherAssert.assertThat("HashCodes should be identical", unit.hashCode() == otherObj.hashCode());

    }

    @Test
    public void processorWithExpectedException_matching_allParametersAreNull() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(null, null);
        CompileTestConfiguration.ProcessorWithExpectedException otherObj = new CompileTestConfiguration.ProcessorWithExpectedException(null, null);

        MatcherAssert.assertThat("Objects should be detected as equal", unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should be detected as equal", otherObj.equals(unit));

        MatcherAssert.assertThat("HashCodes should be identical", unit.hashCode() == otherObj.hashCode());

    }

    @Test
    public void processorWithExpectedException_nonMatching_passedObjectIsNull() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalStateException.class);

        MatcherAssert.assertThat("Objects should be detected as not equal", !unit.equals(null));


    }

    @Test
    public void processorWithExpectedException_nonMatching_oneProcessorIsNull() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalStateException.class);
        CompileTestConfiguration.ProcessorWithExpectedException otherObj = new CompileTestConfiguration.ProcessorWithExpectedException(null, IllegalStateException.class);

        MatcherAssert.assertThat("Objects should not be detected as equal", !unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should not be detected as equal", !otherObj.equals(unit));


    }

    @Test
    public void processorWithExpectedException_nonMatching_oneThrowableIsNull() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalStateException.class);
        CompileTestConfiguration.ProcessorWithExpectedException otherObj = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, null);

        MatcherAssert.assertThat("Objects should not be detected as equal", !unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should not be detected as equal", !otherObj.equals(unit));


    }

    @Test
    public void processorWithExpectedException_nonMatching_processorIsDifferent() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalStateException.class);
        CompileTestConfiguration.ProcessorWithExpectedException otherObj = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor2.class, IllegalStateException.class);

        MatcherAssert.assertThat("Objects should not be detected as equal", !unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should not be detected as equal", !otherObj.equals(unit));


    }

    @Test
    public void processorWithExpectedException_nonMatching_throwableIsDifferent() {

        CompileTestConfiguration.ProcessorWithExpectedException unit = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalStateException.class);
        CompileTestConfiguration.ProcessorWithExpectedException otherObj = new CompileTestConfiguration.ProcessorWithExpectedException(SimpleTestProcessor1.class, IllegalArgumentException.class);

        MatcherAssert.assertThat("Objects should not be detected as equal", !unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should not be detected as equal", !otherObj.equals(unit));


    }


    // -------------------------------------------------------------------
    // -- GeneratedFileObjectCheck
    // -------------------------------------------------------------------


    @Test
    public void generatedFileObjectCheck_matching_withFileObject() {

        CompileTestConfiguration.GeneratedFileObjectCheck otherObj = new CompileTestConfiguration.GeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, generatedFileObjectChecks3.getLocation(),
                generatedFileObjectChecks3.getPackageName(), generatedFileObjectChecks3.getRelativeName(), generatedFileObjectChecks3.getGeneratedFileObjectMatchers());

        MatcherAssert.assertThat("Objects should be detected as equal", generatedFileObjectChecks3.equals(otherObj));
        MatcherAssert.assertThat("Objects should be detected as equal", otherObj.equals(generatedFileObjectChecks3));

        MatcherAssert.assertThat("HashCodes should be identical", generatedFileObjectChecks3.hashCode() == otherObj.hashCode());

    }

    @Test
    public void generatedFileObjectCheck_notMatching() {


        MatcherAssert.assertThat("Objects should not be detected as equal", !generatedFileObjectChecks3.equals(generatedFileObjectChecks4));

        MatcherAssert.assertThat("Objects should not be detected as equal", !generatedFileObjectChecks4.equals(generatedFileObjectChecks3));

    }

    @Test
    public void generatedFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher() {

        // location differs
        JavaFileManager.Location alternativeLocation = StandardLocation.SOURCE_PATH;
        MatcherAssert.assertThat("PRECONDITION locations must not match", alternativeLocation != generatedFileObjectChecks3.getLocation());
        generatedFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(generatedFileObjectChecks3, alternativeLocation,
                generatedFileObjectChecks3.getPackageName(), generatedFileObjectChecks3.getRelativeName(), generatedFileObjectChecks3.getGeneratedFileObjectMatchers());

        // packageName differs
        String alternativePackageName = "XXX";
        MatcherAssert.assertThat("PRECONDITION locations must not match", !alternativePackageName.equals(generatedFileObjectChecks3.getPackageName()));
        generatedFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(generatedFileObjectChecks3, generatedFileObjectChecks3.getLocation(),
                alternativePackageName, generatedFileObjectChecks3.getRelativeName(), generatedFileObjectChecks3.getGeneratedFileObjectMatchers());

        // relativeName differs
        String alternativeRelativeName = "XXX";
        MatcherAssert.assertThat("PRECONDITION locations must not match", !alternativeRelativeName.equals(generatedFileObjectChecks3.getRelativeName()));
        generatedFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(generatedFileObjectChecks3, generatedFileObjectChecks3.getLocation(),
                generatedFileObjectChecks3.getPackageName(), alternativeRelativeName, generatedFileObjectChecks3.getGeneratedFileObjectMatchers());

        // expectedFileObject differs
        generatedFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(generatedFileObjectChecks3, generatedFileObjectChecks3.getLocation(),
                generatedFileObjectChecks3.getPackageName(), generatedFileObjectChecks3.getRelativeName(), toArray(Mockito.mock(GeneratedFileObjectMatcher.class)));

    }

    private void generatedFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(CompileTestConfiguration.GeneratedFileObjectCheck unit, JavaFileManager.Location location, String packageName, String relativeName, GeneratedFileObjectMatcher<FileObject>[] generatedFileObjectMatcher) {

        CompileTestConfiguration.GeneratedFileObjectCheck otherObj = new CompileTestConfiguration.GeneratedFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, location,
                packageName, packageName, generatedFileObjectMatcher);

        MatcherAssert.assertThat("Objects should not be detected as equal", !unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should not be detected as equal", !otherObj.equals(unit));

    }

    // -------------------------------------------------------------------
    // -- GeneratedJavaFileObjectCheck
    // -------------------------------------------------------------------


    @Test
    public void generatedJavaFileObjectCheck_notMatching() {


        MatcherAssert.assertThat("Objects should not be detected as equal", !generatedJavaFileObjectChecks3.equals(generatedJavaFileObjectChecks4));
        MatcherAssert.assertThat("Objects should not be detected as equal", !generatedJavaFileObjectChecks4.equals(generatedJavaFileObjectChecks3));

    }


    @Test
    public void generatedJavaFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher() {

        // location differs
        JavaFileManager.Location alternativeLocation = StandardLocation.SOURCE_PATH;
        MatcherAssert.assertThat("PRECONDITION locations must not match", alternativeLocation != generatedJavaFileObjectChecks3.getLocation());
        generatedJavaFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(generatedJavaFileObjectChecks3, alternativeLocation,
                generatedJavaFileObjectChecks3.getClassName(), generatedJavaFileObjectChecks3.getKind(), Mockito.mock(GeneratedFileObjectMatcher.class));

        // className differs
        String alternativeClassName = "XXX";
        MatcherAssert.assertThat("PRECONDITION locations must not match", !alternativeClassName.equals(generatedJavaFileObjectChecks3.getClassName()));
        generatedJavaFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(generatedJavaFileObjectChecks3, generatedJavaFileObjectChecks3.getLocation(),
                alternativeClassName, generatedJavaFileObjectChecks3.getKind(), Mockito.mock(GeneratedFileObjectMatcher.class));

        // kind differs
        JavaFileObject.Kind alternativeKind = JavaFileObject.Kind.OTHER;
        MatcherAssert.assertThat("PRECONDITION locations must not match", !alternativeKind.equals(generatedJavaFileObjectChecks3.getKind()));
        generatedJavaFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(generatedJavaFileObjectChecks3, generatedJavaFileObjectChecks3.getLocation(),
                generatedJavaFileObjectChecks3.getClassName(), alternativeKind, Mockito.mock(GeneratedFileObjectMatcher.class));

        // expectedFileObject differs
        generatedJavaFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(generatedJavaFileObjectChecks3, generatedJavaFileObjectChecks3.getLocation(),
                generatedJavaFileObjectChecks3.getClassName(), generatedJavaFileObjectChecks3.getKind(), Mockito.mock(GeneratedFileObjectMatcher.class));

    }

    private void generatedJavaFileObjectCheck_notMatchingSingleField_withGeneratedFileObjectMatcher_singleTest(CompileTestConfiguration.GeneratedJavaFileObjectCheck unit, JavaFileManager.Location location, String className, JavaFileObject.Kind kind, GeneratedFileObjectMatcher<JavaFileObject> generatedFileObjectMatcher) {

        CompileTestConfiguration.GeneratedJavaFileObjectCheck otherObj = new CompileTestConfiguration.GeneratedJavaFileObjectCheck(CompileTestConfiguration.FileObjectCheckType.EXISTS, location,
                className, kind, generatedFileObjectMatcher);

        MatcherAssert.assertThat("Objects should not be detected as equal", !unit.equals(otherObj));
        MatcherAssert.assertThat("Objects should not be detected as equal", !otherObj.equals(unit));

    }


}
