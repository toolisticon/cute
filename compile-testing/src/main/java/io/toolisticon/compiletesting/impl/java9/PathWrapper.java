package io.toolisticon.compiletesting.impl.java9;

import java.lang.reflect.Array;

/**
 * Wrapper class for Java 7 Path.
 */
public class PathWrapper {

    public static final String JAVA_NIO_FILE_PATH_FQN = "java.nio.file.Path";


    private final Object wrappedPath;

    public PathWrapper(Object path) {

        if (path == null || JAVA_NIO_FILE_PATH_FQN.equals(path.getClass().getCanonicalName())) {
            throw new IllegalArgumentException("Passed instance must not be null and of runtime type ");
        }

        wrappedPath = path;

    }

    public Object getWrappedPath() {
        return wrappedPath;
    }


    public static Object[] getWrappedPaths(PathWrapper... paths) {

        // shit in shit out
        if (paths == null) {
            return null;
        }

        Object[] resultArray = (Object[]) Array.newInstance(getPathClass(), paths.length);

        int i = 0;
        for (PathWrapper path : paths) {

            resultArray[i] = path.getWrappedPath();

            i++;
        }

        return resultArray;

    }

    public static Class getPathClass() {
        try {
            return Class.forName(JAVA_NIO_FILE_PATH_FQN);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
