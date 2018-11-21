package io.toolisticon.compiletesting;

import javax.tools.FileObject;
import java.io.IOException;

/**
 * Interface to allow custom checks in fluent api.
 *
 * @param <T>
 */
public interface GeneratedFileObjectMatcher<T extends FileObject> {

    /**
     * Method to check
     *
     * @param fileObject
     */
    boolean check(T fileObject) throws IOException;

}
