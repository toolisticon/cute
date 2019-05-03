package io.toolisticon.compiletesting.impl.java9;

import java.lang.reflect.Method;

public class ModuleDescriptorWrapper extends AbstractJava9BaseWrapper {

    final static Class MODULE_DESCRIPTOR_CLASS = AbstractJava9BaseWrapper.getClassForName("java.lang.module.ModuleDescriptor");


    final static Method METHOD_NAME = AbstractJava9BaseWrapper.getMethod(MODULE_DESCRIPTOR_CLASS, "name");
    final static Method METHOD_IS_AUTOMATIC = AbstractJava9BaseWrapper.getMethod(MODULE_DESCRIPTOR_CLASS, "isAutomatic");

    ModuleDescriptorWrapper(Object instance) {

        super(MODULE_DESCRIPTOR_CLASS, instance);

    }

    public String name() {

        return AbstractJava9BaseWrapper.invoke(METHOD_NAME, getWrappedInstance(), String.class);

    }

    public boolean isAutomatic() {

        return AbstractJava9BaseWrapper.invoke(METHOD_IS_AUTOMATIC, getWrappedInstance(), Boolean.class);

    }

}
