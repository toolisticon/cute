package io.toolisticon.compiletesting.impl.java9;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class ModuleFinderWrapper extends AbstractJava9BaseWrapper {

    private final static Class MODULE_FINDER_CLASS = AbstractJava9BaseWrapper.getClassForName("java.lang.module.ModuleFinder");

    private final static Method MODULE_FINDER_METHOD_OF = AbstractJava9BaseWrapper.getMethod(MODULE_FINDER_CLASS, "of", getClassForName("[Ljava.nio.file.Path;"));
    private final static Method MODULE_FINDER_METHOD_FIND_ALL = AbstractJava9BaseWrapper.getMethod(MODULE_FINDER_CLASS, "findAll");


    private ModuleFinderWrapper(Object instance) {
        super(MODULE_FINDER_CLASS, instance);
    }


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

    public static String getModuleForJarFile(File file) {
        try {
            Set<ModuleReferenceWrapper> moduleReferenceWrappers = ModuleFinderWrapper.of​(FileBrigde.toPath(file.getParentFile())).findAll();

            // now must get the correct module reference wrapper again
            ModuleReferenceWrapper moduleReferenceWrapper = getMatchingModuleReferenceWrapper(moduleReferenceWrappers, file);

            return moduleReferenceWrapper != null ? moduleReferenceWrapper.descriptor().name() : null;
        } catch (Exception e) {
            return "<NO_MODULE>";
        }
    }


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
