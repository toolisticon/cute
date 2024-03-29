package io.toolisticon.compiletesting.test;

import io.toolisticon.cute.PassIn;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Test class for annotation processor tools.
 */
@PassIn
public class AnnotationProcessorUnitTestTestClass {

    private String privateField;
    protected String protectedField;
    String packagePrivateField;
    public String publicField;
    public final String publicFinalField = "";
    public static String publicStaticField;
    public transient String publicTransientField;

    enum TestEnum1 {
        TEST11, TEST12;
    }

    public enum TestEnum2 {
        TEST21, TEST22;
    }

    public static class EmbeddedStaticClass {

    }

    public Comparator<Long> comparatorWithAnonymousClass = new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            return 0;
        }
    };


    public class EmbeddedClass {

    }

    public class EmbeddedClassWithNoNoargConstructor {

        public EmbeddedClassWithNoNoargConstructor(String abs) {

        }

    }

    public abstract class AbstractEmbeddedClass {

        public abstract void abstractMethod();

    }

    {
        int x = 0;
    }

    static {
        int y = 0;
    }

    public AnnotationProcessorUnitTestTestClass() {

    }

    public AnnotationProcessorUnitTestTestClass(String withParameter) {

    }

    public String methodWithReturnTypeAndParameters(Boolean first, String second) {
        return "";
    }


    public int testGenericsOnParameter(Map<String, Comparator<Long>> o1, Map<? extends StringBuilder, Comparator<? super List<?>>> o2) {
        return 0;
    }

}
