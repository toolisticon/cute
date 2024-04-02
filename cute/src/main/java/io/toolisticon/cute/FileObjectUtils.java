package io.toolisticon.cute;


import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

/**
 * Utility class to provide FileObjects that can be used for comparison in checks.
 */
public abstract class FileObjectUtils {

    static abstract class AbstractFileObject implements FileObject {
        @Override
        public URI toUri() {
            return URI.create("string://" + StandardLocation.SOURCE_OUTPUT + "/Dummy");
        }

        @Override
        public String getName() {
            return "Dummy";
        }

        @Override
        public OutputStream openOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream());
        }


        @Override
        public Writer openWriter() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastModified() {
            return 0;
        }

        @Override
        public boolean delete() {
            return false;
        }

    }


    static class FileObjectFromString extends AbstractFileObject {


        private final String content;

        public FileObjectFromString(String content) {

            this.content = content;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return new ByteArrayInputStream(this.content.getBytes());
        }


        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return content;
        }

    }

    static class FileObjectFromResource extends AbstractFileObject {

        private final String resource;

        public FileObjectFromResource(String resource) {

            this.resource = resource;
        }


        @Override
        public InputStream openInputStream() throws IOException {
            return FileObjectUtils.class.getResourceAsStream(resource);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {

            ByteArrayOutputStream buffer;
            try (InputStream inputStream = openInputStream()) {

                buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                return buffer.toString();

            }

        }

    }

    public static FileObject fromString(String content) {
        return new FileObjectFromString(content);
    }

    public static FileObject fromResource(String resource) {
        return new FileObjectFromResource(resource);
    }

}
