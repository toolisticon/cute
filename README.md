# TOOLISTICON CUTE - Compile-Time Unit Testing

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.toolisticon.cute/cute/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.toolisticon.cute/cute)
![Build Status](https://github.com/toolisticon/cute/workflows/default/badge.svg)
[![codecov](https://codecov.io/gh/toolisticon/cute/branch/master/graph/badge.svg)](https://codecov.io/gh/toolisticon/cute)

# Why you should use this project?
Testing of annotation processors can be a very difficult task.
Usually annotation procecssors are tighly bound to the compile time model of Java and are relying on tools provided by the java compiler during the compilation process.

Mocking of both tools and the compile time model is a very difficult task.
It's much easier to define testcases as source files and to test your processors during the normal compilation process.

This compile testing framework allows you to to do this and additionally supports you to provide unit tests for your annotation processor related code in an easy way.

# Features

- allows compile time tests supporting the most relevant test frameworks (junit4, junit5, testng, ...)
- simple, but powerful fluent api (immutable)
- supports all Java versions >=7 (including support for java 9 modules)
- Enables you to debug annotation processors during compilation tests
- provides useful information for analysis of failing tests:
   - error and warning messages
   - unexpected exceptions
   - writes generated files to filesystem
   - test configuration
- works with most IDEs and compilers
- dependency-less - binds no 3rd party libraries (therefore no versioning conflicts with your application)

# How does it work?
Simply add the following dependencies to your project to be able to use this testing framework.

```xml
<dependencies>

   <!-- Compile testing framework -->
   <dependency>
       <groupId>io.toolisticon.cute</groupId>
       <artifactId>cute</artifactId>
       <version>${currentVersion}</version>
       <scope>test</scope>
   </dependency>
    
   <!-- 
        Legacy API : Only add it if you don't want to use the new API.
                     or migration to new API isn't an option.
    -->
    <dependency>
        <groupId>io.toolisticon.cute</groupId>
        <artifactId>cute-legacy</artifactId>
        <version>${currentVersion}</version>
        <scope>test</scope>
    </dependency>
   
   <!-- 
       optional : only needed if you want to trigger assertion 
                  errors via your unit test framework
       Per default assertion errors are thrown and handled as java.lang.AssertionError by most unit test frameworks.
   -->
   <dependency>
       <groupId>io.toolisticon.cute</groupId>
       <artifactId>${extension-junit4, extension-junit5, extension-testng}</artifactId>
       <version>${currentVersion}</version>
       <scope>test</scope>
   </dependency>

</dependencies>
```
     
# Tests Types

There are two types of tests: Unit tests and Compilation (or Integration) Tests.

For both test types you can test

- if compilation was successful or not
- if certain messages have been created during compilation
- if certain java files (sources and classes) or resource files have been created
- if your annotation processor really has been applied (this is implicitly done)

The CompileTestBuilder always returns immutable CompileTestBuilder instances. 
So it's safe to initialize a base CompileTestBuilder instance once in a testclass and to refine it further in the testcases.

## Black-Box-Compilation tests

Black-Box-Compilation test allow you to define testcase source files and to apply your processor on it during its compilation.

```java
 Cute.blackBoxTest()
    .given()
        .processors(YourProcessorUnderTest.class)
        .andSourceFiles("/exampletestcase/Testcase1.java")
    .whenCompiled()
    .thenExpectThat()
        .compilationSucceeds()
        .andThat()
            .compilerMessage()
            .ofKindWarning()
            .atSource("/exampletestcase/Testcase1.java")
            .atLine(10L)
            .atColumn(20L)
            .contains("WARNING SNIPPET(will check if a warning exists that contains passed string)")
        .andThat()
            .generatedSourceFile("your.test.package.GeneratedFile")
            .matches(
                CuteApi.ExpectedFileObjectMatcherKind.BINARY,
                JavaFileObjectUtils.readFromString("package your.test.package;\npublic class GeneratedFile{}")
            )
        .executeTest();
```

Additionally, to the explicitly configured assertions it implicitly checks if your annotation processor has been applied and triggers an AssertionError if not.

## Unit tests

Usually - if you are developing annotation processors - your code is likely to rely on the tools provided to the annotation processor via the ProcessingEnvironment like Filer, Messager, Types and Elements.
These classes and the Java compile time model are hard to mock. That's why unit testing is usually very hard to do.
This library helps you to execute unit test at compile time, giving you the ProcessingEnvironment's tools and access to the compile time model for free.

The unit test concept provided by this library uses a default source file and applies a default annotation processor on it. 
Your unit test code can be declared via the fluent api:

```java
// a junit 4 example 
// test class should be in same package like your unit under test
// tested methods must be package visible
@Test
public void exampleUnitTest() {

    Cute.unitTest()
        .when()
        .passInProcessor(SampleProcesssor.class)
        .intoUnitTest( new UnitTestForTestingAnnotationProcessorsWithoutPassIn<SampleProcesssor>() {
            @Override
            public void unitTest(SampleProcesssor unit, ProcessingEnvironment processingEnvironment) {
            
                // Processor's init() method was called by cute framework
                String result = unit.yourMethodToTest("ABC");
            
                // AssertionErrors will be passed through your external unit test function
                MatcherAssert.assertThat(result, Matchers.is("EXPECTED RESULT"));
            
            }
        })
        .thenExpectThat()
            .compilationSucceeds()
            .andThat()
            .compilerMessage()
                .ofKindWarning()
                .contains("WARNING SNIPPET(will check if a warning exists that contains passed string)")
        .executeTest();

}
```
 
Besides that it's also possible to add an assertion if an expected exception has been thrown.

Additionally, you have compile time model access for all classes residing in your classpath (including all test classes), which allows you to setup test classes easily, for example by adding classes to your src/test/java folder or by adding static inner classes to your unit test class.

## Passing elements and annotation processor into unit tests 
Another cute feature is that it's possible to easily pass in elements and even annotation processors into the unit test. 

For passing in elements all you need to do is to create a static class next to your unit tests and to add the _PassIn_ annotation on the element you want to pass in.
The static class then can be used as parameter in _defineTestWithPassedInElement_ method.

Additionally, you can pass in an annotation processor by using the annotation processor class  as a parameter in _defineTestWithPassedInElement_ method.
An instance of the annotation processor will be created and initialized.

```java
private static class PassedInElement {
    // Add your custom code like
    @PassIn
    void yourMethodToBePassedIn(){
    }
}

    // Example 1 : Pass in element
@Test
public void yourUnitTestWithPassedInElement() {

    Cute
        .unitTest()
        .when().passInElement().<ExecutableElement>fromClass(PassedInElement.class)
        .intoUnitTest( new UnitTest<ExecutableElement>() {
            @Override
            public void unitTest(ProcessingEnvironment processingEnvironment, ExecutableElement element) {

                // put your unit test code
                // ....

            }
        })
        .thenExpectThat()
        .compilationSucceeds()
        .executeTest();


}

// Example 2 : Pass in annotation processor and element
// The processor will be instantiated and initialized
@Test
public void yourUnitTestWithPassedInElementAndProcessor() {

    Cute
        .unitTest()
        .when()
        .passInProcessor(YourProcessorToTest.class)
        .andPassInElement().<ExecutableElement>fromClass(PassedInElement.class)
        .intoUnitTest( new UnitTestForTestingAnnotationProcessors<YourProcessorToTest, ExecutableElement>() {
            @Override
            public void unitTest(YourProcessorToTest unit, ProcessingEnvironment processingEnvironment, ExecutableElement element) {

                // put your unit test code
                String result = unit.methodToTest(element);
                MatcherAssert.assertThat(result, Matchers.is("EXPECTED RESULT"));

            }
        })
        .thenExpectThat()
        .compilationSucceeds()
        .executeTest();


}
```
 
# Projects using this toolkit library

- [Annotation processor toolkit](https://github.com/toolisticon/annotation-processor-toolkit) : Toolkit that allows you to build annotation processors in a more comfortable way
- [SPI-AP](https://github.com/toolisticon/SPI-Annotation-Processor) : Annotation processor for generation of spi service locator files

# Alternatives

- [google compile-testing](https://github.com/google/compile-testing): Another library for testing annotation processor at compile time. 

# Contributing

We welcome any kind of suggestions and pull requests.

## Building and developing compile-testing project

The compile-testing is built using Maven via bundled maven wrapper.
A simple import of the pom in your IDE should get you up and running. To build the compile-testing project on the commandline, just run `mvnw` or `mvnw clean install`

## Requirements

The likelihood of a pull request being used rises with the following properties:

- You have used a feature branch.
- You have included a test that demonstrates the functionality added or fixed.
- You adhered to the [code conventions](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html).

## Contributions

- (2018) Tobias Stamann (Holisticon AG)

# License

This project is released under the revised [MIT License](LICENSE).

