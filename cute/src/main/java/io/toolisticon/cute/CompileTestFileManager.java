package io.toolisticon.cute;

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

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import io.toolisticon.cute.CuteApi.ResourceFileBB;

/**
 * Forwarding file manager to be able to test for generated sources and resources
 */
class CompileTestFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {


	/**
	 * The file object cache that is used to store generated classes and resources.
	 * @param <T> The type of cached file objects, must be either FileObject or JavaFileObject
	 */
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
   
    /**
     * This internal class is used to provide resources from class path via a custom package.
     * Internally this works like an alias.
     * It hides any pre-existing resources with uri.
     */
    public static class ProvidedResourceFilesCache{
    	
    	
    	final Map<URI, FileObject> resourcesMap = new HashMap<>();
    	
    	ProvidedResourceFilesCache (List<ResourceFileBB> resourceFiles) {
    		
    		if (resourceFiles != null) {
    			resourceFiles.stream().filter(e -> e.getRelativeName() != null).forEach( e -> resourcesMap.put(uriForFileObject(StandardLocation.CLASS_PATH, e.targetPackageName(), e.getRelativeName()), FileObjectUtils.forPassedInResource(e)));
    		}
    		
    	}
    	
    	FileObject get(String packageName, String relativeName) {
    		URI uri = uriForFileObject(StandardLocation.CLASS_PATH, packageName,relativeName);
    		return this.resourcesMap.get(uri);
    	}
    	
    	
    	
    }


    private final FileObjectCache<InMemoryOutputJavaFileObject> generatedJavaFileObjectCache = new FileObjectCache<>();
    private final FileObjectCache<InMemoryOutputFileObject> generatedFileObjectCache = new FileObjectCache<>();
    private final ProvidedResourceFilesCache providedResourceFilesCache;


    public CompileTestFileManager(StandardJavaFileManager standardJavaFileManager, List<CuteApi.ResourceFileBB> resourceFiles) {
        super(standardJavaFileManager);
        
        // configure cache for resource files
        providedResourceFilesCache = new ProvidedResourceFilesCache(resourceFiles);

    }

    List<InMemoryOutputJavaFileObject> getGeneratedJavaFileObjects() {
        return new ArrayList<>(generatedJavaFileObjectCache.getEntries());
    }

    List<InMemoryOutputFileObject> getGeneratedFileObjects() {
        return new ArrayList<>(generatedFileObjectCache.getEntries());
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return a.toUri().equals(b.toUri());
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {

        InMemoryOutputJavaFileObject result = new InMemoryOutputJavaFileObject(location, className, kind);
        generatedJavaFileObjectCache.addFileObject(result.toUri(), result);
        return result;

    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) {
        InMemoryOutputFileObject result = new InMemoryOutputFileObject(location, packageName, relativeName);
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
        } else if (StandardLocation.CLASS_PATH.equals(location)) {
        	
        	// Try to get it from provided resource file cache
        	FileObject providedResource = this.providedResourceFilesCache.get(packageName, relativeName);
        	
        	if (providedResource != null) {
        		return providedResource;
        	}
        	
        }
        
        // use standard manager if file haven't been found so far
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
    	
    	if (location.isOutputLocation()) {
    		return this.generatedJavaFileObjectCache.contains(uriForJavaFileObject(location, className, kind));
    	} 
    	
    	try {
			return super.getJavaFileForInput(location, className, kind) != null;
		} catch (IOException e) {
			throw new IllegalStateException("Something went wrong while accessing the default FileManager", e);
		}
        

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
    	
    	// return true if file could be found in resource cache, otherwise check regular file manager
    	if (StandardLocation.CLASS_PATH.equals(location) && this.providedResourceFilesCache.get(packageName, relativeName) != null) {
    		return true;
    	} 
    	
    	// otherwise check the generated file object cache
    	if (location.isOutputLocation()) {
    		return this.generatedFileObjectCache.contains(uriForFileObject(location, packageName, relativeName));
    	}
    	
    	// use standard file manager for input files
    	try {
			return super.getFileForInput(location, packageName, relativeName) != null;
		} catch (IOException e) {
			throw new IllegalStateException("Something went wrong while accessing the default FileManager", e);
		}
    	
    }


    interface OutputStreamCallback {

        void setContent(byte[] content);

    }


    static URI uriForFileObject(Location location, String packageName, String relativeName) {
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


    public static abstract class AbstractInMemoryOutputFileObject extends SimpleJavaFileObject implements OutputStreamCallback {

        private byte[] content = new byte[0];

        public AbstractInMemoryOutputFileObject(URI uri, Kind kind) {
            super(uri, kind);
        }

        public byte[] getContent() {
            return content;
        }

        @Override
        public void setContent(byte[] content) {
            this.content = content != null ? content : new byte[0];
        }

        @Override
        public InputStream openInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public OutputStream openOutputStream() {
            return new InMemoryOutputStream(this);
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) {
            return new InputStreamReader(openInputStream());
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return new String(content);
        }

        @Override
        public Writer openWriter() {
            return new OutputStreamWriter(openOutputStream());
        }
    }

    public static class InMemoryOutputJavaFileObject extends AbstractInMemoryOutputFileObject {

        private final Location location;
        private final String className;
        private final JavaFileObject.Kind kind;

        InMemoryOutputJavaFileObject(Location location, String className, JavaFileObject.Kind kind) {
            super(uriForJavaFileObject(location, className, kind), kind);
            this.location = location;
            this.className = className;
            this.kind = kind;
        }

        public Location getLocation() {
            return location;
        }

        public String getClassName() {
            return className;
        }

        @Override
        public Kind getKind() {
            return kind;
        }
    }

    public static class InMemoryOutputFileObject extends AbstractInMemoryOutputFileObject {

        private final Location location;
        private final String packageName;
        private final String relativeName;

        InMemoryOutputFileObject(Location location, String packageName, String relativeName) {
            super(uriForFileObject(location, packageName, relativeName), JavaFileObject.Kind.OTHER);
            this.location = location;
            this.packageName = packageName;
            this.relativeName = relativeName;
        }


        public Location getLocation() {
            return location;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getRelativeName() {
            return relativeName;
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
