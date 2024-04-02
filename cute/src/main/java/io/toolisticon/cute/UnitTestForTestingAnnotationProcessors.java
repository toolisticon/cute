package io.toolisticon.cute;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;

/**
 * Interface that is used during unit test creation. It allows setting up a unit test without the need to create a valid annotation processor.
 * In comparison to {@link UnitTest} an instance of the processor under test will be created and it's init method will be called.
 *
 * @param <PROCESSOR>    the processor to use
 * @param <ELEMENT_TYPE> the expected element type to be found
 */
public interface UnitTestForTestingAnnotationProcessors<PROCESSOR extends Processor, ELEMENT_TYPE extends Element> extends UnitTestBase {

    /**
     * The unit test method.
     *
     * @param unit                  the initialized processor under test (initialized via init method)
     * @param processingEnvironment the processingEnvironment
     * @param element               the element the underlying annotation processor is applied on
     */
    void unitTest(PROCESSOR unit, ProcessingEnvironment processingEnvironment, ELEMENT_TYPE element);
}
