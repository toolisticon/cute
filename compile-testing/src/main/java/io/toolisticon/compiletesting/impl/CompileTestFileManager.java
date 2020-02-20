package io.toolisticon.compiletesting.impl;

import io.toolisticon.compiletesting.Constants;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Forwarding file manager to be able to test for generated sources and resources
 */
class CompileTestFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {


    public static class FileObjectCache<T extends FileObject> {

        final Map<URI, T> fileObjectCache = new HashMap<>();


        public boolean contains(URI uri) {
            return fileObjectCache.containsKey(uri);
        }

        public T getFileObject(URI uri) {
            return fileObjectCache.get(uri);
        }

        public void addFileObject(URI uri, T fileObject) {
            fileObjectCache.put(uri, fileObject);
        }

        public Collection<T> getEntries() {
            return fileObjectCache.values();
        }

        public boolean isEmpty() {
            return fileObjectCache.isEmpty();
        }


    }


    private final FileObjectCache<JavaFileObject> generatedJavaFileObjectCache = new FileObjectCache<>();
    private final FileObjectCache<FileObject> generatedFileObjectCache = new FileObjectCache<>();


    public CompileTestFileManager(StandardJavaFileManager standardJavaFileManager) {
        super(standardJavaFileManager);

    }

    List<JavaFileObject> getGeneratedJavaFileObjects() {
        return new ArrayList<JavaFileObject>(generatedJavaFileObjectCache.getEntries());
    }

    List<FileObject> getGeneratedFileObjects() {
        return new ArrayList<FileObject>(generatedFileObjectCache.getEntries());
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return a.toUri().equals(b.toUri());
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {


        JavaFileObject result = new InMemoryOutputJavaFileObject(uriForJavaFileObject(location, className, kind), kind);
        generatedJavaFileObjectCache.addFileObject(result.toUri(), result);
        return result;

    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        JavaFileObject result = new InMemoryOutputJavaFileObject(uriForFileObject(location, packageName, relativeName), JavaFileObject.Kind.OTHER);
        generatedFileObjectCache.addFileObject(result.toUri(), result);
        return result;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {

        if (location.isOutputLocation()) {

            URI uri = uriForJavaFileObject(location, className, kind);

            if (generatedJavaFileObjectCache.contains(uri)) {
                return generatedJavaFileObjectCache.getFileObject(uri);
            } else {
                throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_FIND_JAVAFILEOBJECT.produceMessage(uri.toString()));
            }
        }
        return super.getJavaFileForInput(location, className, kind);

    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {

        if (location.isOutputLocation()) {

            URI uri = uriForFileObject(location, packageName, relativeName);

            if (generatedFileObjectCache.contains(uri)) {
                return generatedFileObjectCache.getFileObject(uri);
            } else {
                throw new IllegalArgumentException(Constants.Messages.IAE_CANNOT_FIND_FILEOBJECT.produceMessage(uri.toString()));
            }
        }
        return super.getFileForInput(location, packageName, relativeName);
    }


    /**
     * Checks if JavaFileObject for passed parameters exists.
     *
     * @param location  the location to search in
     * @param className the classname
     * @param kind      the kind of java file object
     * @return true if JavaFileObject exists, otherwise false
     */
    public boolean existsExpectedJavaFileObject(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) {

        return this.generatedJavaFileObjectCache.contains(uriForJavaFileObject(location, className, kind));

    }

    /**
     * Checks if JavaFileObject for passed parameters exists.
     *
     * @param location     the location to search in
     * @param packageName  The package name
     * @param relativeName The name relative to the passed packageName to check for
     * @return true if FileObject exists, otherwise false
     */
    public boolean existsExpectedFileObject(JavaFileManager.Location location, String packageName, String relativeName) {

        return this.generatedFileObjectCache.contains(uriForFileObject(location, packageName, relativeName));


    }


    interface OutputStreamCallback {

        void setContent(byte[] content);

    }


    private static URI uriForFileObject(Location location, String packageName, String relativeName) {
        StringBuilder uri = new StringBuilder("mem://").append(location.getName()).append('/');
        if (!packageName.isEmpty()) {
            uri.append(packageName.replace('.', '/')).append('/');
        }
        uri.append(relativeName);
        return URI.create(uri.toString());
    }

    private static URI uriForJavaFileObject(Location location, String className, JavaFileObject.Kind kind) {
        return URI.create(
                "mem://" + location.getName() + '/' + className.replace('.', '/') + kind.extension);
    }


    public static class InMemoryOutputJavaFileObject extends SimpleJavaFileObject implements OutputStreamCallback {

        private byte[] content = new byte[0];

        public InMemoryOutputJavaFileObject(URI uri, Kind kind) {
            super(uri, kind);
        }

        @Override
        public void setContent(byte[] content) {
            this.content = content != null ? content : new byte[0];
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return new InMemoryOutputStream(this);
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream());
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return new String(content);
        }

        @Override
        public Writer openWriter() throws IOException {
            return new OutputStreamWriter(openOutputStream());
        }
    }

    /**
     * Does call a callback function on close to set content.
     * Have to check if it is sufficient to pass content on close.
     */
    public static class InMemoryOutputStream extends ByteArrayOutputStream {

        private final OutputStreamCallback outputStreamCallback;

        public InMemoryOutputStream(OutputStreamCallback outputStreamCallback) {
            this.outputStreamCallback = outputStreamCallback;
        }

        @Override
        public void close() throws IOException {

            super.close();
            outputStreamCallback.setContent(this.toByteArray());

        }

        @Override
        public void write(byte[] b) throws IOException {
            super.write(b);
            outputStreamCallback.setContent(this.toByteArray());
        }
    }
}
