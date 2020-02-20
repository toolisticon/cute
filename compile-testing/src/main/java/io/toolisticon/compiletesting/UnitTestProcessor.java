package io.toolisticon.compiletesting;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * Interface that is used during unit test creation. It allows setting up a unit test without the need to create a valid annotation processor.
 * Please use {@link UnitTestProcessorForTestingAnnotationProcessors} if you want to unit test annotation processor methods.
 */
public interface UnitTestProcessor<ELEMENT_TYPE extends Element> {

    /**
     * The unit test method.
     *
     * @param processingEnvironment the processingEnvironment
     * @param element               the element the underlying annotation processor is applied on
     */
    void unitTest(ProcessingEnvironment processingEnvironment, ELEMENT_TYPE element);

}
