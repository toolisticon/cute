# TOOLISTICON Compile-Testing

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.toolisticon.compiletesting/compiletesting-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.toolisticon.compiletesting/compiletesting-parent)
[![Build Status](https://api.travis-ci.org/toolisticon/compile-testing.svg)](https://travis-ci.org/toolisticon/compile-testing)
[![codecov](https://codecov.io/gh/toolisticon/compile-testing/branch/master/graph/badge.svg)](https://codecov.io/gh/toolisticon/compile-testing)

# Why you should use this project?
Testing of annotation processors can be a very difficult task. 
Mocking of annotations processor related tools given by the JDK and the compile time model is very difficult.
It's easier to define testcases as source files and test your processor during a normal compilation process.

This framework allows you to setup such kinds of test in an easy way.

# Features

- allows compile time tests supporting the most relevant test frameworks (junit4, junit5, testng)
- simple, but powerful fluent api
- almost dependency-less - just one runtime dependencies (to spiap-api, will be removed soon)
- supports all Java versions 6,7,8,>=9
- supported by most IDEs and compilers (Intellij, Eclipse, NetBeans)

# How does it work?
Simply add the following dependencies to your project to be able to use this testing framework.

     <dependencies>

        <!-- Compile testing framework, supports junit 4 per default-->
            <dependency>
            <groupId>io.toolisticon.compiletesting</groupId>
            <artifactId>compile-testing</artifactId>
            <version>${currentVersion}</version>
            <scope>test</scope>
        </dependency>

        <!-- optional : ONE extension dependency if your are not using junit 4 -->
        <dependency>
            <groupId>io.toolisticon.compiletesting</groupId>
            <artifactId>${extension-junit5, extension-testng}</artifactId>
            <version>${currentVersion}</version>
            <scope>test</scope>
        </dependency>

     </dependencies>

     
# Tests types

There are two types of tests: Unit tests and Compilation (or Integration) Tests.

For both test types you can test:

- if compilation was successful or not
- if certain messages have been created during compilation
- if certain java files (sources and classes) and resource files have been created
- if your annotation processor really has been applied (this is implicitly done)

## Compilation tests

Compilation test allow you to define testcase source classes and to apply your processor on it.

    // a junit 4 example 
    // test class should be in same package like your unit under test
    // tested methods must be package visible
    @Test
    public void exampleCompilationTest() {

        CompileTestBuilder.compilationTest()
                .addSources(JavaFileObjectUtils.readFromResource("/exampletestcase/Testcase1.java"))
                .addProcessors(YourProcessorUnderTest.class)
                .compilationShouldSucceed()
                .expectedWarningMessages("WARNING SNIPPET(will check if a warning exists that contains passed string)")
                .expectedJavaFileObjectExists(
                        StandardLocation.SOURCE_OUTPUT,
                        "your.test.package.GeneratedFile", 
                        JavaFileObject.Kind.SOURCE, 
                        JavaFileObjectUtils.readFromString("package your.test.package;\npublic class GeneratedFile{}"))
                .testCompilation();
    }

It implicitly checks if your annotation processor has been applied and triggers an AssertionError if not.



## Unit tests

Usually - if you are developing annotation processors - your code is likely to rely on the tools provided to the annotation processor via the ProcessingEnvironment like Filer, Messager, Types and Elements.
These classes and the Java compile time model are hard to mock. That why unit testing is usually very hard to do.
This library helps you to execute unit test at compile time, giving you the ProcessingEnvironment's tools and access to the compile time model for free.

The unit test concept provided by this library uses a default source file and applies a default annotation processor on it. 
Your unit test code can be declared as part of the fluent api:

    // a junit 4 example 
    // test class should be in same package like your unit under test
    // tested methods must be package visible
    @Test
    public void exampleUnitTest() {
       
        CompileTestBuilder.unitTest().useProcessor(
                new UnitTestProcessor() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                        // Your unit test code
                        SampleProcesssor sampleProcesssor = new SampleProcesssor();
                        sampleProcesssor.init(processingEnvironment);

                        String result = sampleProcesssor.yourMethodToTest("ABC");

                        // AssertionErrors will be passed through your external unit test function
                        MatcherAssert.assertThat(result, Matchers.is("EXPECTED RESULT"));
                        
                    }
                })
                .compilationShouldSucceed()
                .expectedWarningMessages("WARNING SNIPPET(will check if a warning exists that contains passed string)")
                .testCompilation();
                
    }
 
 Besides that it's also possible to test if an expected exception has been thrown.
 You have compile time model access for all compiled and not compiled classes residing in your classpath, which allows you to setup test classes easily.
 
 
 
# Projects using this toolkit library

- [SPI-AP](https://github.com/toolisticon/SPI-Annotation-Processor) : Annotation processor for generation of spi service locator files

# Alternatives

- [google compile-testing](https://github.com/google/compile-testing): Another library for testing annotation processor. It has some drawbacks (binds a lot of common 3rd party libraries, lacking compatibility to different Java versions, no documentation) 

# Contributing

We welcome any kind of suggestions and pull requests.

## Building and developing compile-testing project

The compile-testing is built using Maven (at least version 3.0.0).
A simple import of the pom in your IDE should get you up and running. To build the compile-testing project on the commandline, just run `mvn` or `mvn clean install`

## Requirements

The likelihood of a pull request being used rises with the following properties:

- You have used a feature branch.
- You have included a test that demonstrates the functionality added or fixed.
- You adhered to the [code conventions](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html).

## Contributions

- (2018) Tobias Stamann (Holisticon AG)

## Sponsoring

This project is sponsored and supported by [holisticon AG](http://www.holisticon.de/)

![Holisticon AG](/docs/assets/img/sponsors/holisticon-logo.png)

# License

This project is released under the revised [BSD License](LICENSE).

