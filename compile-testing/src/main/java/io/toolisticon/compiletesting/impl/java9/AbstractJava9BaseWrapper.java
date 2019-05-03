package io.toolisticon.compiletesting.impl.java9;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * To support java 9 modules we must be able to access java 9 related api.
 * But the remain compatible with all previous java this must be done via reflection.
 *
 * This wrapper base class provides support to build wrappers for Java 9 API.
 */
abstract class AbstractJava9BaseWrapper {

    private final Class clazz;
    private final Object wrappedInstance;

    AbstractJava9BaseWrapper(Class clazz, Object wrappedInstance) {
        this.clazz = clazz;
        this.wrappedInstance = wrappedInstance;
    }


    public boolean isInstanceOf(Object instanceToCheck) {

        return instanceToCheck != null && this.clazz.isAssignableFrom(instanceToCheck.getClass());

    }

    Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class getClazz() {
        return clazz;
    }


    static Class getClassForName(String className) {

        if (Java9SupportCheck.UNSUPPORTED_JAVA_VERSION) {
            return null;
        }

        try {
            return Class.forName(className);
        } catch (Exception e) {
            throw new UnsupportedJavaVersionException();
        }


    }

    static Method getMethod(Class clazz, String methodName, Class... parameterTypes) {

        if (clazz == null) {
            throw new IllegalArgumentException("passed class must not be null");
        }

        if (methodName == null) {
            throw new IllegalArgumentException("passed method name must not be null");
        }

        // get ModuleFinderClass
        Method method = null;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;

    }

    /**
     * Invokes a method.
     * @param method the method to call
     * @param instance the instance to apply the method on, must be null for static methods
     * @param returnType the return type
     * @param parameters the parameters used to call the method
     * @param <T> The return type
     * @return the return value of the called method
     */
     static <T> T invoke(Method method, Object instance, Class<T> returnType, Object... parameters) {

        T result = null;

        try {
            result = (T) method.invoke(instance, parameters);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Passed method must be accessible", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Passed method isn't called correctly", e);
        } catch (IllegalArgumentException e) {

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("[");

            for (Object param : parameters) {
                stringBuilder.append(param != null ? param.getClass().getCanonicalName() : "<NULL>").append(",");
            }


            stringBuilder.append("]");

            throw new IllegalArgumentException("Passed parameter types doesnt match : " + stringBuilder.toString(), e);

        } catch (NullPointerException e) {

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("[");

            for (Object param : parameters) {
                stringBuilder.append(param != null ? param.toString() : "<NULL>").append(",");
            }


            stringBuilder.append("]");


            // rethrow
            throw new IllegalArgumentException("NPE: method:" + (method != null ? method.getName() : " <NULL>") + "; instance : " + (instance != null ? "<NOT NULL>" : "<NULL>") + " ; params:" + stringBuilder.toString(), e);

        }


        return result;
    }


}
