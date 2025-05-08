package io.toolisticon.cute;

import io.toolisticon.cute.matchers.CoreGeneratedFileObjectMatchers;
import io.toolisticon.fluapigen.api.FluentApi;
import io.toolisticon.fluapigen.api.FluentApiBackingBean;
import io.toolisticon.fluapigen.api.FluentApiBackingBeanMapping;
import io.toolisticon.fluapigen.api.FluentApiCommand;
import io.toolisticon.fluapigen.api.FluentApiConverter;
import io.toolisticon.fluapigen.api.FluentApiImplicitValue;
import io.toolisticon.fluapigen.api.FluentApiInlineBackingBeanMapping;
import io.toolisticon.fluapigen.api.FluentApiInterface;
import io.toolisticon.fluapigen.api.FluentApiParentBackingBeanMapping;
import io.toolisticon.fluapigen.api.FluentApiRoot;
import io.toolisticon.fluapigen.api.MappingAction;
import io.toolisticon.fluapigen.api.TargetBackingBean;
import io.toolisticon.fluapigen.validation.api.HasNoArgConstructor;
import io.toolisticon.fluapigen.validation.api.NotNull;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Locale;

@FluentApi("CompileTestBuilder")
public class CompileTestBuilderApi {

    /**
     * ------------------------------------------------------------------------
     * BackingBeans
     * ------------------------------------------------------------------------
     */

    @FluentApiBackingBean
    public interface CompilerTestBB extends CuteApi.CompilerTestBB {


    }


    @FluentApiBackingBean
    public interface ExceptionCheckBB extends CuteApi.ExceptionCheckBB {


    }

    
    @FluentApiBackingBean
    public interface PassInConfigurationBB extends CuteApi.PassInConfigurationBB {

    }

    @FluentApiBackingBean
    public interface CompilerMessageCheckBB extends CuteApi.CompilerMessageCheckBB {

    }


    @FluentApiBackingBean
    public interface GeneratedJavaFileObjectCheckBB extends CuteApi.GeneratedJavaFileObjectCheckBB {

    }

    @FluentApiBackingBean
    public interface GeneratedFileObjectCheckBB extends CuteApi.GeneratedFileObjectCheckBB {

    }

    /**
     * ------------------------------------------------------------------------
     * Fluent Interfaces
     * ------------------------------------------------------------------------
     */


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


    /**
     * Abstract base builder class.
     * Contains common configurations.
     *
     * @param <TEST_BUILDER>         The test-builder interface
     * @param <COMPILERMESSAGECHECK> The compiler message check type
     *                               The implementing type passed to base class
     */
    public interface BasicBuilder<TEST_BUILDER extends BasicBuilder<TEST_BUILDER, COMPILERMESSAGECHECK>, COMPILERMESSAGECHECK extends CompileMessageCheckBuilder<TEST_BUILDER, COMPILERMESSAGECHECK>> {

        /**
         * Compilation is expected to be successful.
         *
         * @return the next builder instance
         */
        @FluentApiImplicitValue(id = "compilationSucceeded", value = "true")
        TEST_BUILDER compilationShouldSucceed();

        /**
         * Compilation is expected to be failing.
         *
         * @return the next builder instance
         */
        @FluentApiImplicitValue(id = "compilationSucceeded", value = "false")
        TEST_BUILDER compilationShouldFail();

        /**
         * Use compiler options.
         * Options with parameters can, but must not be split over two consecutive Strings.
         * Those options can be put in one single String (e.g. "-source 1.7" or "-target 1.7").
         *
         * @param compilerOptions the options to use
         * @return the next builder instance
         */
        TEST_BUILDER useCompilerOptions(@FluentApiBackingBeanMapping("compilerOptions") String... compilerOptions);


        /**
         * Defines modules used during compilation.
         * This configuration will be ignored for Java versions &lt; 9.
         *
         * @param modules The modules to use during compilation
         * @return the next builder instance
         */
        TEST_BUILDER useModules(@FluentApiBackingBeanMapping("modules") String... modules);

        /**
         * Starts a sub builder for adding a check for an error compiler message.
         *
         * @return an immutable builder instance for creating a complex compiler message check
         */
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "ERROR", target = TargetBackingBean.NEXT)
        COMPILERMESSAGECHECK expectErrorMessage();

        /**
         * Starts a sub builder for adding a check for a mandatory warning compiler message.
         *
         * @return an immutable builder instance for creating a complex compiler message check
         */
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "MANDATORY_WARNING", target = TargetBackingBean.NEXT)
        COMPILERMESSAGECHECK expectMandatoryWarningMessage();

        /**
         * Starts a sub builder for adding a check for a warning compiler message.
         *
         * @return an immutable builder instance for creating a complex compiler message check
         */
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "WARNING", target = TargetBackingBean.NEXT)
        COMPILERMESSAGECHECK expectWarningMessage();

        /**
         * Starts a sub builder for adding a check for a note compiler message.
         *
         * @return an immutable builder instance for creating a complex compiler message check
         */
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "NOTE", target = TargetBackingBean.NEXT)
        COMPILERMESSAGECHECK expectNoteMessage();

        /**
         * Adds some warning checks.
         *
         * @param warningChecks the warning checks to set, null values will be ignored.
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("compileMessageChecks")
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "WARNING", target = TargetBackingBean.INLINE)
        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "CONTAINS", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectWarningMessageThatContains(@FluentApiBackingBeanMapping(value = "searchString", target = TargetBackingBean.INLINE) @NotNull String... warningChecks);

        /**
         * Adds some mandatory warning checks.
         *
         * @param mandatoryWarningChecks the mandatory warning checks to set, null values will be ignored.
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("compileMessageChecks")
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "MANDATORY_WARNING", target = TargetBackingBean.INLINE)
        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "CONTAINS", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectMandatoryWarningMessageThatContains(@FluentApiBackingBeanMapping(value = "searchString", target = TargetBackingBean.INLINE) @NotNull String... mandatoryWarningChecks);

        /**
         * Adds some error checks.
         *
         * @param errorChecksToSet the error checks to set, null values will be ignored.
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("compileMessageChecks")
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "ERROR", target = TargetBackingBean.INLINE)
        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "CONTAINS", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectErrorMessageThatContains(@FluentApiBackingBeanMapping(value = "searchString", target = TargetBackingBean.INLINE) @NotNull String... errorChecksToSet);

        /**
         * Adds some notes checks.
         *
         * @param noteChecksToSet the notes checks to set, null values will be ignored.
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("compileMessageChecks")
        @FluentApiImplicitValue(id = "compilerMessageScope", value = "NOTE", target = TargetBackingBean.INLINE)
        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "CONTAINS", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectNoteMessageThatContains(@FluentApiBackingBeanMapping(value = "searchString", target = TargetBackingBean.INLINE) @NotNull String... noteChecksToSet);


        /**
         * Adds a check if a specific generated FileObject exists.
         *
         * @param location     the location (usually a {@link StandardLocation})
         * @param packageName  the package name
         * @param relativeName the relative name to the passed package
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("fileObjectChecks")
        @FluentApiImplicitValue(id = "checkType", value = "EXISTS", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectThatFileObjectExists(@FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.INLINE) JavaFileManager.Location location, @FluentApiBackingBeanMapping(value = "packageName", target = TargetBackingBean.INLINE) String packageName, @FluentApiBackingBeanMapping(value = "relativeName", target = TargetBackingBean.INLINE) String relativeName);

        /**
         * Adds a check if a specific generated FileObject exists (uses binary comparision).
         * Additionally, checks if files are equal if passed expectedFileObject is not null.
         *
         * @param location           the location (usually from javax.tools.StandardLocation)
         * @param packageName        the package name
         * @param relativeName       the package relative name
         * @param expectedFileObject the file used for comparison of content
         * @return the next builder instance
         */

        default TEST_BUILDER expectThatFileObjectExists(JavaFileManager.Location location, String packageName, String relativeName, FileObject expectedFileObject) {
            return expectThatFileObjectExists(location, packageName, relativeName, CoreGeneratedFileObjectMatchers.createBinaryMatcher(expectedFileObject));
        }

        /**
         * Adds a check if a specific generated FileObject exists.
         * Additionally, checks if files are equal if passed expectedFileObject is not null.
         * Comparison algorithm can be selected by matcherKind parameter
         *
         * @param location           the location (usually from javax.tools.StandardLocation)
         * @param packageName        the package name
         * @param relativeName       the package relative name
         * @param matcherKind        the matcher kind
         * @param expectedFileObject the file used for comparison of content
         * @return the next builder instance
         */

        default TEST_BUILDER expectThatFileObjectExists(JavaFileManager.Location location, String packageName, String relativeName, ExpectedFileObjectMatcherKind matcherKind, FileObject expectedFileObject) {
            if (matcherKind == null || expectedFileObject == null) {
                throw new IllegalArgumentException("Passed matcherKind and expectedFileObject must not be null!");
            }
            return expectThatFileObjectExists(location, packageName, relativeName, matcherKind.createMatcher(expectedFileObject));
        }

        /**
         * Adds a check if a specific generated FileObject exists.
         * Additionally, checks if file object matches with passed matcher.
         *
         * @param location                   the location (usually from javax.tools.StandardLocation)
         * @param packageName                the package name
         * @param relativeName               the package relative name
         * @param generatedFileObjectMatcher the matcher to use
         * @return the next builder instance
         */

        @FluentApiInlineBackingBeanMapping("fileObjectChecks")
        @FluentApiImplicitValue(id = "checkType", value = "EXISTS", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectThatFileObjectExists(@FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.INLINE) JavaFileManager.Location location, @FluentApiBackingBeanMapping(value = "packageName", target = TargetBackingBean.INLINE) String packageName, @FluentApiBackingBeanMapping(value = "relativeName", target = TargetBackingBean.INLINE) String relativeName, @FluentApiBackingBeanMapping(value = "generatedFileObjectMatcher", target = TargetBackingBean.INLINE) GeneratedFileObjectMatcher... generatedFileObjectMatcher);


        /**
         * Adds a check if a specific generated FileObject doesn't exist.
         *
         * @param location     the location (usually from javax.tools.StandardLocation)
         * @param packageName  the package name
         * @param relativeName the package relative name
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("fileObjectChecks")
        @FluentApiImplicitValue(id = "checkType", value = "DOESNT_EXIST", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectThatFileObjectDoesntExist(@FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.INLINE) JavaFileManager.Location location, @FluentApiBackingBeanMapping(value = "packageName", target = TargetBackingBean.INLINE) String packageName, @FluentApiBackingBeanMapping(value = "relativeName", target = TargetBackingBean.INLINE) String relativeName);

        /**
         * Checks if a generated source file exists.
         *
         * @param className the full qualified name of the class
         * @return the next builder instance
         */
        default TEST_BUILDER expectThatGeneratedSourceFileExists(String className) {
            return expectThatJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT, className, JavaFileObject.Kind.SOURCE);
        }

        /**
         * Checks if a generated source file exists and if it matches passed JavaFileObject by using binary comparision.
         * Additionally, checks if files are equal if passed expectedJavaFileObject is not null.‚
         *
         * @param className              the full qualified name of the class
         * @param expectedJavaFileObject the file used for comparision of content
         * @return the next builder instance
         */
        default TEST_BUILDER expectThatGeneratedSourceFileExists(String className, JavaFileObject expectedJavaFileObject) {
            return expectThatGeneratedSourceFileExists(className, CoreGeneratedFileObjectMatchers.createBinaryMatcher(expectedJavaFileObject));
        }

        /**
         * Adds a check if a specific class file exists.
         *
         * @param className the class name
         * @return the next builder instance
         */
        default TEST_BUILDER expectThatGeneratedClassExists(String className) {
            return expectThatJavaFileObjectExists(StandardLocation.CLASS_OUTPUT, className, JavaFileObject.Kind.CLASS);
        }

        /**
         * Adds a check if a specific generated source file exists.
         * Additionally, checks if java file object matches with passed matcher.
         *
         * @param className                    the class name
         * @param generatedJavaFileObjectCheck the matcher to use
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("javaFileObjectChecks")
        @FluentApiImplicitValue(id = "checkType", value = "EXISTS", target = TargetBackingBean.INLINE)
        @FluentApiImplicitValue(id = "location", value = "SOURCE_OUTPUT", converter = StandardLocationStringToLocationConverter.class, target = TargetBackingBean.INLINE)
        @FluentApiImplicitValue(id = "kind", value = "SOURCE", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectThatGeneratedSourceFileExists(@FluentApiBackingBeanMapping(value = "className", target = TargetBackingBean.INLINE) String className, @FluentApiBackingBeanMapping(value = "generatedFileObjectMatcher", target = TargetBackingBean.INLINE) GeneratedFileObjectMatcher generatedJavaFileObjectCheck);

        /**
         * Adds a check if a specific JavaFileObject doesn't exist.
         *
         * @param className the class name
         * @return the next builder instance
         */

        default TEST_BUILDER expectThatGeneratedSourceFileDoesntExist(String className) {
            return expectThatJavaFileObjectDoesntExist(StandardLocation.SOURCE_OUTPUT, className, JavaFileObject.Kind.SOURCE);
        }

        /**
         * Adds a check if a specific JavaFileObject exists.
         *
         * @param location  the location (usually from javax.tools.StandardLocation)
         * @param className the class name
         * @param kind      the kind of the JavaFileObject
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("javaFileObjectChecks")
        @FluentApiImplicitValue(id = "checkType", value = "EXISTS", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectThatJavaFileObjectExists(@FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.INLINE) JavaFileManager.Location location, @FluentApiBackingBeanMapping(value = "className", target = TargetBackingBean.INLINE) String className, @FluentApiBackingBeanMapping(value = "kind", target = TargetBackingBean.INLINE) JavaFileObject.Kind kind);


        /**
         * Adds a check if a specific generated JavaFileObject exists (uses binary comparision).
         * Additionally, checks if files are equal if passed expectedJavaFileObject is not null.
         *
         * @param location               the location (usually from javax.tools.StandardLocation)
         * @param className              the class name
         * @param kind                   the kind of the JavaFileObject
         * @param expectedJavaFileObject the file used for comparision of content
         * @return the next builder instance
         */

        default TEST_BUILDER expectThatJavaFileObjectExists(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, JavaFileObject expectedJavaFileObject) {
            return expectThatJavaFileObjectExists(location, className, kind, ExpectedFileObjectMatcherKind.BINARY.createMatcher(expectedJavaFileObject));
        }

        /**
         * Adds a check if a specific generated JavaFileObject exists.
         * Additionally, checks if files are equal if passed expectedJavaFileObject is not null.
         *
         * @param location               the location (usually from javax.tools.StandardLocation)
         * @param className              the class name
         * @param kind                   the kind of the JavaFileObject
         * @param expectedJavaFileObject the file used for comparision of content
         * @return the next builder instance
         */
        default TEST_BUILDER expectThatJavaFileObjectExists(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, ExpectedFileObjectMatcherKind expectedFileObjectMatcherKind, JavaFileObject expectedJavaFileObject) {
            return expectThatJavaFileObject(CuteApi.FileObjectCheckType.EXISTS, location, className, kind, CoreGeneratedFileObjectMatchers.createBinaryMatcher(expectedJavaFileObject));
        }

        /**
         * Adds a check if a specific generated JavaFileObject exists.
         * Additionally, checks if java file object matches with passed matcher.
         *
         * @param location                     the location (usually a {@link StandardLocation})
         * @param className                    the class name
         * @param kind                         the kind of the JavaFileObject
         * @param generatedJavaFileObjectCheck the matcher to use
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("javaFileObjectChecks")
        @FluentApiImplicitValue(id = "checkType", value = "EXISTS", target = TargetBackingBean.INLINE)
        default TEST_BUILDER expectThatJavaFileObjectExists(@FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.INLINE) JavaFileManager.Location location, @FluentApiBackingBeanMapping(value = "className", target = TargetBackingBean.INLINE) String className, @FluentApiBackingBeanMapping(value = "kind", target = TargetBackingBean.INLINE) JavaFileObject.Kind kind, @FluentApiBackingBeanMapping(value = "generatedFileObjectMatcher", target = TargetBackingBean.INLINE) GeneratedFileObjectMatcher generatedJavaFileObjectCheck) {
            return expectThatJavaFileObject(CuteApi.FileObjectCheckType.EXISTS, location, className, kind, generatedJavaFileObjectCheck);
        }


        /**
         * Adds a check if a specific generated JavaFileObject exists or doesn't exist.
         * Additionally, checks if java file object matches with passed matcher.
         *
         * @param checkType                    the kind of check
         * @param location                     the location (usually a {@link StandardLocation})
         * @param className                    the class name
         * @param kind                         the kind of the JavaFileObject
         * @param generatedJavaFileObjectCheck the matcher to use
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("javaFileObjectChecks")
        TEST_BUILDER expectThatJavaFileObject(@FluentApiBackingBeanMapping(value = "checkType", target = TargetBackingBean.INLINE) CuteApi.FileObjectCheckType checkType, @FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.INLINE) JavaFileManager.Location location, @FluentApiBackingBeanMapping(value = "className", target = TargetBackingBean.INLINE) String className, @FluentApiBackingBeanMapping(value = "kind", target = TargetBackingBean.INLINE) JavaFileObject.Kind kind, @FluentApiBackingBeanMapping(value = "generatedFileObjectMatcher", target = TargetBackingBean.INLINE) GeneratedFileObjectMatcher generatedJavaFileObjectCheck);

        /**
         * Adds a check if a specific JavaFileObject doesn't exist.
         *
         * @param location  the location (usually from javax.tools.StandardLocation)
         * @param className the class name
         * @param kind      the kind of the JavaFileObject
         * @return the next builder instance
         */
        @FluentApiInlineBackingBeanMapping("javaFileObjectChecks")
        @FluentApiImplicitValue(id = "checkType", value = "DOESNT_EXIST", target = TargetBackingBean.INLINE)
        TEST_BUILDER expectThatJavaFileObjectDoesntExist(@FluentApiBackingBeanMapping(value = "location", target = TargetBackingBean.INLINE) JavaFileManager.Location location, @FluentApiBackingBeanMapping(value = "className", target = TargetBackingBean.INLINE) String className, @FluentApiBackingBeanMapping(value = "kind", target = TargetBackingBean.INLINE) JavaFileObject.Kind kind);

        /**
         * Created the compile-test configuration instance.
         *
         * @return the configuration instance
         */

        @FluentApiCommand(ClosingCommand.class)
        CompilerTestBB createCompileTestConfiguration();

        /**
         * Executes the compilation tests.
         *
         * @throws IllegalStateException if there's some invalid configuration
         */
        @FluentApiCommand(ExecuteTestCommand.class)
        void executeTest();


    }

    /**
     * Builder class used to create compile tests (== Integration tests).
     * Class is designed to produce immutable builder instances in its fluent api.
     */
    @FluentApiInterface(CompilerTestBB.class)
    public interface CompilationTestBuilder extends BasicBuilder<CompilationTestBuilder, CompilationTestCompileMessageCheckBuilder> {


        /**
         * Adds processors.
         *
         * @param processorTypes the processor types to use, processors must have a noarg constructor
         * @return the CompilationTestBuilder instance
         */
        CompilationTestBuilder addProcessors(@FluentApiBackingBeanMapping(value = "processors", converter = ProcessorConverter.class, action = MappingAction.ADD) @HasNoArgConstructor() Class<? extends Processor>... processorTypes);


        /**
         * Adds source files to compile to compilation test.
         *
         * @param sources the sources to use
         * @return the CompilationTestBuilder instance
         */
        CompilationTestBuilder addSources(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.ADD) JavaFileObject... sources);

        /**
         * Adds source files to compile to compilation test.
         * Sources will be read from resources.
         * Source file names must either end with ".java" or ".java.ct".
         *
         * @param sources the sources to use
         * @return the CompilationTestBuilder instance
         */
        default CompilationTestBuilder addSources(String... sources) {
            return addSources(Arrays.stream(sources).map(e -> JavaFileObjectUtils.readFromResource(e)).toArray(JavaFileObject[]::new));
        }

        /**
         * Add a source file for String.
         *
         * @param clazzName The package name
         * @param content   The content to check for
         * @return the CompilationTestBuilder instance
         */
        default CompilationTestBuilder addSource(String clazzName, String content) {
            return addSources(JavaFileObjectUtils.readFromString(clazzName, content));
        }
    }

    @FluentApiInterface(CompilerTestBB.class)
    public interface UnitTestBuilder extends BasicBuilder<UnitTestBuilder, UnitTestCompileMessageCheckBuilder> {

        /**
         * Sets the processor to use.
         * The processor should support {@link TestAnnotation} annotation processing if no custom source file is defined.
         * If custom source is used you need to define a processor that is going to process the source file.
         *
         * @param processor the processor to use
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         */
        UnitTestBuilder useProcessor(@FluentApiBackingBeanMapping(value = "processors", action = MappingAction.SET) @NotNull Class<Processor> processor);

        /**
         * Allows writing of unit tests.
         * You can pass in a {@link UnitTest} instance that contains your test code in it's unitTest method.
         * <p>
         * The {@link javax.annotation.processing.ProcessingEnvironment} and an Element of type ELEMENT_TYPE will passed to the UnitTestProcessor.unitTest method.
         * <p>
         * The {@link TestAnnotation} will be used to look up this Element during compilation.
         * <p>
         * So please make sure that the {@link TestAnnotation} is used exactly once, when you are using a custom source files
         *
         * @param unitTest       the processor to use
         * @param <ELEMENT_TYPE> The expected element type (Must be TypeElement, if no custom source files are used)
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        <ELEMENT_TYPE extends Element> UnitTestBuilder defineTest(@FluentApiBackingBeanMapping("unitTest") @NotNull UnitTest<ELEMENT_TYPE> unitTest);

        /**
         * Allows writing of unit tests.
         * You can pass in a {@link UnitTest} instance that contains your test code in it's unitTest method.
         * <p>
         * The {@link javax.annotation.processing.ProcessingEnvironment} and an Element of type ELEMENT_TYPE will passed to the UnitTestProcessor.unitTest method.
         * <p>
         * The passed customAnnotationTyoe will be used to look up this Element.
         * <p>
         * So please make sure that the customAnnotationType annotation is used exactly once in your custom source file that.
         *
         * @param customAnnotationType the annotation type to search the element for
         * @param unitTest             the processor to use
         * @param <ELEMENT_TYPE>       The expected element type (Must be TypeElement, if no custom source files are used)
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        @FluentApiInlineBackingBeanMapping("passInConfiguration")
        @FluentApiImplicitValue(id = "passInElement", value = "true", target = TargetBackingBean.INLINE)
        <ELEMENT_TYPE extends Element> UnitTestBuilder defineTest(
                @FluentApiBackingBeanMapping(value = "annotationToScanFor", target = TargetBackingBean.INLINE) @NotNull Class<? extends Annotation> customAnnotationType,
                @FluentApiBackingBeanMapping("unitTest") @NotNull UnitTest<ELEMENT_TYPE> unitTest);

        /**
         * Allows writing of unit tests.
         * You can pass in a {@link UnitTest} instance that contains your test code in it's unitTest method.
         * <p>
         * The {@link javax.annotation.processing.ProcessingEnvironment} and an Element of type ELEMENT_TYPE will passed to the UnitTestProcessor.unitTest method.
         * <p>
         * The {@link PassIn} will be used to look up this Element.
         * <p>
         *
         * @param classToScan    the class to search element annotated with {@link PassIn}
         * @param unitTest       the processor to use
         * @param <ELEMENT_TYPE> The expected element type (Must be TypeElement, if no custom source files are used)
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        default <ELEMENT_TYPE extends Element> UnitTestBuilder defineTestWithPassedInElement(Class<?> classToScan, UnitTest<ELEMENT_TYPE> unitTest) {
            return defineTestWithPassedInElement(classToScan, PassIn.class, unitTest);
        }

        /**
         * Allows writing of unit tests.
         * You can pass in a {@link UnitTest} instance that contains your test code in it's unitTest method.
         * <p>
         * The {@link javax.annotation.processing.ProcessingEnvironment} and an Element of type ELEMENT_TYPE will be passed to the UnitTestProcessor.unitTest method.
         * <p>
         * The {@link PassIn} will be used to look up this Element.
         * <p>
         *
         * @param classToScan        the class to search element annotated with annotationToSearch
         * @param annotationToSearch the annotation type to search for
         * @param unitTest           the processor to use
         * @param <ELEMENT_TYPE>     The expected element type (Must be TypeElement, if no custom source files are used)
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        @FluentApiInlineBackingBeanMapping("passInConfiguration")
        @FluentApiImplicitValue(id = "passInElement", value = "true", target = TargetBackingBean.INLINE)
        <ELEMENT_TYPE extends Element> UnitTestBuilder defineTestWithPassedInElement(
                @FluentApiBackingBeanMapping(value = "passedInClass", target = TargetBackingBean.INLINE) @NotNull Class<?> classToScan,
                @FluentApiBackingBeanMapping(value = "annotationToScanFor", target = TargetBackingBean.INLINE) @NotNull Class<? extends Annotation> annotationToSearch,
                @FluentApiBackingBeanMapping("unitTest") @NotNull UnitTest<ELEMENT_TYPE> unitTest);

        /**
         * Allows unit
         * Provides a specific processor instance that can be used for unit testing.
         * Additionally, it provides an element
         * The passed processor won't be used as an annotation processor during compilation.
         * <p>
         * It will internally use a generic processor that
         * The processor should support {@link TestAnnotation} annotation processing if no custom source file is defined.
         * If custom source is used make sure {@link TestAnnotation} is used somewhere in the custom source file to make sure if annotation processor is used.
         *
         * @param processorUnderTestClass                the Processor which should be provided as a
         * @param unitTestForTestingAnnotationProcessors the processor to use
         * @param <PROCESSOR_UNDER_TEST>                 The processor type under test
         * @param <ELEMENT_TYPE>                         The expected element type to be processed
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        @FluentApiInlineBackingBeanMapping("passInConfiguration")
        <PROCESSOR_UNDER_TEST extends Processor, ELEMENT_TYPE extends Element> UnitTestBuilder defineTest(
                @FluentApiBackingBeanMapping(value = "passedInProcessor", target = TargetBackingBean.INLINE) @NotNull @HasNoArgConstructor Class<PROCESSOR_UNDER_TEST> processorUnderTestClass,
                @FluentApiBackingBeanMapping("unitTest") @NotNull UnitTestForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST, ELEMENT_TYPE> unitTestForTestingAnnotationProcessors);

        /**
         * Sets the processor to use.
         * The processor should support annotation processing of passed annotation type if no custom source file is defined.
         * Please make sure to add a custom source files in which the customAnnotationType annotation is used exactly once.
         *
         * @param processorUnderTestClass                the Processor type
         * @param customAnnotationType                   the custom annotation used to search the element to pass be passed in
         * @param unitTestForTestingAnnotationProcessors the processor to use
         * @param <PROCESSOR_UNDER_TEST>                 The processor type under test
         * @param <ELEMENT_TYPE>                         The expected element type to be processed
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor or customAnnotationType is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        default <PROCESSOR_UNDER_TEST extends Processor, ELEMENT_TYPE extends Element> UnitTestBuilder defineTest(Class<PROCESSOR_UNDER_TEST> processorUnderTestClass, Class<? extends Annotation> customAnnotationType, UnitTestForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST, ELEMENT_TYPE> unitTestForTestingAnnotationProcessors) {
            return defineTestWithPassedInElement(processorUnderTestClass, null, customAnnotationType, unitTestForTestingAnnotationProcessors);
        }

        /**
         * Sets the processor to use.
         * The processor should support annotation processing of passed annotation type if no custom source file is defined.
         * Please make sure to add a custom source file in which the customAnnotationType annotation is used exactly once.
         *
         * @param processorUnderTestClass                the Processor type
         * @param classToScan                            the type to search the PassIn element
         * @param unitTestForTestingAnnotationProcessors the processor to use
         * @param <PROCESSOR_UNDER_TEST>                 The processor type under test
         * @param <ELEMENT_TYPE>                         The expected element type to be processed
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor or customAnnotationType is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        default <PROCESSOR_UNDER_TEST extends Processor, ELEMENT_TYPE extends Element> UnitTestBuilder defineTestWithPassedInElement(Class<PROCESSOR_UNDER_TEST> processorUnderTestClass, Class<?> classToScan, UnitTestForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST, ELEMENT_TYPE> unitTestForTestingAnnotationProcessors) {
            return defineTestWithPassedInElement(processorUnderTestClass, classToScan, PassIn.class, unitTestForTestingAnnotationProcessors);
        }

        /**
         * Sets the processor to use.
         * The processor should support annotation processing of passed annotation type if no custom source file is defined.
         * Please make sure to add a custom source file in which the customAnnotationType annotation is used exactly once.
         *
         * @param processorUnderTestClass                the Processor type
         * @param classToScan                            the type to search the PassIn element
         * @param annotationToSearch                     annotation to search
         * @param unitTestForTestingAnnotationProcessors the processor to use
         * @param <PROCESSOR_UNDER_TEST>                 The processor type under test
         * @param <ELEMENT_TYPE>                         The expected element type to be processed
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed processor or customAnnotationType is null.
         * @throws IllegalStateException    if more than one Element is found or if ELEMENT_TYPE doesn't match type of the found element
         */
        @FluentApiInlineBackingBeanMapping("passInConfiguration")
        @FluentApiImplicitValue(id = "passInElement", value = "true", target = TargetBackingBean.INLINE)
        <PROCESSOR_UNDER_TEST extends Processor, ELEMENT_TYPE extends Element> UnitTestBuilder defineTestWithPassedInElement(
                @FluentApiBackingBeanMapping(value = "passedInProcessor", target = TargetBackingBean.INLINE) Class<PROCESSOR_UNDER_TEST> processorUnderTestClass,
                @FluentApiBackingBeanMapping(value = "passedInClass", target = TargetBackingBean.INLINE) Class<?> classToScan,
                @FluentApiBackingBeanMapping(value = "annotationToScanFor", target = TargetBackingBean.INLINE) Class<? extends Annotation> annotationToSearch,
                @FluentApiBackingBeanMapping("unitTest") UnitTestForTestingAnnotationProcessors<PROCESSOR_UNDER_TEST, ELEMENT_TYPE> unitTestForTestingAnnotationProcessors
        );

        /**
         * Sets the source file used to apply processor on.
         * The source file must contain an annotation that is processed by the processor.
         *
         * @param source The source file to use
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed source is null.
         */
        UnitTestBuilder useSource(@FluentApiBackingBeanMapping(value = "sourceFiles", action = MappingAction.SET) @NotNull JavaFileObject source);

        /**
         * Sets the source file used to apply processor on.
         * The referenced resource file must contain an annotation that is processed by the processor.
         * The source file name must either end with ".java" or ".java.ct".
         *
         * @param resource The resource file to use
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed source is null.
         */
        default UnitTestBuilder useSource(String resource) {
            return useSource(JavaFileObjectUtils.readFromResource(resource));
        }

        /**
         * Sets the source file used to apply processor on.
         * The source file will be added from String.
         *
         * @param className The name of the file passed in as a class name (fqn or simple class name)
         * @return the UnitTestBuilder instance
         * @throws IllegalArgumentException if passed source is null.
         */
        default UnitTestBuilder useSource(String className, String content) {
            return useSource(JavaFileObjectUtils.readFromString(className, content));
        }

        /**
         * Sets an expected exception thrown in the unit test case.
         *
         * @param expectedException the exceptions expected to be thrown
         * @return the UnitTestBuilder instance
         */
        @FluentApiInlineBackingBeanMapping("exceptionChecks")
        UnitTestBuilder expectedThrownException(@FluentApiBackingBeanMapping(value = "exceptionIsThrown", target = TargetBackingBean.INLINE) Class<? extends Exception> expectedException);


        /**
         * {@inheritDoc}
         */

        @FluentApiCommand(ExecuteTestCommand.class)
        void executeTest();


    }

    /**
     * Fluent immutable builder for creation of complex compiler message checks.
     *
     * @param <COMPILETESTBUILDER>      The enclosing builder.
     * @param <COMPILERMESSAGEBUILDER>> The compiler message interface
     */

    public interface CompileMessageCheckBuilder<COMPILETESTBUILDER extends BasicBuilder<COMPILETESTBUILDER, COMPILERMESSAGEBUILDER>, COMPILERMESSAGEBUILDER extends CompileMessageCheckBuilder<COMPILETESTBUILDER, COMPILERMESSAGEBUILDER>> {

        /**
         * The line number to search the compiler message at.
         *
         * @param lineNumber the line number to check for.Line check will be skipped if passed lineNumber is null.
         * @return the next immutable builder instance
         */
        COMPILERMESSAGEBUILDER atLineNumber(@FluentApiBackingBeanMapping(value = "atLine", converter = FluentApiConverter.LongToIntegerConverter.class) Long lineNumber);

        /**
         * The column number to search the compiler message at.
         *
         * @param columnNumber the column number to check for. Column check will be skipped if passed columnNumber is null.
         * @return the next immutable builder instance
         */

        COMPILERMESSAGEBUILDER atColumnNumber(@FluentApiBackingBeanMapping(value = "atColumn", converter = FluentApiConverter.LongToIntegerConverter.class) Long columnNumber);

        /**
         * Do check for localized compiler message.
         *
         * @param locale the locale to use, or null for default locale.
         * @return the next immutable builder instance
         */
        COMPILERMESSAGEBUILDER withLocale(@FluentApiBackingBeanMapping("withLocale") Locale locale);

        /**
         * Do check if compiler message is linked for a specific source
         *
         * @param source the source
         * @return the next immutable builder instance
         */
        COMPILERMESSAGEBUILDER atSource(@FluentApiBackingBeanMapping("atSource") String source);

        /**
         * Check if a compiler message exists that contains the passed message token.
         * May be used for checking for message codes.
         *
         * @param expectedContainedMessageToken the message to search for
         * @return the next immutable builder instance of enclosing builder
         */
        @FluentApiParentBackingBeanMapping(value = "compileMessageChecks", action = MappingAction.ADD)
        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "CONTAINS")
        COMPILETESTBUILDER thatContains(@FluentApiBackingBeanMapping(value = "searchString") String expectedContainedMessageToken);

        /**
         * Check if a compiler message matches  the passed message string.
         *
         * @param expectedMessage the message to search for
         * @return the next immutable builder instance of enclosing builder
         */
        @FluentApiParentBackingBeanMapping(value = "compileMessageChecks", action = MappingAction.ADD)
        @FluentApiImplicitValue(id = "compilerMessageComparisonType", value = "EQUALS")
        COMPILETESTBUILDER thatIsEqualTo(@FluentApiBackingBeanMapping(value = "searchString") String expectedMessage);


    }

    @FluentApiInterface(CompilerMessageCheckBB.class)
    public interface UnitTestCompileMessageCheckBuilder extends CompileMessageCheckBuilder<UnitTestBuilder, UnitTestCompileMessageCheckBuilder> {

    }

    @FluentApiInterface(CompilerMessageCheckBB.class)
    public interface CompilationTestCompileMessageCheckBuilder extends CompileMessageCheckBuilder<CompilationTestBuilder, CompilationTestCompileMessageCheckBuilder> {

    }

    /**
     * Internal builder class for unit and compilation tests.
     */
    @FluentApiInterface(CompilerTestBB.class)
    @FluentApiRoot
    public interface TestTypeBuilder {

        /**
         * Does a compilation test.
         * You can freely  choose sources to compile and processors to use.
         *
         * @return the builder
         */
        @FluentApiImplicitValue(id = "testType", value = "BLACK_BOX")
        CompilationTestBuilder compilationTest();


        /**
         * Do a unit test.
         *
         * @return the UnitTestBuilder instance
         */
        @FluentApiImplicitValue(id = "testType", value = "UNIT")
        UnitTestBuilder unitTest();

    }


    /**
     * ------------------------------------------------------------------------
     * Commands
     * ------------------------------------------------------------------------
     */

    @FluentApiCommand
    public static class ClosingCommand {
        public static CompilerTestBB getConfig(CompilerTestBB compilerTestBB) {
            return compilerTestBB;
        }
    }

    @FluentApiCommand
    public static class ExecuteTestCommand {
        static void myCommand(CuteApi.CompilerTestBB backingBean) {
            if (backingBean.testType() == CuteApi.TestType.UNIT && backingBean.sourceFiles().size() == 0) {
                backingBean.sourceFiles().add(JavaFileObjectUtils.readFromResource("/AnnotationProcessorUnitTestClass.java"));
            }
            if (backingBean.testType() == CuteApi.TestType.BLACK_BOX && backingBean.sourceFiles().size() == 0) {
                throw new IllegalStateException("There must be at least one source file present to execute a black box test!");
            }
            new CompileTest(backingBean).executeTest();
        }
    }

    /**
     * ------------------------------------------------------------------------
     * Converters
     * ------------------------------------------------------------------------
     */

    public static class StandardLocationStringToLocationConverter implements FluentApiConverter<String, StandardLocation> {

        @Override
        public StandardLocation convert(String s) {
            return StandardLocation.valueOf(s);
        }

    }

    public static class ProcessorConverter implements FluentApiConverter<Class<? extends Processor>[], Class<Processor>[]> {

        @Override
        public Class<Processor>[] convert(Class<? extends Processor>[] processor) {
            return (Class<Processor>[]) processor;
        }

    }

}
