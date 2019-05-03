package io.toolisticon.compiletesting.impl.java9;

import java.lang.reflect.Method;

/**
 * Wrapper class for Java 8 Optional.
 */
public class OptionalWrapper<T> extends AbstractJava9BaseWrapper {

    final static Class OPTIONAL_CLASS = AbstractJava9BaseWrapper.getClassForName("java.util.Optional");

    final static Method METHOD_GET = AbstractJava9BaseWrapper.getMethod(OPTIONAL_CLASS, "get");
    final static Method METHOD_IS_PRESENT = AbstractJava9BaseWrapper.getMethod(OPTIONAL_CLASS, "isPresent");

    public OptionalWrapper(Object optionalInstance) {

        super(OPTIONAL_CLASS, optionalInstance);

    }

    /**
     * Calls Optional.get()
     * @return the result of Optional.get()
     */
    public T get() {

        Object result = isPresent() ?
                AbstractJava9BaseWrapper.invoke(METHOD_GET, getWrappedInstance(), Object.class) : null;

        return (T) result;

    }

    /**
     * Calls Optional.isPresent()
     * @return the result of Optional.isPresent()
     */
    public boolean isPresent() {
        return AbstractJava9BaseWrapper.invoke(METHOD_IS_PRESENT, getWrappedInstance(), Boolean.class);
    }


}
