package io.toolisticon.cute;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnitTestAnnotationProcessorClassWithPassIn<ELEMENT_TYPE extends Element> extends AbstractUnitTestAnnotationProcessorWithPassIn {

    private final Set<String> supportedAnnotationTypes = new HashSet<>();






    /**
     * The unit test processor instance to use.
     */
    private final UnitTest<ELEMENT_TYPE> unitTest;


    public UnitTestAnnotationProcessorClassWithPassIn(Class<?> classToScan, Class<? extends Annotation> annotationTypeUsedForScan, UnitTest<ELEMENT_TYPE> unitTest) {
        super(classToScan,annotationTypeUsedForScan);
        this.supportedAnnotationTypes.add(TestAnnotation.class.getCanonicalName());
        this.unitTest = unitTest;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // just try to execute tests if annotation is processed == annotations size is 1
        if (!roundEnv.processingOver() && annotations.size() == 1) {
            Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(TestAnnotation.class);

            if (set.size() == 1) {
                try {
                    unitTest.unitTest(this.processingEnv, (ELEMENT_TYPE) getPassedInElement());
                } catch (ClassCastException e) {
                    throw new FailingAssertionException(Constants.Messages.UNIT_TEST_PRECONDITION_INCOMPATIBLE_ELEMENT_TYPE.produceMessage());
                }
            } else {

                throw new IllegalStateException(Constants.Messages.UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT.produceMessage(TestAnnotation.class.getCanonicalName()));

            }
        }
        return false;
    }

    protected void triggerError(String message) {

        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);

    }



}