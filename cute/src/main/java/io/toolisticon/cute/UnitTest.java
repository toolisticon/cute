package io.toolisticon.cute;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * Interface that is used during unit test creation. It allows setting up a unit test without the need to create a valid annotation processor.
 * <p>
 * It allows passing in of an element.
 * This is either the processed element or a passed in element depending on unit test setup.
 * <p>
 * Please use {@link UnitTestForTestingAnnotationProcessors} if you want to unit test annotation processor methods.
 */
public interface UnitTest<ELEMENT_TYPE extends Element> extends UnitTestBase {

    /**
     * The unit test method.
     *
     * @param processingEnvironment the processingEnvironment
     * @param element               the passed in element: either the processed element or the passed in element depending on unit test setup
     */
    void unitTest(ProcessingEnvironment processingEnvironment, ELEMENT_TYPE element);

}
