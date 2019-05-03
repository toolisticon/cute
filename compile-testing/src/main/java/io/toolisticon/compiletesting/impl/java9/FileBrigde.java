package io.toolisticon.compiletesting.impl.java9;

import java.io.File;
import java.lang.reflect.Method;

public class FileBrigde {

    private static final Method TO_PATH_METHOD = AbstractJava9BaseWrapper.getMethod(File.class, "toPath");


    public static PathWrapper toPath(File file) {

        Object result = AbstractJava9BaseWrapper.invoke(TO_PATH_METHOD, file, Object.class);

        return new PathWrapper(result);

    }


}
