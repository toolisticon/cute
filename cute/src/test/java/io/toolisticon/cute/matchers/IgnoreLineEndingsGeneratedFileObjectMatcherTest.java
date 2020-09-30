package io.toolisticon.cute.matchers;

import io.toolisticon.cute.FailingAssertionException;
import io.toolisticon.cute.JavaFileObjectUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import javax.tools.FileObject;
import java.io.IOException;

public class IgnoreLineEndingsGeneratedFileObjectMatcherTest {

    @Test
    public void check_matchingFiles() throws IOException {

        FileObject fo1 = JavaFileObjectUtils.readFromString("ABC\nDEF\r\nHIJ\nKLM\r\nNOP");
        FileObject fo2 = JavaFileObjectUtils.readFromString("ABC\r\nDEF\nHIJ\nKLM\r\nNOP");

        IgnoreLineEndingsGeneratedFileObjectMatcher unit = new IgnoreLineEndingsGeneratedFileObjectMatcher(fo1);
        MatcherAssert.assertThat("Must match", unit.check(fo2));


    }

    @Test(expected = FailingAssertionException.class)
    public void check_nonMatchingFiles() throws IOException {

        FileObject fo1 = JavaFileObjectUtils.readFromString("ABCDEF");
        FileObject fo2 = JavaFileObjectUtils.readFromString("ABCXXXEF");

        IgnoreLineEndingsGeneratedFileObjectMatcher unit = new IgnoreLineEndingsGeneratedFileObjectMatcher(fo1);
        unit.check(fo2);


    }

}