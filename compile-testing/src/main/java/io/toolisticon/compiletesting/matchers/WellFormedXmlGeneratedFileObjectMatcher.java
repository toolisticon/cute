package io.toolisticon.compiletesting.matchers;


import io.toolisticon.compiletesting.Constants;
import io.toolisticon.compiletesting.FailingAssertionException;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;

import javax.tools.FileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;

/**
 * Matcher to check if file contains well formed xml.
 */
class WellFormedXmlGeneratedFileObjectMatcher implements GeneratedFileObjectMatcher<FileObject> {

    /**
     * Hidden constructor
     */
    WellFormedXmlGeneratedFileObjectMatcher() {

    }

    @Override
    public boolean check(FileObject fileObject) throws IOException {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // speed up processing by preventing dowwnloading of dtds
            factory.setValidating(false);
            factory.setNamespaceAware(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(fileObject.openInputStream());

            return true;
        } catch (Exception e) {
            throw new FailingAssertionException(Constants.Messages.GFOM_FILEOBJECT_IS_NOT_WELL_FORMED.produceMessage(fileObject.getName().toString()));
        }


    }


}
