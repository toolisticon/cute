package io.toolisticon.compiletesting.matchers;

import io.toolisticon.compiletesting.Constants;
import io.toolisticon.compiletesting.FailingAssertionException;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;

import javax.tools.FileObject;
import java.io.IOException;

/**
 * Validates if the generated file contains all passed Strings.
 */
class ContainsStringsGeneratedFileOjectMatcher implements GeneratedFileObjectMatcher<FileObject> {


    String[] stringsToCheck;

    /**
     * Hidden constructor
     */
    ContainsStringsGeneratedFileOjectMatcher(String... stringsToCheck) {
        this.stringsToCheck = stringsToCheck;
    }

    @Override
    public boolean check(FileObject fileObject) throws IOException {

        String fileContent = fileObject.getCharContent(true).toString();

        for (String stringToCheck : stringsToCheck) {

            if (!fileContent.contains(stringToCheck)) {
                throw new FailingAssertionException(Constants.Messages.GFOM_COULDNT_FIND_SUBSTRING.produceMessage(stringToCheck, fileObject.getName().toString()));
            }

        }

        return true;
    }
}

