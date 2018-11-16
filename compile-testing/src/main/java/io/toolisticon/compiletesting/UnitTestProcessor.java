package io.toolisticon.compiletesting;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Interface that is used during unit test creation. It allows setting up a unit test without the need to create a valid annotation processor.
 */
public interface UnitTestProcessor {

    /**
     * The unit test method.
     *
     * @param processingEnvironment the processingEnvironment
     * @param typeElement               the element the underlying annotation processor is applied on
     */
    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement);

}
