package io.toolisticon.cute;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Base annotation processor for unit tests.
 *
 * @param <ELEMENT_TYPE> The expected type of the processed element
 */
public class UnitTestAnnotationProcessorClassWithoutPassIn extends AbstractUnitTestAnnotationProcessorClass {

    /**
     * The unit test processor instance to use.
     */
    private final UnitTestWithoutPassIn unitTest;


    public UnitTestAnnotationProcessorClassWithoutPassIn(Class<? extends Annotation> annotationTypeToUse, UnitTestWithoutPassIn unitTest) {
        super(annotationTypeToUse);

        this.unitTest = unitTest;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // just try to execute tests if annotation is processed == annotations size is 1
        if (!roundEnv.processingOver() && annotations.size() == 1) {
            Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(annotationTypeToUse);


            try {
                unitTest.unitTest(this.processingEnv);
            } catch (ClassCastException e) {
                if (e.getMessage() != null  && e.getMessage().contains("com.sun.tools.javac.code.Symbol$ClassSymbol")) {
                    throw new FailingAssertionException(Constants.Messages.UNIT_TEST_PRECONDITION_INCOMPATIBLE_ELEMENT_TYPE.produceMessage());
                } else {
                    throw e;
                }
            }

        }
        return false;
    }

}



