package io.toolisticon.compiletesting.matchers;

import io.toolisticon.compiletesting.FailingAssertionException;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
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
