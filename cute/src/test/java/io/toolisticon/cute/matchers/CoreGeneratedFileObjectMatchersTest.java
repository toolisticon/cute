package io.toolisticon.cute.matchers;

import io.toolisticon.cute.GeneratedFileObjectMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.tools.FileObject;
import java.util.Arrays;

public class CoreGeneratedFileObjectMatchersTest {

    @Test
    public void createRegexMatcher() {

        GeneratedFileObjectMatcher unit = CoreGeneratedFileObjectMatchers.createRegexMatcher("ABC");
        MatcherAssert.assertThat(unit, Matchers.isA(RegexGeneratedFileObjectMatcher.class));
        MatcherAssert.assertThat(((RegexGeneratedFileObjectMatcher) unit).patternString, Matchers.is("ABC"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void createRegexMatcher_nullValuedPattern() {

        CoreGeneratedFileObjectMatchers.createRegexMatcher(null);

    }

    @Test
    public void createContainsSubstringsMatcher() {

        GeneratedFileObjectMatcher unit = CoreGeneratedFileObjectMatchers.createContainsSubstringsMatcher("ABC", "DEF");
        MatcherAssert.assertThat(unit, Matchers.isA(ContainsStringsGeneratedFileObjectMatcher.class));
        MatcherAssert.assertThat(Arrays.asList(((ContainsStringsGeneratedFileObjectMatcher) unit).stringsToCheck), Matchers.contains("ABC", "DEF"));

    }

    @Test
    public void createIsWellFormedXmlMatcher() {

        GeneratedFileObjectMatcher unit = CoreGeneratedFileObjectMatchers.createIsWellFormedXmlMatcher();
        MatcherAssert.assertThat(unit, Matchers.isA(WellFormedXmlGeneratedFileObjectMatcher.class));

    }

    @Test
    public void createIgnoreLineEndingsMatcher() {

        FileObject fo = Mockito.mock(FileObject.class);
        GeneratedFileObjectMatcher unit = CoreGeneratedFileObjectMatchers.createIgnoreLineEndingsMatcher(fo);
        MatcherAssert.assertThat(unit, Matchers.isA(IgnoreLineEndingsGeneratedFileObjectMatcher.class));
        MatcherAssert.assertThat(((IgnoreLineEndingsGeneratedFileObjectMatcher) unit).expectedFileObject, Matchers.is(fo));

    }

    @Test(expected = IllegalArgumentException.class)
    public void createIgnoreLineEndingsMatcher_nullValued() {

        CoreGeneratedFileObjectMatchers.createIgnoreLineEndingsMatcher(null);

    }

    @Test
    public void createBinaryMatcher() {

        FileObject fo = Mockito.mock(FileObject.class);
        GeneratedFileObjectMatcher unit = CoreGeneratedFileObjectMatchers.createBinaryMatcher(fo);
        MatcherAssert.assertThat(unit, Matchers.isA(BinaryGeneratedFileObjectMatcher.class));
        MatcherAssert.assertThat(((BinaryGeneratedFileObjectMatcher) unit).expectedFileObject, Matchers.is(fo));

    }

    @Test(expected = IllegalArgumentException.class)
    public void createBinaryMatcher_nullValued() {

        CoreGeneratedFileObjectMatchers.createBinaryMatcher(null);

    }
}