package io.toolisticon.cute;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract base class that
 */
abstract class AbstractUnitTestAnnotationProcessorClass extends AbstractProcessor {

    private final Set<String> supportedAnnotationTypes = new HashSet<>();

    /**
     * The annotation type to search for.
     */
    protected Class<? extends Annotation> annotationTypeToUse;


    AbstractUnitTestAnnotationProcessorClass(Class<? extends Annotation> annotationTypeToUse) {
        this.annotationTypeToUse = annotationTypeToUse;
        this.supportedAnnotationTypes.add(annotationTypeToUse.getCanonicalName());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }


    protected <T extends Element> Element getElement(Set<T> elements) {

        if (elements.size() == 1) {
            return elements.iterator().next();
        } else  if (elements.isEmpty()) {
            throw new FailingAssertionException(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_ONE_ELEMENT.produceMessage());
        } else {

            List<T> filteredList = new ArrayList<>();
            for (T element : elements) {

                if (element.getAnnotation(PassIn.class) != null) {
                    filteredList.add(element);
                }

            }

            if (filteredList.isEmpty()) {
                throw new FailingAssertionException(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT.produceMessage(annotationTypeToUse.getCanonicalName()));
            } else if (filteredList.size() > 1) {
                throw new FailingAssertionException(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT_WITH_PASSIN_ANNOTATION.produceMessage());
            } else {
                return filteredList.get(0);
            }

        }

    }

}
