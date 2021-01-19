package io.toolisticon.cute;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

public class UnitTestAnnotationProcessorClassWithPassIn<ELEMENT_TYPE extends Element> extends AbstractUnitTestAnnotationProcessorWithPassIn {


    /**
     * The unit test processor instance to use.
     */
    private final UnitTest<ELEMENT_TYPE> unitTest;


    public UnitTestAnnotationProcessorClassWithPassIn(Class<?> classToScan, Class<? extends Annotation> annotationTypeUsedForScan, UnitTest<ELEMENT_TYPE> unitTest) {
        super(TestAnnotation.class, classToScan, annotationTypeUsedForScan);
        this.unitTest = unitTest;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // just try to execute tests if annotation is processed == annotations size is 1
        if (!roundEnv.processingOver() && annotations.size() == 1) {

            try {
                unitTest.unitTest(this.processingEnv, (ELEMENT_TYPE) getPassedInElement());
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