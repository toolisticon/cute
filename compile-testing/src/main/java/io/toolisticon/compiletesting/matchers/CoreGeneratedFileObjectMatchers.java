package io.toolisticon.compiletesting.matchers;

import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;

import javax.tools.FileObject;
import java.util.regex.PatternSyntaxException;

/**
 * Convenience method to create core matchers for checking
 */
public final class CoreGeneratedFileObjectMatchers {

    /**
     * Hidden constructor.
     */
    private CoreGeneratedFileObjectMatchers() {

    }

    /**
     * Static function to create a {@link RegexGeneratedFileObjectMatcher} instance.
     *
     * @param pattern a valid pattern string
     * @return the instance
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    public static GeneratedFileObjectMatcher<FileObject> createRegexMatcher(String pattern) {
        return new RegexGeneratedFileObjectMatcher(pattern);
    }

    /**
     * Static function to create a {@link ContainsStringsGeneratedFileOjectMatcher} instance.
     *
     * @return the instance
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    public static GeneratedFileObjectMatcher<FileObject> createContainsSubstringsMatcher(String... stringsToSearch) {
        return new ContainsStringsGeneratedFileOjectMatcher(stringsToSearch);
    }


    /**
     * Static function to create a {@link WellFormedXmlGeneratedFileObjectMatcher} instance.
     *
     * @return the instance
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    public static GeneratedFileObjectMatcher<FileObject> createIsWellFormedXmlMatcher() {
        return new WellFormedXmlGeneratedFileObjectMatcher();
    }


}
