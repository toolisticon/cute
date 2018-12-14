package io.toolisticon.compiletesting;

import javax.tools.FileObject;
import java.io.IOException;

/**
 * Interface to allow custom checks in fluent api.
 *
 * @param <T> The type of the file object: Either FileObject or JavaFileObject.
 */
public interface GeneratedFileObjectMatcher<T extends FileObject> {

    /**
     * Method to check
     *
     * @param fileObject the file object to check
     */
    boolean check(T fileObject) throws IOException;

}
