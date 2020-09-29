package io.toolisticon.cute.matchers;

import io.toolisticon.cute.FailingAssertionException;
import io.toolisticon.cute.JavaFileObjectUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import javax.tools.FileObject;
import java.io.IOException;

/**
 * Unit test for {@link BinaryGeneratedFileObjectMatcher}.
 */
public class BinaryGeneratedFileObjectMatcherTest {


    @Test
    public void check_matchingFiles() throws IOException {

        FileObject fo1 = JavaFileObjectUtils.readFromString("ABCDEF");
        FileObject fo2 = JavaFileObjectUtils.readFromString("ABCDEF");

        BinaryGeneratedFileObjectMatcher unit = new BinaryGeneratedFileObjectMatcher(fo1);
        MatcherAssert.assertThat("Must match", unit.check(fo2));


    }

    @Test(expected = FailingAssertionException.class)
    public void check_nonMatchingFiles() throws IOException {

        FileObject fo1 = JavaFileObjectUtils.readFromString("ABCDEF");
        FileObject fo2 = JavaFileObjectUtils.readFromString("ABCXXXEF");

        BinaryGeneratedFileObjectMatcher unit = new BinaryGeneratedFileObjectMatcher(fo1);
        unit.check(fo2);


    }


}