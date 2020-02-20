package io.toolisticon.compiletesting;

import io.toolisticon.compiletesting.impl.CommonUtilities;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to create JavaFileObjects and therefore also FileObjects.
 * These files can be used for comparision or as source files during compilation.
 */
public class JavaFileObjectUtils {


    /**
     * A file object used to represent source coming from a string.
     */
    public static class JavaSourceFromString extends SimpleJavaFileObject {
        /**
         * The source content of this "file".
         */
        private final String content;

        /**
         * Constructs a new JavaSourceFromString.
         *
         * @param name    the name of the compilation unit represented by this file object
         * @param content the source content for the compilation unit represented by this file object
         */
        private JavaSourceFromString(String name, String content) {
            super(URI.create("string://" + name.replace('.', '/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.content = content;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return new StringBufferInputStream(this.content);
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream());
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }


    }

    /**
     * Resource based java source.
     */
    public static class JavaSourceFromResource extends SimpleJavaFileObject {

        private final static String JAVA_COMPATIBILTY_FILE_ENDING_SUFFIX = ".ct";

        private final Class<?> relativeLocationRoot;
        private final String location;

        private JavaSourceFromResource(String location) {

            this(location, null);

        }

        private JavaSourceFromResource(String location, Class<?> relativeLocationRoot) {

            super(URI.create("resource://" + (location.endsWith(".java" + JAVA_COMPATIBILTY_FILE_ENDING_SUFFIX) ? location.substring(0, location.length() - JAVA_COMPATIBILTY_FILE_ENDING_SUFFIX.length()) : location)), Kind.SOURCE);
            this.relativeLocationRoot = relativeLocationRoot;
            this.location = location;

        }

        @Override
        public InputStream openInputStream() throws IOException {

            Class<?> relativeRoot = relativeLocationRoot != null ? relativeLocationRoot : JavaFileObjectUtils.class;
            InputStream inputStream = relativeRoot.getResourceAsStream(location);

            if (inputStream == null) {
                throw new IllegalStateException(Constants.Messages.ISE_CANNOT_OPEN_INPUTSTREAM_WITH_URI.produceMessage(uri.toString()));
            }

            return inputStream;
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream());
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return readFromInputStream(openInputStream());
        }
    }


    /**
     * Url based java source.
     */
    public static class JavaSourceFromUrl extends SimpleJavaFileObject {

        private final URL url;

        private JavaSourceFromUrl(URL url) throws URISyntaxException {
            super(url.toURI(), Kind.SOURCE);
            this.url = url;
        }


        @Override
        public InputStream openInputStream() throws IOException {
            return url.openStream();
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream());
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return readFromInputStream(openInputStream());
        }
    }


    /**
     * Read a java source file from resources.
     * <p>
     * Some IDEs like Eclipse don't like resource files ending with *.java.
     * In this case extend the file name by ".ct" suffix (f.e. "JavaClass.java.ct").
     * The suffix will be ignored for looking up files via the compile test file manager.
     * *
     *
     * @param location             the location
     * @param relativeLocationRoot relative location root class
     * @return The SimpleJavaFileObject for resource
     */
    public static SimpleJavaFileObject readFromResource(String location, Class<?> relativeLocationRoot) {

        if (location == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("location"));
        }

        return new JavaSourceFromResource(location, relativeLocationRoot);
    }

    /**
     * Read a java source file from resources.
     * Passed location will be handled as absolute path and will be used to both read resource and as location in compile test file manager.
     * <p>
     * Some IDEs like Eclipse don't like resource files ending with *.java.
     * In this case extend the file name by ".ct" suffix (f.e. "JavaClass.java.ct").
     * The suffix will be ignored for looking up files via the compile test file manager.
     *
     * @param location the location
     * @return The SimpleJavaFileObject for resource
     */
    public static SimpleJavaFileObject readFromResource(String location) {

        if (location == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("location"));
        }

        return new JavaSourceFromResource((!location.startsWith("/") ? "/" : "") + location, null);
    }

    /**
     * Reads multiple java source files from resources.
     * Passed locations will be handled as absolute path and will be used to both read resource and as location in compile test file manager.
     * <p>
     * Some IDEs like Eclipse don't like resource files ending with *.java.
     * In this case extend the file name by ".ct" suffix (f.e. "JavaClass.java.ct").
     * The suffix will be ignored for looking up files via the compile test file manager.
     *
     * @param locations the location
     * @return The SimpleJavaFileObject for resource
     */
    public static SimpleJavaFileObject[] readFromResources(String... locations) {

        List<SimpleJavaFileObject> resourceFiles = new ArrayList<SimpleJavaFileObject>();

        for (String location : locations) {
            resourceFiles.add(readFromResource(location));
        }

        return resourceFiles.toArray(new SimpleJavaFileObject[resourceFiles.size()]);
    }

    /**
     * Read a java source file from string.
     *
     * @param location the location
     * @param content  content of the file
     * @return The SimpleJavaFileObject for passed content string
     */
    public static SimpleJavaFileObject readFromString(String location, String content) {

        if (location == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("location"));
        }

        if (content == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("content"));
        }

        return new JavaSourceFromString(location, content);
    }

    /**
     * Read a java source file from resources.
     * This one works great if you don't rely on the location, f.e. in case of comparision.
     *
     * @param content content of the file
     * @return he SimpleJavaFileObject for passed content string
     */
    public static SimpleJavaFileObject readFromString(String content) {

        // create a random location
        String location = "string_" + CommonUtilities.getRandomString(6);

        return readFromString(location, content);
    }


    /**
     * Read a java source file from resurces.
     *
     * @param url the location
     * @return The SimpleJavaFileObject for passed URL
     * @throws URISyntaxException       if passed url cannot be converted into URI
     * @throws IllegalArgumentException if passed url is null
     */
    public static SimpleJavaFileObject readFromUrl(URL url) throws URISyntaxException {

        if (url == null) {
            throw new IllegalArgumentException(Constants.Messages.IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL.produceMessage("url"));
        }

        return new JavaSourceFromUrl(url);
    }

    /**
     * Reads a String from an InputStream.
     * CLoses the stream
     *
     * @param stream the inputStream to use
     * @return The String read from the inputStreams
     * @throws IOException if an error occurs
     */
    private static String readFromInputStream(InputStream stream) throws IOException {

        byte[] buffer = new byte[10000];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int line = 0;
        // read bytes from stream, and store them in buffer
        while ((line = stream.read(buffer)) != -1) {
            // Writes bytes from byte array (buffer) into output stream.
            os.write(buffer, 0, line);
        }
        stream.close();
        os.flush();
        os.close();

        return new String(os.toByteArray());
    }


}
