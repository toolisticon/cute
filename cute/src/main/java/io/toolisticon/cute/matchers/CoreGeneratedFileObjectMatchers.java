package io.toolisticon.cute.matchers;

import io.toolisticon.cute.Constants;
import io.toolisticon.cute.GeneratedFileObjectMatcher;

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
     * @throws PatternSyntaxException   If the expression's syntax is invalid
     * @throws IllegalArgumentException if passed pattern is null
     */
    public static GeneratedFileObjectMatcher createRegexMatcher(String pattern) {

        if (pattern == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("pattern"));
        }

        return new RegexGeneratedFileObjectMatcher(pattern);
    }

    /**
     * Static function to create a {@link ContainsStringsGeneratedFileObjectMatcher} instance.
     *
     * @return the instance
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    public static  GeneratedFileObjectMatcher createContainsSubstringsMatcher(String... stringsToSearch) {
        return new ContainsStringsGeneratedFileObjectMatcher(stringsToSearch);
    }


    /**
     * Static function to create a {@link WellFormedXmlGeneratedFileObjectMatcher} instance.
     *
     * @return the instance
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    public static GeneratedFileObjectMatcher createIsWellFormedXmlMatcher() {
        return new WellFormedXmlGeneratedFileObjectMatcher();
    }

    public static <T extends FileObject> GeneratedFileObjectMatcher createIgnoreLineEndingsMatcher(T expectedFileObject) {

        if (expectedFileObject == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("expected fileobject"));
        }

        return new IgnoreLineEndingsGeneratedFileObjectMatcher(expectedFileObject);
    }

    public static <T extends FileObject> GeneratedFileObjectMatcher createBinaryMatcher(T expectedFileObject) {

        if (expectedFileObject == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("expected fileobject"));
        }

        return new BinaryGeneratedFileObjectMatcher(expectedFileObject);
    }


}
