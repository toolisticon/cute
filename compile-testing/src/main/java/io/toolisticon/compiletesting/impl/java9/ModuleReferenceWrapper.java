package io.toolisticon.compiletesting.impl.java9;

import java.lang.reflect.Method;
import java.net.URI;

/**
 * Wrapper class for Java 9 ModuleReference.
 */
public class ModuleReferenceWrapper extends AbstractJava9BaseWrapper {

    final static Class MODULE_REFERENCE_CLASS = AbstractJava9BaseWrapper.getClassForName("java.lang.module.ModuleReference");


    final static Method METHOD_DESCRIPTOR = AbstractJava9BaseWrapper.getMethod(MODULE_REFERENCE_CLASS, "descriptor");
    final static Method METHOD_LOCATION = AbstractJava9BaseWrapper.getMethod(MODULE_REFERENCE_CLASS, "location");


    ModuleReferenceWrapper(Object instance) {

        super(MODULE_REFERENCE_CLASS, instance);

    }


    /**
     * Gets the location of the wrapped ModuleReference instance
     * @return The location.
     */
    public OptionalWrapper<URI> location() {

        return new OptionalWrapper<URI>(AbstractJava9BaseWrapper.invoke(METHOD_LOCATION, getWrappedInstance(), Object.class));

    }

    /**
     * Gets the wrapped ModuleDescriptor for the wrapped ModuleReference.
     * @return the wrapped ModuleDescriptor
     */
    public ModuleDescriptorWrapper descriptor() {

        return new ModuleDescriptorWrapper(AbstractJava9BaseWrapper.invoke(METHOD_DESCRIPTOR, getWrappedInstance(), Object.class));

    }

}
