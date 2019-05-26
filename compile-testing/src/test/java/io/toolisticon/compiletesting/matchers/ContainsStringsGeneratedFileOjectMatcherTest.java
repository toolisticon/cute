package io.toolisticon.compiletesting.matchers;

import io.toolisticon.compiletesting.FailingAssertionException;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * Unit test for {@link ContainsStringsGeneratedFileOjectMatcher}.
 */
public class ContainsStringsGeneratedFileOjectMatcherTest {

    @Test
    public void testForConatainingStrings_valid() throws IOException {

        CoreGeneratedFileObjectMatchers.createContainsSubstringsMatcher("head", "body").check(JavaFileObjectUtils.readFromString("<html>\n<head>\n</head>\n<body>\n</body>\n</html>"));

    }

    @Test(expected = FailingAssertionException.class)
    public void testForConatainingStrings_invalid() throws IOException {

        CoreGeneratedFileObjectMatchers.createContainsSubstringsMatcher("head", "whoopdidoo").check(JavaFileObjectUtils.readFromString("<html>\n<head>\n</head>\n<body>\n</body>\n</html>"));

    }


}