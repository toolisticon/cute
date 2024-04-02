package io.toolisticon.cute.matchers;

import io.toolisticon.cute.Constants;
import io.toolisticon.cute.FailingAssertionException;
import io.toolisticon.cute.GeneratedFileObjectMatcher;

import javax.tools.FileObject;
import java.io.IOException;

/**
 * Validates if the generated file contains all passed Strings.
 */
class ContainsStringsGeneratedFileObjectMatcher implements GeneratedFileObjectMatcher {


    String[] stringsToCheck;

    /**
     * Hidden constructor
     */
    ContainsStringsGeneratedFileObjectMatcher(String... stringsToCheck) {
        this.stringsToCheck = stringsToCheck;
    }

    @Override
    public boolean check(FileObject fileObject) throws IOException {

        String fileContent = fileObject.getCharContent(true).toString();

        for (String stringToCheck : stringsToCheck) {

            if (!fileContent.contains(stringToCheck)) {
                throw new FailingAssertionException(Constants.Messages.GFOM_COULDNT_FIND_SUBSTRING.produceMessage(stringToCheck, fileObject.getName()));
            }

        }

        return true;
    }
}

