package io.toolisticon.compiletesting.impl.java9;

import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Wrapper class for Java 9 ModuleDescriptor.
 */
public class ModuleDescriptorWrapper extends AbstractJava9BaseWrapper {

    /**
     * The ModuleDescriptor class.
     */
    final static Class MODULE_DESCRIPTOR_CLASS = AbstractJava9BaseWrapper.getClassForName("java.lang.module.ModuleDescriptor");

    /**
     * the ModuleDescriptor.name() method.
     */
    final static Method METHOD_NAME = AbstractJava9BaseWrapper.getMethod(MODULE_DESCRIPTOR_CLASS, "name");
    /**
     * the ModuleDescriptor.read(InputStream) method.
     */
    final static Method METHOD_READ = AbstractJava9BaseWrapper.getMethod(MODULE_DESCRIPTOR_CLASS, "read", InputStream.class);


    /**
     * Hidden constructor
     *
     * @param instance the instance to wrap
     */
    ModuleDescriptorWrapper(Object instance) {

        super(MODULE_DESCRIPTOR_CLASS, instance);

    }

    /**
     * Get the name of the module
     *
     * @return
     */
    public String name() {

        return AbstractJava9BaseWrapper.invoke(METHOD_NAME, getWrappedInstance(), String.class);

    }

    /**
     * Reads the module descriptor from passed inputstream.
     *
     * @param inputStream the input stream to read module descriptor from
     * @return The wrapped ModuleDescriptor
     */
    public static ModuleDescriptorWrapper read(InputStream inputStream) {

        return new ModuleDescriptorWrapper(AbstractJava9BaseWrapper.invoke(METHOD_READ, null, Object.class, inputStream));

    }

}
