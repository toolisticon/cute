package io.toolisticon.cute.matchers;

import io.toolisticon.cute.CompileTestBuilder;
import io.toolisticon.cute.FailingAssertionException;
import io.toolisticon.cute.JavaFileObjectUtils;
import org.junit.Test;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;

/**
 * Unit test for {@link ContainsStringsGeneratedFileOjectMatcher}.
 */
public class ContainsStringsGeneratedFileOjectMatcherTest {

    @Test
    public void testForContainingStrings_valid() throws IOException {

        CoreGeneratedFileObjectMatchers.createContainsSubstringsMatcher("head", "body").check(JavaFileObjectUtils.readFromString("<html>\n<head>\n</head>\n<body>\n</body>\n</html>"));

    }

    @Test(expected = FailingAssertionException.class)
    public void testForContainingStrings_invalid() throws IOException {

        CoreGeneratedFileObjectMatchers.createContainsSubstringsMatcher("head", "whoopdidoo").check(JavaFileObjectUtils.readFromString("<html>\n<head>\n</head>\n<body>\n</body>\n</html>"));

    }

    @Test
    public void checkUsageOnFileObject() {

        CompileTestBuilder.compilationTest().expectThatJavaFileObjectExists(StandardLocation.SOURCE_OUTPUT,"io.toolisticon.cute.TestClass", JavaFileObject.Kind.SOURCE,CoreGeneratedFileObjectMatchers.createContainsSubstringsMatcher("abc"));

    }


}