package io.toolisticon.compiletesting.impl.java9;

public final class Java9SupportCheck {

    final static boolean UNSUPPORTED_JAVA_VERSION;

    static {

        // get ModuleFinderClass
        Class moduleFinderClass = null;
        try {
            moduleFinderClass = Class.forName("java.lang.module.ModuleFinder");
        } catch (Exception e) {
            // ignore
        }

        UNSUPPORTED_JAVA_VERSION = moduleFinderClass == null;

    }

    /**
     * Hiddden constructor.
     */
    private Java9SupportCheck() {

    }

}