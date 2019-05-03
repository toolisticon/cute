package io.toolisticon.compiletesting.impl.java9;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Wrapper class for Java 9 ModuleFinder.
 */
public class ModuleFinderWrapper extends AbstractJava9BaseWrapper {

    /**
     * The ModuleFinder class.
     */
    private final static Class MODULE_FINDER_CLASS = AbstractJava9BaseWrapper.getClassForName("java.lang.module.ModuleFinder");

    private final static Method MODULE_FINDER_METHOD_OF = AbstractJava9BaseWrapper.getMethod(MODULE_FINDER_CLASS, "of", getClassForName("[Ljava.nio.file.Path;"));
    private final static Method MODULE_FINDER_METHOD_FIND_ALL = AbstractJava9BaseWrapper.getMethod(MODULE_FINDER_CLASS, "findAll");


    private ModuleFinderWrapper(Object instance) {
        super(MODULE_FINDER_CLASS, instance);
    }


    /**
     * Find all ModuleReferences.
     * @return A set containing all ModuleReferences
     */
    public Set<ModuleReferenceWrapper> findAll() {

        Set<ModuleReferenceWrapper> resultSet = new HashSet<ModuleReferenceWrapper>();
        Set<Object> moduleReferencesSet = (Set<Object>) AbstractJava9BaseWrapper.invoke(MODULE_FINDER_METHOD_FIND_ALL, getWrappedInstance(), Set.class);

        for (Object moduleReference : moduleReferencesSet) {
            resultSet.add(new ModuleReferenceWrapper(moduleReference));
        }

        return resultSet;

    }


    /**
     * Get the ModuleFinder instance.
     *
     * @param entries the search path
     * @return the ModuleFile wrapper instance
     */
    public static ModuleFinderWrapper of​(PathWrapper... entries) {

        Object[] parameters = new Object[1];
        parameters[0] = PathWrapper.getWrappedPaths(entries);
        Object instance = AbstractJava9BaseWrapper.invoke(MODULE_FINDER_METHOD_OF, null, Object.class, parameters);
        ModuleFinderWrapper result = new ModuleFinderWrapper(instance);

        return result;

    }

    /**
     * Gets the ModuleReference for the passed file
     * @param file
     * @return
     */
    public static ModuleReferenceWrapper getModuleReferenceForFile(File file) {

        File directory = file.isDirectory() ? file : file.getParentFile();

        ModuleFinderWrapper moduleFinder = ModuleFinderWrapper.of​(FileBrigde.toPath(directory));


        for (ModuleReferenceWrapper moduleReference : moduleFinder.findAll()) {

            if (file.toURI().equals(moduleReference.location().get())) {
                return moduleReference;
            }

        }

        return null;

    }

    /**
     * Gets the module name for passed jar file.
     * @param file the jar file
     * @return The name of the module
     */
    public static String getModuleForJarFile(File file) {
        try {

            JarFile jarFile = new JarFile(file);

            ZipEntry moduleInfo = jarFile.getEntry("module-info.class");
            if (moduleInfo != null) {

                ModuleDescriptorWrapper moduleDescriptorWrapper = ModuleDescriptorWrapper.read(jarFile.getInputStream(moduleInfo));
                return moduleDescriptorWrapper.name();

            } else {

                // Fallback for automatic modules... It's really annoying that there isn't a simple method in the JDK to get the module name or for regular AND automatic modules
                Set<ModuleReferenceWrapper> moduleReferenceWrappers = ModuleFinderWrapper.of​(FileBrigde.toPath(file.getParentFile())).findAll();

                // now must get the correct module reference wrapper again
                ModuleReferenceWrapper moduleReferenceWrapper = getMatchingModuleReferenceWrapper(moduleReferenceWrappers, file);

                return moduleReferenceWrapper != null ? moduleReferenceWrapper.descriptor().name() : null;

            }


        } catch (Exception e) {
            return "<NO_MODULE>";
        }
    }


    /**
     * Gets the matching module reference wrapper.
     * @param moduleReferenceWrappers the ModuleReferences to search in
     * @param file the file
     * @return the wrapped ModuleReference
     */
    private static ModuleReferenceWrapper getMatchingModuleReferenceWrapper(Set<ModuleReferenceWrapper> moduleReferenceWrappers, File file) {

        for (ModuleReferenceWrapper moduleReferenceWrapper : moduleReferenceWrappers) {

            OptionalWrapper<URI> uri = moduleReferenceWrapper.location();
            if (uri.isPresent() && uri.get().equals(file.toURI())) {
                return moduleReferenceWrapper;
            }

        }

        return null;
    }

}
