package io.toolisticon.cute;

/**
 * Utility class to check if java 9 api is unsupported.
 */
final class Java9SupportCheck {

    /**
     * Constant to check if JAVA 9 API is unsupported.
     */
    final static boolean UNSUPPORTED_JAVA_VERSION;

    static {

        // get ModuleFinderClass
        Class<?> moduleFinderClass = null;
        try {
            moduleFinderClass = Class.forName("java.lang.module.ModuleFinder");
        } catch (Exception e) {
            // ignore
        }

        UNSUPPORTED_JAVA_VERSION = moduleFinderClass == null;

    }

    /**
     * Hidden constructor.
     */
    private Java9SupportCheck() {

    }

}