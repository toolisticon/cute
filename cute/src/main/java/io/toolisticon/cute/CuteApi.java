package io.toolisticon.cute;


import io.toolisticon.cute.matchers.CoreGeneratedFileObjectMatchers;
import io.toolisticon.fluapigen.api.FluentApi;
import io.toolisticon.fluapigen.api.FluentApiBackingBean;
import io.toolisticon.fluapigen.api.FluentApiBackingBeanField;
import io.toolisticon.fluapigen.api.FluentApiBackingBeanMapping;
import io.toolisticon.fluapigen.api.FluentApiCommand;
import io.toolisticon.fluapigen.api.FluentApiConverter;
import io.toolisticon.fluapigen.api.FluentApiImplicitValue;
import io.toolisticon.fluapigen.api.FluentApiInterface;
import io.toolisticon.fluapigen.api.FluentApiParentBackingBeanMapping;
import io.toolisticon.fluapigen.api.FluentApiRoot;
import io.toolisticon.fluapigen.api.MappingAction;
import io.toolisticon.fluapigen.api.TargetBackingBean;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@FluentApi("Cute")
public class CuteApi {

    @FluentApiBackingBean
    public interface CompilerTestBB {
        TestType testType();

        UnitTestType getPassInType();


        List<Class<Processor>> processors();

        List<String> compilerOptions();

        Set<JavaFileObject> sourceFiles();

        Set<String> modules();

        @FluentApiBackingBeanField("compilationSucceeded")
        Boolean compilationSucceeded();

        @FluentApiBackingBeanField("exceptionIsThrown")
        Class<? extends Exception> getExceptionIsThrown();

        UnitTestBase unitTest();

        PassInConfigurationBB passInConfiguration();

        @FluentApiBackingBeanField("compileMessageChecks")
        List<CompilerMessageCheckBB> compilerMessageChecks();

        @FluentApiBackingBeanField("javaFileObjectChecks")
        List<GeneratedJavaFileObjectCheckBB> javaFileObjectChecks();

        @FluentApiBackingBeanField("fileObjectChecks")
        List<GeneratedFileObjectCheckBB> fileObjectChecks();

        default long countErrorMessageChecks() {
            long count = 0;

            for (CuteApi.CompilerMessageCheckBB compilerMessageCheck : compilerMessageChecks()) {
                if (CompilerMessageKind.ERROR.toString().equals(compilerMessageCheck.getKind().name())) {
                    count++;
                }
            }

            return count;
        }

        default List<String> getNormalizedCompilerOptions() {

            List<String> normalizedCompilerOptions = new ArrayList<>();

            for (String compilerOption : compilerOptions()) {

                if (compilerOption != null) {
                    for (String tokenizedCompilerOption : compilerOption.split("[ ]+")) {
                        if (!tokenizedCompilerOption.isEmpty()) {
                            normalizedCompilerOptions.add(tokenizedCompilerOption);
                        }
                    }
                }
            }

            return normalizedCompilerOptions;

        }


    }


    @FluentApiBackingBean
    public interface PassInConfigurationBB {

        /**
         * This is a marker if passing in of Elements should be used.
         * In this case exactly one PassIn annotation must be present in passed in Class or in added source files.
         *
         * @return true if unit test should pass in an element, otherwise false
         */
        @FluentApiBackingBeanField(value = "passInElement", initValue = "false")
        boolean getPassInElement();

        @FluentApiBackingBeanField("passedInClass")
        Class<?> getPassedInClass();

        @FluentApiBackingBeanField("annotationToScanFor")
        Class<? extends Annotation> getAnnotationToScanFor();

        @FluentApiBackingBeanField("passedInProcessor")
        Class<? extends Processor> getPassedInProcessor();
    }

    @FluentApiBackingBean
    public interface CompilerMessageCheckBB {

        @FluentApiBackingBeanField("compilerMessageScope")
        CompilerMessageKind getKind();

        @FluentApiBackingBeanField("compilerMessageComparisonType")
        CompilerMessageComparisonType getComparisonType();

        @FluentApiBackingBeanField("searchString")
        List<String> getSearchString();

        @FluentApiBackingBeanField("atLine")
        Integer atLine();

        @FluentApiBackingBeanField("atColumn")
        Integer atColumn();

        @FluentApiBackingBeanField("atSource")
        String atSource();

        @FluentApiBackingBeanField("withLocale")
        Locale withLocale();

    }


    public enum TestType {
        UNIT,
        BLACK_BOX
    }

    public enum UnitTestType {
        NO_PASS_IN,
        ELEMENT,
        PROCESSOR,
        ELEMENT_AND_PROCESSOR
    }

    public enum CompilerMessageComparisonType {
        CONTAINS,
        EQUALS;
    }

    public enum CompilerMessageKind {
        NOTE,
        WARNING,
        MANDATORY_WARNING,
        ERROR;
    }


    public enum FileObjectCheckType {
        EXISTS,
        DOESNT_EXIST
    }

    @FluentApiBackingBean
    public interface GeneratedJavaFileObjectCheckBB {

        @FluentApiBackingBeanField("checkType")
        FileObjectCheckType getCheckType();

        @FluentApiBackingBeanField("location")
        JavaFileManager.Location getLocation();

        @FluentApiBackingBeanField("className")
        String getClassName();

        @FluentApiBackingBeanField("kind")
        JavaFileObject.Kind getKind();

        @FluentApiBackingBeanField("generatedFileObjectMatcher")
        GeneratedFileObjectMatcher getGeneratedFileObjectMatcher();
    }

    @FluentApiBackingBean
    public interface GeneratedFileObjectCheckBB {

        @FluentApiBackingBeanField("checkType")
        FileObjectCheckType getCheckType();

        @FluentApiBackingBeanField("location")
        JavaFileManager.Location getLocation();

        @FluentApiBackingBeanField("packageName")
        String getPackageName();

        @FluentApiBackingBeanField("relativeName")
        String getRelativeName();

        @FluentApiBackingBeanField("generatedFileObjectMatcher")
        GeneratedFileObjectMatcher[] getGeneratedFileObjectMatchers();

    }


    @FluentApiInterface(CompilerTestBB.class)
    @FluentApiRoot
    public interface MyRootInterface {

        @FluentApiImplicitValue(id = "testType", value = "UNIT")
        @FluentApiImplicitValue(id = "sourceFiles", value = Constants.DEFAULT_UNIT_TEST_SOURCE_FILE, converter = ResourceToFileObjectConverter.class)
        UnitTestRootInterface unitTest();


        @FluentApiImplicitValue(id = "testType", value = "BLACK_BOX")
        BlackBoxTestRootInterface blackBoxTest();


    }

    public static class ResourceToFileObjectConverter implements FluentApiConverter<String, JavaFileObject> {
        @Override
        public JavaFileObject convert(String resource) {
            return JavaFileObjectUtils.readFromResource(resource);
        }
    }

    // --------------------------------------------------------------------
    // Black Box Test Interfaces
    // --------------------------------------------------------------------

    @FluentApiInterface(CompilerTestBB.class)
    public interface BlackBoxTestRootInterface {

        BlackBoxTestProcessorsInterface given();

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface BlackBoxTestProcessorsInterface {

        BlackBoxTestSourceFilesInterface processors(@FluentApiBackingBeanMapping(value = "processors") Class<? extends Processor>... processors);

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface BlackBoxTestSourceFilesInterface {

        BlackBoxTestFinalGivenInterface andSourceFiles(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.ADD) JavaFileObject... sourceFile);

        default BlackBoxTestFinalGivenInterface andSourceFiles(String... resources) {
            return andSourceFiles(Arrays.stream(resources).map(e -> JavaFileObjectUtils.readFromResource(e)).toArray(JavaFileObject[]::new));
        }

        default BlackBoxTestFinalGivenInterface andSourceFile(String className, String content) {
            return andSourceFiles(JavaFileObjectUtils.readFromString(className, content));
        }


    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface BlackBoxTestFinalGivenInterface {

        BlackBoxTestFinalGivenInterface andSourceFiles(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.ADD) JavaFileObject... sourceFile);

        default BlackBoxTestFinalGivenInterface andSourceFiles(String... resources) {
            return andSourceFiles(Arrays.stream(resources).map(e -> JavaFileObjectUtils.readFromResource(e)).toArray(String[]::new));
        }

        default BlackBoxTestFinalGivenInterface andSourceFile(String className, String content) {
            return andSourceFiles(JavaFileObjectUtils.readFromString(className, content));
        }

        BlackBoxTestFinalGivenInterface andUseCompilerOptions(@FluentApiBackingBeanMapping(value = "compilerOptions") String... compilerOptions);

        BlackBoxTestFinalGivenInterface andUseModules(@FluentApiBackingBeanMapping(value = "modules") String... modules);

        CompilerTestInterface whenCompiled();

        @FluentApiCommand(ExecuteTestCommand.class)
        void executeTest();

    }

    // --------------------------------------------------------------------
    // Unit Test Interfaces
    // --------------------------------------------------------------------

    @FluentApiInterface(CompilerTestBB.class)
    public interface UnitTestRootInterface {

        UnitTestGivenInterface given();

        /**
         * Starts when block for unit test.
         *
         * @return the next fluent api instance
         */
        UnitTestWhenInterface when();

        CompilerTestInterface when(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET) UnitTestWithoutPassIn unitTest);

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface UnitTestGivenInterface {

        UnitTestGivenInterface useCompilerOptions(@FluentApiBackingBeanMapping(value = "compilerOptions") String... compilerOptions);

        UnitTestGivenInterface useModules(@FluentApiBackingBeanMapping(value = "modules") String... modules);


        default UnitTestGivenInterface useSourceFile(String resource) {
            return useSourceFile(JavaFileObjectUtils.readFromResource(resource));
        }

        default UnitTestGivenInterface useSourceFile(String className, String content) {
            return useSourceFile(JavaFileObjectUtils.readFromString(className, content));
        }

        UnitTestGivenInterface useSourceFile(@FluentApiBackingBeanMapping(value = "sourceFiles") JavaFileObject sourceFile);


        UnitTestWhenInterface when();


    }


    @FluentApiInterface(CompilerTestBB.class)
    public interface UnitTestWhenInterface {

        PassInElementInterface passInElement();

        <PROCESSOR_CLASS extends Processor> PassInProcessorInterface<PROCESSOR_CLASS> passInProcessor(@FluentApiBackingBeanMapping(value = "passedInProcessor", target = TargetBackingBean.NEXT) Class<PROCESSOR_CLASS> processorClass);

        CompilerTestInterface unitTestWithoutPassIn(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET, target = TargetBackingBean.NEXT) UnitTestWithoutPassIn unitTest);

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface PassInElementInterface {

        default <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromStringSource(String sourceString, String name) {
            return this.<ELEMENT_TYPE>fromJavaFileObject(JavaFileObjectUtils.readFromString(name, sourceString));
        }

        default <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromSourceFile(String resourceName) {
            return this.<ELEMENT_TYPE>fromJavaFileObject(JavaFileObjectUtils.readFromResource(resourceName));
        }

        @FluentApiImplicitValue(id = "passInElement", value = "true", target = TargetBackingBean.NEXT)
        <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromJavaFileObject(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.SET) JavaFileObject javaFileObject);

        @FluentApiImplicitValue(id = "passInElement", value = "true", target = TargetBackingBean.NEXT)
        <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromClass(@FluentApiBackingBeanMapping(value = "passedInClass", target = TargetBackingBean.NEXT) Class<?> classToScanForElement);

        /**
         * The element will be passed in from given source files. Exactly one Source file must contain a PassIn annotation.
         * This method should only be used, if non-runtime annotations are involved in unit tests.
         *
         * @param <ELEMENT_TYPE> the Element type of passed in interface.
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "passInElement", value = "true", target = TargetBackingBean.NEXT)
        <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromGivenSourceFiles();

    }

    @FluentApiInterface(PassInConfigurationBB.class)
    public interface PassInProcessorInterface<PROCESSOR_CLASS extends Processor> {
        PassInProcessorAndElementInterface<PROCESSOR_CLASS> andPassInElement();

        @FluentApiImplicitValue(id = "getPassInType", value = "PROCESSOR", target = TargetBackingBean.NEXT)
        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        CompilerTestInterface intoUnitTest(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET, target = TargetBackingBean.NEXT) UnitTestForTestingAnnotationProcessorsWithoutPassIn<PROCESSOR_CLASS> unitTest);
    }


    @FluentApiInterface(PassInConfigurationBB.class)
    public interface PassInElementAndProcessorInterface<ELEMENT_TYPE extends Element> {

        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        <PROCESSOR_TYPE extends Processor> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_TYPE> andPassInProcessor(@FluentApiBackingBeanMapping(value = "passedInProcessor") Class<PROCESSOR_TYPE> processorClass);

        @FluentApiImplicitValue(id = "getPassInType", value = "ELEMENT", target = TargetBackingBean.NEXT)
        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        CompilerTestInterface intoUnitTest(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET, target = TargetBackingBean.NEXT) UnitTest<ELEMENT_TYPE> unitTest);
    }

    @FluentApiInterface(PassInConfigurationBB.class)
    public interface PassInProcessorAndElementInterface<PROCESSOR_CLASS extends Processor> {

        default <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromStringSource(String sourceString, String name) {
            return this.<ELEMENT_TYPE>fromJavaFileObject(JavaFileObjectUtils.readFromString(name, sourceString));
        }

        default <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromSourceFile(String resourceName) {
            return this.<ELEMENT_TYPE>fromJavaFileObject(JavaFileObjectUtils.readFromResource(resourceName));
        }

        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        @FluentApiImplicitValue(id = "passInElement", value = "true")
        <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromJavaFileObject(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.SET, target = TargetBackingBean.NEXT) JavaFileObject javaFileObject);

        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        @FluentApiImplicitValue(id = "passInElement", value = "true")
        <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromClass(@FluentApiBackingBeanMapping(value = "passedInClass", target = TargetBackingBean.THIS) Class<?> classToScanForElement);

        /**
         * The element will be passed in from given source files. Exactly one Source file must contain a PassIn annotation.
         * This method should only be used, if non-runtime annotations are involved in unit tests.
         *
         * @param <ELEMENT_TYPE> the Element type of passed in interface.
         * @return the next fluent interface
         */
        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        @FluentApiImplicitValue(id = "passInElement", value = "true")
        <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromGivenSourceFiles();

    }


    @FluentApiInterface(CompilerTestBB.class)
    public interface UnitTestWhenWithPassedInElementAndProcessorInterface<E extends Element, P extends Processor> {

        @FluentApiImplicitValue(id = "getPassInType", value = "ELEMENT_AND_PROCESSOR", target = TargetBackingBean.NEXT)
        CompilerTestInterface intoUnitTest(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET) UnitTestForTestingAnnotationProcessors<P, E> unitTest);
    }


    // --------------------------------------------------------------------
    // Common "then" Interfaces
    // --------------------------------------------------------------------

    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilerTestInterface {


        CompilerTestOutcomeInterface thenExpectThat();

        @FluentApiCommand(ExecuteTestCommand.class)
        void executeTest();


    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilerTestOutcomeInterface {

        @FluentApiImplicitValue(id = "compilationSucceeded", value = "true")
        CompilerTestExpectAndThatInterface compilationSucceeds();

        @FluentApiImplicitValue(id = "compilationSucceeded", value = "false")
        CompilerTestExpectAndThatInterface compilationFails();

        CompilerTestExpectAndThatInterface exceptionIsThrown(@FluentApiBackingBeanMapping(value = "exceptionIsThrown") Class<? extends Exception> exception);


    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilerTestExpectAndThatInterface {

        CompilerTestExpectThatInterface andThat();

        @FluentApiCommand(ExecuteTestCommand.class)
        void executeTest();

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilerTestExpectThatInterface {


        default GeneratedJavaFileObjectCheck generatedClass(String className) {
            return javaFileObject(StandardLocation.CLASS_OUTPUT, className, JavaFileObject.Kind.CLASS);
        }

        default GeneratedJavaFileObjectCheck generatedSourceFile(String className) {
            return javaFileObject(StandardLocation.SOURCE_OUTPUT, className, JavaFileObject.Kind.SOURCE);
        }

        default GeneratedFileObjectCheck generatedResourceFile(String packageName,
                                                               String relativeName) {
            return fileObject(StandardLocation.CLASS_OUTPUT, packageName, relativeName);
        }

        GeneratedJavaFileObjectCheck javaFileObject(
                @FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.NEXT) JavaFileManager.Location location,
                @FluentApiBackingBeanMapping(value = "className", target = TargetBackingBean.NEXT) String className,
                @FluentApiBackingBeanMapping(value = "kind", target = TargetBackingBean.NEXT) JavaFileObject.Kind kind);

        GeneratedFileObjectCheck fileObject(
                @FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.NEXT) JavaFileManager.Location location,
                @FluentApiBackingBeanMapping(value = "packageName", target = TargetBackingBean.NEXT) String packageName,
                @FluentApiBackingBeanMapping(value = "relativeName", target = TargetBackingBean.NEXT) String relativeName);


        CompilerMessageCheckMessageType compilerMessage();


    }


    @FluentApiInterface(CompilerMessageCheckBB.class)
    public interface CompilerMessageCheckMessageType {

        @FluentApiImplicitValue(id = "compilerMessageScope", value = "NOTE")
        CompilerMessageCheckComparisonType ofKindNote();

        @FluentApiImplicitValue(id = "compilerMessageScope", value = "WARNING")
        CompilerMessageCheckComparisonType ofKindWarning();

        @FluentApiImplicitValue(id = "compilerMessageScope", value = "MANDATORY_WARNING")
        CompilerMessageCheckComparisonType ofKindMandatoryWarning();

        @FluentApiImplicitValue(id = "compilerMessageScope", value = "ERROR")
        CompilerMessageCheckComparisonType ofKindError();

    }

    @FluentApiInterface(CompilerMessageCheckBB.class)
    public interface CompilerMessageCheckComparisonType {

        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "CONTAINS")
        @FluentApiParentBackingBeanMapping(value = "compileMessageChecks")
        CompilerTestExpectAndThatInterface contains(
                @FluentApiBackingBeanMapping(value = "searchString", action = MappingAction.SET) String... text
        );


        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "EQUALS")
        @FluentApiParentBackingBeanMapping(value = "compileMessageChecks")
        CompilerTestExpectAndThatInterface equals(
                @FluentApiBackingBeanMapping(value = "searchString", action = MappingAction.SET) String text
        );


        CompilerMessageCheckComparisonType atLine(@FluentApiBackingBeanMapping(value = "atLine") int line);

        CompilerMessageCheckComparisonType atColumn(@FluentApiBackingBeanMapping(value = "atColumn") int column);

        CompilerMessageCheckComparisonType atSource(@FluentApiBackingBeanMapping(value = "atSource") String column);

        CompilerMessageCheckComparisonType withLocale(@FluentApiBackingBeanMapping(value = "withLocale") Locale column);

    }

    @FluentApiInterface(GeneratedJavaFileObjectCheckBB.class)
    public interface GeneratedJavaFileObjectCheck {

        @FluentApiImplicitValue(id = "checkType", value = "EXISTS")
        @FluentApiParentBackingBeanMapping(value = "javaFileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface exists();

        @FluentApiImplicitValue(id = "checkType", value = "DOESNT_EXIST")
        @FluentApiParentBackingBeanMapping(value = "javaFileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface doesntExist();

        default CompilerTestExpectAndThatInterface equals(JavaFileObject expectedJavaFileObject) {
            return matches(ExpectedFileObjectMatcherKind.BINARY, expectedJavaFileObject);
        }

        default CompilerTestExpectAndThatInterface matches(ExpectedFileObjectMatcherKind expectedFileObjectMatcherKind, JavaFileObject expectedJavaFileObject) {
            return matches(expectedFileObjectMatcherKind.createMatcher(expectedJavaFileObject));
        }

        @FluentApiImplicitValue(id = "checkType", value = "EXISTS")
        @FluentApiParentBackingBeanMapping(value = "javaFileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface matches(@FluentApiBackingBeanMapping(value = "generatedFileObjectMatcher") GeneratedFileObjectMatcher generatedJavaFileObjectCheck);

    }

    @FluentApiInterface(GeneratedFileObjectCheckBB.class)
    public interface GeneratedFileObjectCheck {

        @FluentApiImplicitValue(id = "checkType", value = "EXISTS")
        @FluentApiParentBackingBeanMapping(value = "fileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface exists();

        @FluentApiImplicitValue(id = "checkType", value = "DOESNT_EXIST")
        @FluentApiParentBackingBeanMapping(value = "fileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface doesntExist();

        default CompilerTestExpectAndThatInterface equals(FileObject expectedFileObject) {
            return matches(ExpectedFileObjectMatcherKind.TEXT_IGNORE_LINE_ENDINGS, expectedFileObject);
        }

        default CompilerTestExpectAndThatInterface matches(ExpectedFileObjectMatcherKind expectedFileObjectMatcherKind, FileObject expectedFileObject) {
            return matches(expectedFileObjectMatcherKind.createMatcher(expectedFileObject));
        }

        @FluentApiImplicitValue(id = "checkType", value = "EXISTS")
        @FluentApiParentBackingBeanMapping(value = "fileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface matches(@FluentApiBackingBeanMapping(value = "generatedFileObjectMatcher") GeneratedFileObjectMatcher... generatedJavaFileObjectCheck);


    }


    /**
     * Configures how FileObjects should be compared.
     */
    public enum ExpectedFileObjectMatcherKind {
        /**
         * Does binary comparison.
         * Be careful: tests using binary comparison may fail because of line-endings depending on OS.
         */
        BINARY {
            @Override
            public <T extends FileObject> GeneratedFileObjectMatcher createMatcher(T expectedFileObject) {
                return CoreGeneratedFileObjectMatchers.createBinaryMatcher(expectedFileObject);
            }
        },
        /**
         * Textual comparison line by line by ignoring the OS depending line-endings.
         */
        TEXT_IGNORE_LINE_ENDINGS {
            @Override
            public <T extends FileObject> GeneratedFileObjectMatcher createMatcher(T expectedFileObject) {
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
        protected abstract <T extends FileObject> GeneratedFileObjectMatcher createMatcher(T expectedFileObject);
    }

    // --------------------------------------------------------------------
    // Commands
    // --------------------------------------------------------------------

    @FluentApiCommand
    public static class ExecuteTestCommand {
        static void myCommand(CompilerTestBB backingBean) {
            new CompileTest(backingBean).executeTest();
        }
    }


}