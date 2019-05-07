package io.toolisticon.compiletesting.extension.modulesupport;

import io.toolisticon.compiletesting.extension.api.ModuleSupportSpi;
import io.toolisticon.spiap.api.Service;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

@Service(ModuleSupportSpi.class)
public class ModuleSupportService implements ModuleSupportSpi {

    @Override
    public void applyModulePath(StandardJavaFileManager stdJavaFileManager, JavaCompiler.CompilationTask compilationTask, Set<String> modules) {

        Map<String, File> moduleNameToJarMap = getModuleNameToJarMap(modules);

        // handle java 9 modules via reflection to maintain backward compatibility
        if (modules != null) {
            try {

                List<File> files = new ArrayList<File>();
                for (String module : modules) {

                    File moduleFile = moduleNameToJarMap.get(module);
                    if (moduleFile != null) {
                        files.add(moduleFile);
                    }

                }
                stdJavaFileManager.setLocation(StandardLocation.MODULE_PATH, files);

                compilationTask.addModules(modules);

            } catch (Exception e) {
                // method not found or access issues ==> java version <9
                // ignore => only thrown for java <9
                e.printStackTrace();
            }
        }

    }

    @Override
    public void writeModuleDebugOutput(StringBuilder stringBuilder) {

        int i = 0;
        for (File file : getJarsFromClasspath()) {
            stringBuilder.append("[")
                    .append(i++)
                    .append("|")
                    .append(getModuleForJarFile(file))
                    .append("] := '")
                    .append(file.getAbsolutePath())
                    .append("'\n");
        }

    }

    Map<String, File> getModuleNameToJarMap(Set<String> modules) {

        // get a map for lookup of jar files by module name
        Map<String, File> moduleToJarMap = new HashMap<String, File>();
        if (modules != null) {
            try {


                List<File> files = getJarsFromClasspath();

                for (File file : files) {

                    String moduleName = getModuleForJarFile(file);

                    if (moduleName != null) {
                        moduleToJarMap.put(moduleName, file);
                    }

                }


            } catch (Exception e) {
                // ignore => only thrown for java <9
                e.printStackTrace();
            }


        }

        return moduleToJarMap;

    }

    /**
     * Gets the module name for passed jar file.
     *
     * @param file the jar file
     * @return The name of the module
     */
    static String getModuleForJarFile(File file) {
        try {

            JarFile jarFile = new JarFile(file);

            ZipEntry moduleInfo = jarFile.getEntry("module-info.class");
            if (moduleInfo != null) {

                ModuleDescriptor moduleDescriptorWrapper = ModuleDescriptor.read(jarFile.getInputStream(moduleInfo));
                return moduleDescriptorWrapper.name();

            } else {

                // Fallback for automatic modules... It's really annoying that there isn't a simple method in the JDK to get the module name or for regular AND automatic modules
                Set<ModuleReference> moduleReferenceWrappers = ModuleFinder.of(file.toPath()).findAll();

                // now must get the correct module reference wrapper again
                ModuleReference moduleReferenceWrapper = getMatchingModuleReferenceWrapper(moduleReferenceWrappers, file);

                return moduleReferenceWrapper != null ? moduleReferenceWrapper.descriptor().name() : null;

            }


        } catch (Exception e) {
            return "<NO_MODULE>";
        }
    }

    /**
     * Gets the matching module reference wrapper.
     *
     * @param moduleReferenceWrappers the ModuleReferences to search in
     * @param file                    the file
     * @return the wrapped ModuleReference
     */
    static ModuleReference getMatchingModuleReferenceWrapper(Set<ModuleReference> moduleReferenceWrappers, File file) {

        for (ModuleReference moduleReferenceWrapper : moduleReferenceWrappers) {

            Optional<URI> uri = moduleReferenceWrapper.location();
            if (uri.isPresent() && uri.get().equals(file.toURI())) {
                return moduleReferenceWrapper;
            }

        }

        return null;
    }


    static List<File> getJarsFromClasspath() {
        List<File> urls = new ArrayList<File>();

        String[] pathElements = System.getProperty("java.class.path").split(System.getProperty("path.separator"));

        for (String pathElement : pathElements) {

            if (pathElement.endsWith(".jar")) {

                File file = new File(pathElement);
                if (file.exists()) {
                    urls.add(file);
                }

            }
        }

        return urls;
    }


}
