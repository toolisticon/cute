package io.toolisticon.compiletesting;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Interface that is used during unit test creation. It allows setting up a unit test without the need to create a valid annotation processor.
 * In comparision to {@link UnitTestProcessor} an instance of the processor under test will be created and it's init method will be called.
 *
 * @param <PROCESSOR> the processor to use
 * @param <ELEMENT_TYPE> the expected element type to be found
 */
public interface UnitTestProcessorForTestingAnnotationProcessors<PROCESSOR extends Processor, ELEMENT_TYPE extends Element> {

    /**
     * The unit test method.
     *
     * @param unit                  the initialized processor under test (initialized via init method)
     * @param processingEnvironment the processingEnvironment
     * @param element           the element the underlying annotation processor is applied on
     */
    public void unitTest(PROCESSOR unit, ProcessingEnvironment processingEnvironment, ELEMENT_TYPE element);
}
