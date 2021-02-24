package io.toolisticon.cute;

import javax.tools.FileObject;
import java.io.IOException;

/**
 * Interface to allow custom checks in fluent api.
 *
 */
public interface GeneratedFileObjectMatcher {

    /**
     * Method to check
     *
     * @param fileObject the file object to check
     * @return true if check is done successfully, otherwise false
     * @throws IOException               might be thrown during access of passed fileObject
     * @throws FailingAssertionException can be thrown to provide a detailed assertion message in case of a failing assertion.
     */
    boolean check(FileObject fileObject) throws IOException;

}
