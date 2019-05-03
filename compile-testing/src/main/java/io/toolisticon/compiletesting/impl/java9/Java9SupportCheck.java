package io.toolisticon.compiletesting.impl.java9;

/**
 * Utility class to check if java 9 api is unsupported.
 */
public final class Java9SupportCheck {

    /**
     * Constant to check if JAVA 9 API is unsupported.
     */
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