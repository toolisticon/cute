package io.toolisticon.compiletesting;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.TypeElement;

/**
 * Interface that is used during unit test creation. It allows setting up a unit test without the need to create a valid annotation processor.
 */
public interface UnitTestProcessorForTestingAnnotationProcessors<PROCESSOR extends Processor> {

    /**
     * The unit test method.
     *
     * @param unit                  the initialized processor under test
     * @param processingEnvironment the processingEnvironment
     * @param typeElement           the element the underlying annotation processor is applied on
     */
    public void unitTest(PROCESSOR unit, ProcessingEnvironment processingEnvironment, TypeElement typeElement);
}
