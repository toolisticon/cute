package io.toolisticon.compiletesting.impl.java9;

import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public final class StandardJavaFileManagerBridge {

    private static final Method METHOD_SET_LOCATION = AbstractJava9BaseWrapper.getMethod(StandardJavaFileManager.class, "setLocation", JavaFileManager.Location.class, Iterable.class);
    private static final Method METHOD_GET_LOCATION_FOR_MODULE = AbstractJava9BaseWrapper.getMethod(JavaFileManager.class, "getLocationForModule", JavaFileManager.Location.class, String.class);
    private static final Method METHOD_SET_LOCATION_FOR_MODULE = AbstractJava9BaseWrapper.getMethod(StandardJavaFileManager.class, "setLocationForModule", JavaFileManager.Location.class, String.class, Collection.class);
    private static final Method METHOD_LIST_LOCATIONS_FOR_MODULES = AbstractJava9BaseWrapper.getMethod(JavaFileManager.class, "listLocationsForModules", JavaFileManager.Location.class);
    private static final Method METHOD_INFER_MODULE_NAME = AbstractJava9BaseWrapper.getMethod(JavaFileManager.class, "inferModuleName", JavaFileManager.Location.class);


    /**
     * Hidden constructor.
     */
    private StandardJavaFileManagerBridge() {

    }


    public static void setLocation(JavaFileManager javaFileManager, JavaFileManager.Location location, Iterable<File> files) {

        AbstractJava9BaseWrapper.invoke(METHOD_SET_LOCATION, javaFileManager, Object.class, location, files);

    }

    public static JavaFileManager.Location getGetLocationForModule(JavaFileManager javaFileManager, JavaFileManager.Location location, String module) {

        return AbstractJava9BaseWrapper.invoke(METHOD_GET_LOCATION_FOR_MODULE, javaFileManager, JavaFileManager.Location.class, location, module);

    }

    public static void setLocationForModule(JavaFileManager javaFileManager, JavaFileManager.Location location, String module, PathWrapper paths) {

        AbstractJava9BaseWrapper.invoke(METHOD_SET_LOCATION_FOR_MODULE, javaFileManager, JavaFileManager.Location.class, location, module, Arrays.asList(PathWrapper.getWrappedPaths(paths)));

    }

    public static Iterable<Set<JavaFileManager.Location>> listLocationsForModules​(JavaFileManager javaFileManager, JavaFileManager.Location location)
            throws IOException {

        return (Iterable<Set<JavaFileManager.Location>>) AbstractJava9BaseWrapper.invoke(METHOD_LIST_LOCATIONS_FOR_MODULES, javaFileManager, Iterable.class, location);


    }

    public static String inferModuleName(JavaFileManager javaFileManager, JavaFileManager.Location location)
            throws IOException {

        return AbstractJava9BaseWrapper.invoke(METHOD_INFER_MODULE_NAME, javaFileManager, String.class, location);


    }

}
