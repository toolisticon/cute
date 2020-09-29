package io.toolisticon.cute.matchers;

import io.toolisticon.cute.FailingAssertionException;
import io.toolisticon.cute.JavaFileObjectUtils;
import org.junit.Test;

import java.io.IOException;


/**
 * Unit test for {@link RegexGeneratedFileObjectMatcher}.
 */
public class RegexGeneratedFileObjectMatcherTest {

    @Test
    public void testForRegexMatcher_valid() throws IOException {

        CoreGeneratedFileObjectMatchers.createRegexMatcher(".*head.*").check(JavaFileObjectUtils.readFromString("war\nheadwarbody"));

    }

    @Test(expected = FailingAssertionException.class)
    public void testForRegexMatcher_invalid() throws IOException {

        CoreGeneratedFileObjectMatchers.createRegexMatcher(".*abc.*").check(JavaFileObjectUtils.readFromString("<html>\n<head>\n</head>\nbody>\n</body>\n</html>"));

    }


}
