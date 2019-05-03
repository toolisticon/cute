package io.toolisticon.compiletesting.impl.java9;

import java.lang.reflect.Method;

public class OptionalWrapper<T> extends AbstractJava9BaseWrapper {

    final static Class OPTIONAL_CLASS = AbstractJava9BaseWrapper.getClassForName("java.util.Optional");

    final static Method METHOD_GET = AbstractJava9BaseWrapper.getMethod(OPTIONAL_CLASS, "get");
    final static Method METHOD_IS_PRESENT = AbstractJava9BaseWrapper.getMethod(OPTIONAL_CLASS, "isPresent");

    public OptionalWrapper(Object optionalInstance) {

        super(OPTIONAL_CLASS, optionalInstance);

    }

    public T get() {

        Object result = isPresent() ?
                AbstractJava9BaseWrapper.invoke(METHOD_GET, getWrappedInstance(), Object.class) : null;

        return (T) result;

    }

    public boolean isPresent() {
        return AbstractJava9BaseWrapper.invoke(METHOD_IS_PRESENT, getWrappedInstance(), Boolean.class);
    }


}
