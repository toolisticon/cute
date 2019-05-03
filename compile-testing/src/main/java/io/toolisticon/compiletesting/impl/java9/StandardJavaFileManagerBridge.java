package io.toolisticon.compiletesting.impl.java9;

import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Bridge for StandardJavaFileManager.
 */
public final class StandardJavaFileManagerBridge {

    private static final Method METHOD_SET_LOCATION = AbstractJava9BaseWrapper.getMethod(StandardJavaFileManager.class, "setLocation", JavaFileManager.Location.class, Iterable.class);

    /**
     * Hidden constructor.
     */
    private StandardJavaFileManagerBridge() {

    }


    public static void setLocation(JavaFileManager javaFileManager, JavaFileManager.Location location, Iterable<File> files) {

        AbstractJava9BaseWrapper.invoke(METHOD_SET_LOCATION, javaFileManager, Object.class, location, files);

    }

}
