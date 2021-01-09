package io.toolisticon.cute;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for unit tests with passed in elements.
 */
abstract class AbstractUnitTestAnnotationProcessorWithPassIn extends AbstractProcessor {


    /**
     * The type to scan for passed in element.
     */
    protected final Class<?> classToScan;

    /**
     * The annotation type to search for.
     */
    private final Class<? extends Annotation> annotationTypeUsedForScan;

    AbstractUnitTestAnnotationProcessorWithPassIn(Class<?> classToScan, Class<? extends Annotation> annotationTypeUsedForScan) {
        this.classToScan = classToScan;
        this.annotationTypeUsedForScan = annotationTypeUsedForScan;
    }

    /**
     * Gets the passed in element.
     * @return the passed in element
     */
    protected Element getPassedInElement() {

        TypeElement element = processingEnv.getElementUtils().getTypeElement(classToScan.getCanonicalName());

        List<Element> allEnclosedElements = getAllSubElements(element);
        List<Element> filteredElements = filterByAnnotation(allEnclosedElements);

        if (filteredElements.size() != 1) {

            throw new FailingAssertionException(Constants.Messages.UNIT_TEST_PASS_IN_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT.produceMessage(annotationTypeUsedForScan.getCanonicalName(), classToScan.getCanonicalName()));

        }

        return filteredElements.get(0);

    }


    /**
     * Creates a list of all enclosed elements of class
     *
     * @param elementToScan the element to scan
     * @return a list containing all sub-elements and the passed in element
     */
    protected List<Element> getAllSubElements(Element elementToScan) {
        List<Element> result = new ArrayList<>();

        if (elementToScan != null) {
            result.add(elementToScan);

            if (elementToScan.getKind() == ElementKind.CONSTRUCTOR || elementToScan.getKind() == ElementKind.METHOD) {
                for (VariableElement parameters : ((ExecutableElement) elementToScan).getParameters()) {
                    result.add(parameters);
                }
            }

            for (Element enclosedElement : elementToScan.getEnclosedElements()) {
                result.addAll(getAllSubElements(enclosedElement));
            }

        }

        return result;
    }

    /**
     * Filters a list of elements by presence of annotationTypeUsedForScan.
     *
     * @param listToFilter the list to filter
     * @return a list containing all elements annotated with annotationTypeUsedForScan
     */
    protected List<Element> filterByAnnotation(List<Element> listToFilter) {
        List<Element> result = new ArrayList<>();

        for (Element element : listToFilter) {
            if (element.getAnnotation(annotationTypeUsedForScan) != null) {
                result.add(element);
            }
        }

        return result;
    }

}
