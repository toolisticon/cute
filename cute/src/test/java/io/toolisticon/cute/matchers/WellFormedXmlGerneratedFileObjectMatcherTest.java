package io.toolisticon.cute.matchers;

import io.toolisticon.cute.FailingAssertionException;
import io.toolisticon.cute.JavaFileObjectUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * Unit test for {@link WellFormedXmlGeneratedFileObjectMatcher}.
 */
public class WellFormedXmlGerneratedFileObjectMatcherTest {

    @Test
    public void testForWellFormedXml_validXml() throws IOException {

        CoreGeneratedFileObjectMatchers.createIsWellFormedXmlMatcher().check(JavaFileObjectUtils.readFromString("<html>\n<head>\n</head>\n<body>\n</body>\n</html>"));

    }

    @Test(expected = FailingAssertionException.class)
    public void testForWellFormedXml_invalidXml() throws IOException {

        CoreGeneratedFileObjectMatchers.createIsWellFormedXmlMatcher().check(JavaFileObjectUtils.readFromString("<html>\n<head>\n</head>\nbody>\n</body>\n</html>"));

    }


}
