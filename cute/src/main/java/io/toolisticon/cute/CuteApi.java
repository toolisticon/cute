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
import io.toolisticon.fluapigen.validation.api.NotNull;

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

/**
 * This class defines the fluent api used to set up and execute black box and unit compile tests.
 * Black box tests are compiling source files by using a processor and allow checking its outcome.
 * Unit tests are allowing you to unit test annotation processor related code by providing a compile-time testing environment.
 *
 * @author Tobias Stamann
 */
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

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    // Fluent API Interfaces
    // --------------------------------------------------------------------
    // --------------------------------------------------------------------

    /**
     * This is the root interface.
     * It allows the selection of the test type.
     * All of its methods will be statically linked in the {@link Cute} fluent api implementation class.
     */
    @FluentApiInterface(CompilerTestBB.class)
    @FluentApiRoot
    public interface MyRootInterface {

        /**
         * This method can be used to start a unit test.
         * @return the next fluent api interface
         */
        @FluentApiImplicitValue(id = "testType", value = "UNIT")
        @FluentApiImplicitValue(id = "sourceFiles", value = Constants.DEFAULT_UNIT_TEST_SOURCE_FILE, converter = ResourceToFileObjectConverter.class)
        UnitTestRootInterface unitTest();


        /**
         * This method can be used to start a black-box test (aka compile-test).
         * Black-box test are compiling some source files with an annotation processor and can check the compilation's outcome.
         * @return
         */
        @FluentApiImplicitValue(id = "testType", value = "BLACK_BOX")
        BlackBoxTestRootInterface blackBoxTest();


    }

    /**
     * A converter to read a JavaFileObject for a resource location string.
     */
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

        /**
         * Allows you to traverse to the given section which allows you to configure the annotation processor, source files,...
         * @return the next fluent interface
         */
        BlackBoxTestProcessorsInterface given();

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface BlackBoxTestProcessorsInterface {

        /**
         * Allows you to add annotation processors used at black-box tests compilation.
         * By passing no processors compilation will be done without using processors.
         * @param processors the annotation processors to use. Empty value will compile the source files without using processors, null values are prohibited and will lead to a {@link io.toolisticon.fluapigen.validation.api.ValidatorException}.
         * @return the next fluent interface
         */
        BlackBoxTestSourceFilesInterface processors(@FluentApiBackingBeanMapping(value = "processors") @NotNull Class<? extends Processor>... processors);

        /**
         * More obvious method not to use processors during compilation.
         * Same as calling processors without values.
         * @return the next fluent interface
         */
        default BlackBoxTestSourceFilesInterface noProcessors(){
            return processors();
        }

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface BlackBoxTestSourceFilesInterface {

        /**
         * Adds source files as JavaFileObjects.
         * The {@link JavaFileObjectUtils} class can be used to provide source files.
         * @param sourceFile the source files to use during compilation
         * @return the next fluent interface
         */
        BlackBoxTestFinalGivenInterface andSourceFiles(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.ADD) @NotNull JavaFileObject... sourceFile);

        /**
         * Add sources files by reading them from resource Strings.
         * @param resources the resource paths used to read the source files from
         * @return the next fluent interface
         */
        // TODO: A validation if passed resource locations are correct would be good
        default BlackBoxTestFinalGivenInterface andSourceFiles(String... resources) {
            return andSourceFiles(Arrays.stream(resources).map(e -> JavaFileObjectUtils.readFromResource(e)).toArray(JavaFileObject[]::new));
        }

        /**
         * Adds a source file by providing a fully qualified class name and the class code provided as a String.
         * @param className the fully qualified name of the class
         * @param content the source code of the class as a string. Of course fully qualified class name must match className parameter.
         * @return the next fluent interface
         */
        default BlackBoxTestFinalGivenInterface andSourceFile(String className, String content) {
            return andSourceFiles(JavaFileObjectUtils.readFromString(className, content));
        }


    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface BlackBoxTestFinalGivenInterface {

        /**
         * Adds source files as JavaFileObjects.
         * The {@link JavaFileObjectUtils} class can be used to provide source files.
         * @param sourceFile the source files to use during compilation
         * @return the next fluent interface
         */
        BlackBoxTestFinalGivenInterface andSourceFiles(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.ADD) JavaFileObject... sourceFile);

        /**
         * Add sources files by reading them from resource Strings.
         * @param resources the resource paths used to read the source files from
         * @return the next fluent interface
         */
        default BlackBoxTestFinalGivenInterface andSourceFiles(@NotNull String... resources) {
            return andSourceFiles(Arrays.stream(resources).map(e -> JavaFileObjectUtils.readFromResource(e)).toArray(String[]::new));
        }

        /**
         * Adds a source file by providing a fully qualified class name and the class code provided as a String.
         * @param className the fully qualified name of the class
         * @param content the source code of the class as a string. Of course fully qualified class name must match className parameter.
         * @return the next fluent interface
         */
        default BlackBoxTestFinalGivenInterface andSourceFile(String className, String content) {
            return andSourceFiles(JavaFileObjectUtils.readFromString(className, content));
        }

        /**
         * Use compiler options.
         * Options with parameters can, but must not be split over two consecutive Strings.
         * Those options can be put in one single String (e.g. "-source 1.7" or "-target 1.7").
         *
         * @param compilerOptions the options to use
         * @return the next builder instance
         */
        BlackBoxTestFinalGivenInterface andUseCompilerOptions(@FluentApiBackingBeanMapping(value = "compilerOptions") @NotNull String... compilerOptions);

        /**
         * Defines modules used during compilation.
         * This configuration will be ignored for Java versions &lt; 9.
         *
         * @param modules The modules to use during compilation
         * @return the next builder instance
         */
        BlackBoxTestFinalGivenInterface andUseModules(@FluentApiBackingBeanMapping(value = "modules") @NotNull String... modules);

        /**
         * Traverses to the compilation result validation section.
         * @return the next builder instance
         */
        CompilerTestInterface whenCompiled();

        /**
         * Executes test without explicitly checking the outcome.
         */
        @FluentApiCommand(ExecuteTestCommand.class)
        @FluentApiImplicitValue(id="compilationSucceeded", value = "true")
        void executeTest();

    }

    // --------------------------------------------------------------------
    // Unit Test Interfaces
    // --------------------------------------------------------------------

    @FluentApiInterface(CompilerTestBB.class)
    public interface UnitTestRootInterface {

        /**
         * Traverses to given section of unit test.
         * Allows to add source files, to define compiler options and java modules.
         * @return
         */
        UnitTestGivenInterface given();

        /**
         * Starts when block for unit test.
         *
         * This allows pass in of an Element or a processor instance before defining the un it test.
         *
         * @return the next fluent api instance
         */
        UnitTestWhenInterface when();

        /**
         * Directly defines unit test. Disables passing in of processor and Element.
         *
         * @return the next fluent api instance
         */
        CompilerTestInterface when(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET) @NotNull UnitTestWithoutPassIn unitTest);

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface UnitTestGivenInterface {

        /**
         * Use compiler options.
         * Options with parameters can, but must not be split over two consecutive Strings.
         * Those options can be put in one single String (e.g. "-source 1.7" or "-target 1.7").
         *
         * @param compilerOptions the options to use
         * @return the next builder instance
         */
        UnitTestGivenInterface useCompilerOptions(@FluentApiBackingBeanMapping(value = "compilerOptions") @NotNull String... compilerOptions);

        /**
         * Defines modules used during compilation.
         * This configuration will be ignored for Java versions &lt; 9.
         *
         * @param modules The modules to use during compilation
         * @return the next builder instance
         */
        UnitTestGivenInterface useModules(@FluentApiBackingBeanMapping(value = "modules") @NotNull String... modules);

        /**
         * Convenience method to add multiple source files via resource strings.
         * @param resources the source file resource strings
         * @return the next builder instance
         */
        default UnitTestGivenInterface useSourceFiles(String ... resources) {
            UnitTestGivenInterface next = this;
            if (resources != null) {
                for (String resource : resources) {
                    next = next.useSourceFile(resource);
                }
            }
            return next;
        }

        /**
         * Adds a source file by its resource path string.
         * @param resource the resource path string
         * @return the next fluent interface
         */
        default UnitTestGivenInterface useSourceFile(String resource) {
            return useSourceFile(JavaFileObjectUtils.readFromResource(resource));
        }

        /**
         * Adds a source file by its fully qualified class name and a source string.
          * @param className the fully qualified class name
         * @param content the source code of the class as a String
         * @return the next fluent interface
         */
        default UnitTestGivenInterface useSourceFile(String className, String content) {
            return useSourceFile(JavaFileObjectUtils.readFromString(className, content));
        }

        /**
         * Adds a source file as a JavaFileObject.
         * Use {@link JavaFileObjectUtils} to provide JavaFileObjects.
         * @param sourceFile the source file
         * @return the next fluent interface
         */
        UnitTestGivenInterface useSourceFile(@FluentApiBackingBeanMapping(value = "sourceFiles") @NotNull JavaFileObject sourceFile);


        /**
         * Traverse to when section to pass in an Element or a processor instant and to define the unit test scenario.
         * @return the next fluent interface
         */
        UnitTestWhenInterface when();


    }


    @FluentApiInterface(CompilerTestBB.class)
    public interface UnitTestWhenInterface {

        /**
         * Passes in an Element.
         * @return the next fluent interface
         */
        PassInElementInterface passInElement();

        /**
         * Passes in a processor instance.
         * Processor class must have an accessible noarg constructor.
         * @param processorClass The processor type to create the instance for - must not be null and have an accessible no args constructor
         * @return the next fluent interface
         * @param <PROCESSOR_CLASS> The processor type
         */
        <PROCESSOR_CLASS extends Processor> PassInProcessorInterface<PROCESSOR_CLASS> passInProcessor(@FluentApiBackingBeanMapping(value = "passedInProcessor", target = TargetBackingBean.NEXT) @NotNull Class<PROCESSOR_CLASS> processorClass);

        /**
         * Disables pass in of Element ant Processor and directly defines the unit test
         * @param unitTest the unit test
         * @return the next fluent interface
         */
        CompilerTestInterface unitTestWithoutPassIn(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET, target = TargetBackingBean.NEXT) @NotNull UnitTestWithoutPassIn unitTest);

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface PassInElementInterface {

        /**
         * Passes in an Element via a source String and fully qualified class name.
         * The class must contain exactly one Element annotated with {@link PassIn} annotation.
         * Passed in Element type can be declared via method type variable: prevInstance.&lt;TypeElement&gt;fromSourceString(...)
         * @param className The fully qualified class name
         * @param sourceString the source string of the class
         * @return the next fluent interface
         * @param <ELEMENT_TYPE> The type of the passed in Element
         */
        default <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromSourceString(String className, String sourceString) {
            return this.<ELEMENT_TYPE>fromJavaFileObject(JavaFileObjectUtils.readFromString(className, sourceString));
        }

        /**
         * Passes in an Element via the resource path String  of a source file.
         * The source file must contain exactly one Element annotated with {@link PassIn} annotation.
         * Passed in Element type can be declared via method type variable: prevInstance.&lt;TypeElement&gt;fromSourceFile(...)
         * @param resourceName The resource path String of the source file
         * @return the next fluent interface
         * @param <ELEMENT_TYPE> The type of the passed in Element
         */

        default <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromSourceFile(String resourceName) {
            return this.<ELEMENT_TYPE>fromJavaFileObject(JavaFileObjectUtils.readFromResource(resourceName));
        }

        /**
         * Passes in an Element via a JavaFileObject representing a source file.
         * The JavaFileObject must contain exactly one Element annotated with {@link PassIn} annotation.
         * Passed in Element type can be declared via method type variable: prevInstance.&lt;TypeElement&gt;fromJavaFileObject(...)
         * @param javaFileObject The JavaFileObject representing a source file
         * @return the next fluent interface
         * @param <ELEMENT_TYPE> The type of the passed in Element
         */
        @FluentApiImplicitValue(id = "passInElement", value = "true", target = TargetBackingBean.NEXT)
        <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromJavaFileObject(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.SET) @NotNull JavaFileObject javaFileObject);

        /**
         * The Element will be passed in via a compiled class.
         * Class must contain exactly one element annotated with {@link PassIn} annotation.
         * Warning: All non-runtime annotations on the element won't be accessible!!!
         * Use source file approach if you need to support non-runtime annotations.
         * @param classToScanForElement the class to scan for Element
         * @return the next fluent interface
         * @param <ELEMENT_TYPE> the element type
         */
        @FluentApiImplicitValue(id = "passInElement", value = "true", target = TargetBackingBean.NEXT)
        <ELEMENT_TYPE extends Element> PassInElementAndProcessorInterface<ELEMENT_TYPE> fromClass(@FluentApiBackingBeanMapping(value = "passedInClass", target = TargetBackingBean.NEXT) @NotNull Class<?> classToScanForElement);

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

        /**
         * Passes in an Element.
         * @return the next fluent interface
         */
        PassInProcessorAndElementInterface<PROCESSOR_CLASS> andPassInElement();

        /**
         * Define unit test.
         * @param unitTest the Unit test
         * @return the next fluent inteface
         */
        @FluentApiImplicitValue(id = "getPassInType", value = "PROCESSOR", target = TargetBackingBean.NEXT)
        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        CompilerTestInterface intoUnitTest(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET, target = TargetBackingBean.NEXT) @NotNull UnitTestForTestingAnnotationProcessorsWithoutPassIn<PROCESSOR_CLASS> unitTest);
    }


    @FluentApiInterface(PassInConfigurationBB.class)
    public interface PassInElementAndProcessorInterface<ELEMENT_TYPE extends Element> {

        /**
         * Pass in processor instance.
         * @param processorClass The processor type. The type must have an accessible noarg constructor
         * @return the next fluent interface
         * @param <PROCESSOR_TYPE> The type of the processor
         */
        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        <PROCESSOR_TYPE extends Processor> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_TYPE> andPassInProcessor(@FluentApiBackingBeanMapping(value = "passedInProcessor") @NotNull Class<PROCESSOR_TYPE> processorClass);

        /**
         * Defines the unit test.
         * @param unitTest the unit test
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "getPassInType", value = "ELEMENT", target = TargetBackingBean.NEXT)
        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        CompilerTestInterface intoUnitTest(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET, target = TargetBackingBean.NEXT) @ NotNull UnitTest<ELEMENT_TYPE> unitTest);
    }

    @FluentApiInterface(PassInConfigurationBB.class)
    public interface PassInProcessorAndElementInterface<PROCESSOR_CLASS extends Processor> {

        /**
         * Passes in an Element via a source String and fully qualified class name.
         * The class must contain exactly one Element annotated with {@link PassIn} annotation.
         * Passed in Element type can be declared via method type variable: prevInstance.&lt;TypeElement&gt;fromSourceString(...)
         * @param className The fully qualified class name
         * @param sourceString the source string of the class
         * @return the next fluent interface
         * @param <ELEMENT_TYPE> The type of the passed in Element
         */
        default <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromSourceString(String className, String sourceString) {
            return this.<ELEMENT_TYPE>fromJavaFileObject(JavaFileObjectUtils.readFromString(className, sourceString));
        }

        /**
         * Passes in an Element via the resource path String  of a source file.
         * The source file must contain exactly one Element annotated with {@link PassIn} annotation.
         * Passed in Element type can be declared via method type variable: prevInstance.&lt;TypeElement&gt;fromSourceFile(...)
         * @param resourceName The resource path String of the source file
         * @return the next fluent interface
         * @param <ELEMENT_TYPE> The type of the passed in Element
         */
        default <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromSourceFile(String resourceName) {
            return this.<ELEMENT_TYPE>fromJavaFileObject(JavaFileObjectUtils.readFromResource(resourceName));
        }

        /**
         * Passes in an Element via a JavaFileObject representing a source file.
         * The JavaFileObject must contain exactly one Element annotated with {@link PassIn} annotation.
         * Passed in Element type can be declared via method type variable: prevInstance.&lt;TypeElement&gt;fromJavaFileObject(...)
         * @param javaFileObject The JavaFileObject representing a source file
         * @return the next fluent interface
         * @param <ELEMENT_TYPE> The type of the passed in Element
         */
        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        @FluentApiImplicitValue(id = "passInElement", value = "true")
        <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromJavaFileObject(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.SET, target = TargetBackingBean.NEXT) @NotNull JavaFileObject javaFileObject);

        /**
         * The Element will be passed in via a compiled class.
         * Class must contain exactly one element annotated with {@link PassIn} annotation.
         * Warning: All non-runtime annotations on the element won't be accessible!!!
         * Use source file approach if you need to support non-runtime annotations.
         * @param classToScanForElement the class to scan for Element
         * @return the next fluent interface
         * @param <ELEMENT_TYPE> the element type
         */

        @FluentApiParentBackingBeanMapping(value = "passInConfiguration")
        @FluentApiImplicitValue(id = "passInElement", value = "true")
        <ELEMENT_TYPE extends Element> UnitTestWhenWithPassedInElementAndProcessorInterface<ELEMENT_TYPE, PROCESSOR_CLASS> fromClass(@FluentApiBackingBeanMapping(value = "passedInClass", target = TargetBackingBean.THIS) @NotNull Class<?> classToScanForElement);

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

        /**
         * Define unit test.
         * @param unitTest the unit test
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "getPassInType", value = "ELEMENT_AND_PROCESSOR", target = TargetBackingBean.NEXT)
        CompilerTestInterface intoUnitTest(@FluentApiBackingBeanMapping(value = "unitTest", action = MappingAction.SET) @NotNull UnitTestForTestingAnnotationProcessors<P, E> unitTest);
    }


    // --------------------------------------------------------------------
    // Common "then" Interfaces
    // --------------------------------------------------------------------

    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilerTestInterface {

        /**
         * Traverse to section to define checks
         * @return the next fluent interface
         */
        CompilerTestOutcomeInterface thenExpectThat();

        /**
         * Executes the test.
         * All AssertionError triggered inside the unit test will bepassed through to your unit test framework.
         */
        @FluentApiCommand(ExecuteTestCommand.class)
        void executeTest();


    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilerTestOutcomeInterface {

        /**
         * Expect tbe compilation to be successful.
         * This means that all (generated) source file are compiled successfully and that no error compiler message has been written.
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "compilationSucceeded", value = "true")
        CompilerTestExpectAndThatInterface compilationSucceeds();

        /**
         * Expect the compilation to fail.
         * This means that either a (generated) source file couldn't be compiled ot that an error compiler message has been written.
         * @return the next fluent imnterface
         */
        @FluentApiImplicitValue(id = "compilationSucceeded", value = "false")
        CompilerTestExpectAndThatInterface compilationFails();

        /**
         * Expect an Exception to be thrown
         * @param exception The exception to check for
         * @return the next fluent interface
         */
        CompilerTestExpectAndThatInterface exceptionIsThrown(@FluentApiBackingBeanMapping(value = "exceptionIsThrown") Class<? extends Exception> exception);


    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilerTestExpectAndThatInterface {

        /**
         * Add another expectation.
         * @return the next fluent interface
         */
        CompilerTestExpectThatInterface andThat();

        /**
         * Executes the test.
         */
        @FluentApiCommand(ExecuteTestCommand.class)
        void executeTest();

    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilerTestExpectThatInterface {

        /**
         * Adds check that generated class exists or doesn't exist.
         * @param className the fully qualified class name
         * @return the next fluent interface
         */
        default GeneratedJavaFileObjectCheck generatedClass(String className) {
            return javaFileObject(StandardLocation.CLASS_OUTPUT, className, JavaFileObject.Kind.CLASS);
        }

        /**
         * Adds check that generated source file exists or doesn't exist.
         * @param className the fully qualified class name
         * @return the next fluent interface
         */
        default GeneratedJavaFileObjectCheck generatedSourceFile(String className) {
            return javaFileObject(StandardLocation.SOURCE_OUTPUT, className, JavaFileObject.Kind.SOURCE);
        }

        /**
         * Adds check that generated resource file exists or doesn't exist.
         * @param packageName the package of the resource file
         * @param relativeName  The relative name of the resource file
         * @return the next fluent interface
         */
        default GeneratedFileObjectCheck generatedResourceFile(String packageName,
                                                               String relativeName) {
            return fileObject(StandardLocation.CLASS_OUTPUT, packageName, relativeName);
        }

        /**
         * Check if a JavaFileObject exists.
         * @param location The location of the JavaFileObject
         * @param className The fully qualified name of the JavaFileObject
         * @param kind The kind of the JavaFileObject
         * @return the next fluent interface
         */
        GeneratedJavaFileObjectCheck javaFileObject(
                @FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.NEXT) @NotNull JavaFileManager.Location location,
                @FluentApiBackingBeanMapping(value = "className", target = TargetBackingBean.NEXT) @NotNull String className,
                @FluentApiBackingBeanMapping(value = "kind", target = TargetBackingBean.NEXT) @NotNull JavaFileObject.Kind kind);

        /**
         * Check if a FileObject exists.
         * @param location The location of the FileObject
         * @param packageName The package name of the FileObject
         * @param relativeName The kind of the FileObject
         * @return the next fluent interface
         */
        GeneratedFileObjectCheck fileObject(
                @FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.NEXT) @NotNull JavaFileManager.Location location,
                @FluentApiBackingBeanMapping(value = "packageName", target = TargetBackingBean.NEXT) @NotNull String packageName,
                @FluentApiBackingBeanMapping(value = "relativeName", target = TargetBackingBean.NEXT) @NotNull String relativeName);


        /**
         * Adds compiler message checks.
         * @return the next fluent interface
         */
        CompilerMessageCheckMessageType compilerMessage();


    }


    @FluentApiInterface(CompilerMessageCheckBB.class)
    public interface CompilerMessageCheckMessageType {

        /**
         * Expects a compiler message of kind NOTE.
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "NOTE")
        CompilerMessageCheckComparisonType ofKindNote();

        /**
         * Expects a compiler message of kind WARNING.
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "WARNING")
        CompilerMessageCheckComparisonType ofKindWarning();

        /**
         * Expects a compiler message of kind MANDATORY WARNING.
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "MANDATORY_WARNING")
        CompilerMessageCheckComparisonType ofKindMandatoryWarning();

        /**
         * Expects a compiler message of kind ERROR.
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "ERROR")
        CompilerMessageCheckComparisonType ofKindError();

    }

    @FluentApiInterface(CompilerMessageCheckBB.class)
    public interface CompilerMessageCheckComparisonType {

        /**
         * Check if a compiler message contains passed snippet Strings.
         * @param text text snippets that must be present in one compiler message
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "CONTAINS")
        @FluentApiParentBackingBeanMapping(value = "compileMessageChecks")
        CompilerTestExpectAndThatInterface contains(
                @FluentApiBackingBeanMapping(value = "searchString", action = MappingAction.SET) @NotNull String... text
        );

        /**
         * Check if a compiler message exists that equals passed String.
         * @param text the text to check for
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "EQUALS")
        @FluentApiParentBackingBeanMapping(value = "compileMessageChecks")
        CompilerTestExpectAndThatInterface equals(
                @FluentApiBackingBeanMapping(value = "searchString", action = MappingAction.SET) @NotNull String text
        );

        /**
         * Expect compiler message at line.
         * @param line the line number the compiler message must be located
         * @return the next fluent interface
         */
        CompilerMessageCheckComparisonType atLine(@FluentApiBackingBeanMapping(value = "atLine") int line);

        /**
         * Expect compiler message at column.
         * @param column the column number the compiler message must be located
         * @return the next fluent interface
         */
        CompilerMessageCheckComparisonType atColumn(@FluentApiBackingBeanMapping(value = "atColumn") int column);

        /**
         * Expect compiler message in source file.
         * @param atSource the source file the compiler message bound with.
         * @return the next fluent interface
         */
        CompilerMessageCheckComparisonType atSource(@FluentApiBackingBeanMapping(value = "atSource") String atSource);

        /**
         * The locale used for compiler message.
         * @param locale the expected locale of the compiler message
         * @return the next fluent interface
         */
        CompilerMessageCheckComparisonType withLocale(@FluentApiBackingBeanMapping(value = "withLocale") Locale locale);

    }

    @FluentApiInterface(GeneratedJavaFileObjectCheckBB.class)
    public interface GeneratedJavaFileObjectCheck {

        /**
         * Expect a file to be existent.
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "checkType", value = "EXISTS")
        @FluentApiParentBackingBeanMapping(value = "javaFileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface exists();

        /**
         * Expect a file to be nonexistent.
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "checkType", value = "DOESNT_EXIST")
        @FluentApiParentBackingBeanMapping(value = "javaFileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface doesntExist();

        /**
         * Expect a file to be binary equal to passed JavaFileObject.
         * @param expectedJavaFileObject the expected file used for comparison
         * @return the next fluent interface
         */
        default CompilerTestExpectAndThatInterface equals(JavaFileObject expectedJavaFileObject) {
            return matches(ExpectedFileObjectMatcherKind.BINARY, expectedJavaFileObject);
        }

        /**
         * Expect that a file matches the passed JavaFileObject and Matcher kind.
         * @param expectedJavaFileObject the expected file used for comparison
         * @return the next fluent interface
         */
        default CompilerTestExpectAndThatInterface matches(ExpectedFileObjectMatcherKind expectedFileObjectMatcherKind, JavaFileObject expectedJavaFileObject) {
            return matches(expectedFileObjectMatcherKind.createMatcher(expectedJavaFileObject));
        }

        /**
         * Expect that a file exists that matches the passed GeneratedFileObjectMatcher.
         * @param generatedJavaFileObjectCheck the GeneratedFileObjectMatcher to use
         * @return the next fluent interface
         */
        @FluentApiImplicitValue(id = "checkType", value = "EXISTS")
        @FluentApiParentBackingBeanMapping(value = "javaFileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface matches(@FluentApiBackingBeanMapping(value = "generatedFileObjectMatcher") GeneratedFileObjectMatcher generatedJavaFileObjectCheck);

    }

    @FluentApiInterface(GeneratedFileObjectCheckBB.class)
    public interface GeneratedFileObjectCheck {

        /**
         * Expect the file to exist.
         * @return The next fluent interface
         */
        @FluentApiImplicitValue(id = "checkType", value = "EXISTS")
        @FluentApiParentBackingBeanMapping(value = "fileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface exists();

        /**
         * Expect the file to be nonexistent.
         * @return The next fluent interface
         */
        @FluentApiImplicitValue(id = "checkType", value = "DOESNT_EXIST")
        @FluentApiParentBackingBeanMapping(value = "fileObjectChecks", action = MappingAction.ADD)
        CompilerTestExpectAndThatInterface doesntExist();

        /**
         * Expect that a file exists that matches the passed FileObject.
         * Matcher ignores line endings.
         * @param expectedFileObject the FileOject used for comparison
         * @return the next fluent inmterface
         */
        default CompilerTestExpectAndThatInterface equals(FileObject expectedFileObject) {
            return matches(ExpectedFileObjectMatcherKind.TEXT_IGNORE_LINE_ENDINGS, expectedFileObject);
        }

        /**
         * Expect that a file exists that matches the passed FileObject.
         * @param expectedFileObjectMatcherKind The matcher kind
         * @param expectedFileObject the FileOject used for comparison
         * @return the next fluent interface
         */
        default CompilerTestExpectAndThatInterface matches(ExpectedFileObjectMatcherKind expectedFileObjectMatcherKind, FileObject expectedFileObject) {
            return matches(expectedFileObjectMatcherKind.createMatcher(expectedFileObject));
        }

        /**
         * Expect a file that matches all passed GeneratedFileObjectMatchers.
         * @param generatedJavaFileObjectCheck the GeneratedFileObjectMatchers to use
         * @return the next fluent interface
         */
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