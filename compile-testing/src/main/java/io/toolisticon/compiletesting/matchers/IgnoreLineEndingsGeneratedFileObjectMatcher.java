package io.toolisticon.compiletesting.matchers;

import io.toolisticon.compiletesting.Constants;
import io.toolisticon.compiletesting.FailingAssertionException;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;

import javax.tools.FileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.PatternSyntaxException;

/**
 * Validates if file objects are matching.
 * Does validation line by line and ignores line endings.
 * (this is good for text files since windows and linux operation systems are using different line endings)
 */
public class IgnoreLineEndingsGeneratedFileObjectMatcher<T extends FileObject> implements GeneratedFileObjectMatcher<T> {

    final T expectedFileObject;

    /**
     * Hidden constructor.
     *
     * @param expectedFileObject the expected java file object
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    IgnoreLineEndingsGeneratedFileObjectMatcher(T expectedFileObject) {

        this.expectedFileObject = expectedFileObject;

    }

    @Override
    public boolean check(T fileObject) throws IOException {

        if (!contentEquals(fileObject.openInputStream(), expectedFileObject.openInputStream())) {
            throw new FailingAssertionException(Constants.Messages.GFOM_FILEOBJECTS_ARENT_EQUAL_BY_TEXTUAL_COMPARISION_WITH_IGNORE_LINEENDINGS.produceMessage());
        }

        return true;
    }


    static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {

        BufferedReader br1 = new BufferedReader(new InputStreamReader(input1));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(input2));

        String br1line;
        String br2line;
        do {
            br1line = br1.readLine();
            br2line = br2.readLine();


            if (br1line != null && br2line != null && br1line != br2line) {

                if (!br1line.equals(br2line)) {
                    return false;
                }

            } else if (br1line != null || br2line != null) {
                return false;
            }

        } while (br1line != null && br2line != null);

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IgnoreLineEndingsGeneratedFileObjectMatcher<?> that = (IgnoreLineEndingsGeneratedFileObjectMatcher<?>) o;

        return expectedFileObject != null ? expectedFileObject.equals(that.expectedFileObject) : that.expectedFileObject == null;
    }

    @Override
    public int hashCode() {
        return expectedFileObject != null ? expectedFileObject.hashCode() : 0;
    }
}

