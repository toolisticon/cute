package io.toolisticon.cute;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;

/**
 * Interface that is used during unit test creation. It allows setting up a unit test without the need to create a valid annotation processor.
 * In comparison to {@link UnitTest} an instance of the processor under test will be created and it's init method will be called.
 *
 * @param <PROCESSOR> the processor to use
 */
public interface UnitTestForTestingAnnotationProcessorsWithoutPassIn<PROCESSOR extends Processor> extends UnitTestBase {

    /**
     * The unit test method.
     *
     * @param unit                  the initialized processor under test (initialized via init method)
     * @param processingEnvironment the processingEnvironment
     */
    void unitTest(PROCESSOR unit, ProcessingEnvironment processingEnvironment);
}

