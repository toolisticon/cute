package io.toolisticon.cute;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.tools.FileObject;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Unit test for {@link FileObjectUtils}.
 */
public class FileObjectUtilsTest {

    @Test
    public void test_fromString() throws IOException {

        FileObject fileObject = FileObjectUtils.fromString("TEST");
        MatcherAssert.assertThat(fileObject.getCharContent(false), Matchers.is("TEST"));
        MatcherAssert.assertThat(new BufferedReader(fileObject.openReader(true)).readLine(), Matchers.is("TEST"));



    }

    @Test
    public void test_fromResource() throws IOException {

        FileObject fileObject = FileObjectUtils.fromResource("/compiletests/tata.txt");
        MatcherAssert.assertThat(fileObject.getCharContent(false), Matchers.is("TATA!"));
        MatcherAssert.assertThat(new BufferedReader(fileObject.openReader(true)).readLine(), Matchers.is("TATA!"));



    }

}
