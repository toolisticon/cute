package io.toolisticon.compiletesting.matchers;

import io.toolisticon.compiletesting.Constants;
import io.toolisticon.compiletesting.FailingAssertionException;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;

import javax.tools.FileObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.PatternSyntaxException;

/**
 * Validates if a generated two FileObjects are equal by using binary comparision.
 */
public class BinaryGeneratedFileObjectMatcher<T extends FileObject> implements GeneratedFileObjectMatcher<T> {

    final FileObject expectedFileObject;

    /**
     * Hidden constructor.
     *
     * @param expectedFileObject the expected java file object
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    BinaryGeneratedFileObjectMatcher(T expectedFileObject) {

        this.expectedFileObject = expectedFileObject;

    }

    @Override
    public boolean check(T fileObject) throws IOException {

        // Must not be null
        if (fileObject == null) {
            return false;
        }

        if (!contentEquals(fileObject.openInputStream(), expectedFileObject.openInputStream())) {
            throw new FailingAssertionException(Constants.Messages.GFOM_FILEOBJECTS_ARENT_EQUAL_BY_BINARY_COMPARISION.produceMessage());
        }

        return true;
    }

    static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {

        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return (ch2 == -1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryGeneratedFileObjectMatcher<?> that = (BinaryGeneratedFileObjectMatcher<?>) o;

        return expectedFileObject != null ? expectedFileObject.equals(that.expectedFileObject) : that.expectedFileObject == null;
    }

    @Override
    public int hashCode() {
        return expectedFileObject != null ? expectedFileObject.hashCode() : 0;
    }
}