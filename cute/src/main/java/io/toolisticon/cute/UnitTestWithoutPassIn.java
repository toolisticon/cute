package io.toolisticon.cute;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * Interface that is used during unit test creation. It allows setting up a unit test without the need to create a valid annotation processor.
 * <p>* <p>
 * Please use {@link UnitTestForTestingAnnotationProcessors} if you want to unit test annotation processor methods.
 */
public interface UnitTestWithoutPassIn extends UnitTestBase{

    /**
     * The unit test method.
     *
     * @param processingEnvironment the processingEnvironment
     */
    void unitTest(ProcessingEnvironment processingEnvironment);

}
