package io.toolisticon.cute;

import javax.tools.JavaFileObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class CuteClassLoaderImpl extends ClassLoader implements CuteClassLoader {

    final CompileTestFileManager compileTestFileManager;

    final Map<String, CompileTestFileManager.InMemoryOutputJavaFileObject> classMap = new HashMap<>();

    public CuteClassLoaderImpl(CompileTestFileManager compileTestFileManager) {
        this.compileTestFileManager = compileTestFileManager;

        // get classes
        List<CompileTestFileManager.InMemoryOutputJavaFileObject> javaFileObjectList = compileTestFileManager.getGeneratedJavaFileObjects().stream()
                .filter(e -> e.getKind() == JavaFileObject.Kind.CLASS)
                .collect(Collectors.toList());

        for (CompileTestFileManager.InMemoryOutputJavaFileObject javaFileObject : javaFileObjectList) {

            String className = convertJavaFileObjectNameToBinaryClassName(javaFileObject);
            classMap.put(className, javaFileObject);

        }

    }


    static String convertJavaFileObjectNameToBinaryClassName(JavaFileObject javaFileObject) {
        Pattern pattern = Pattern.compile("^/(.*)[.]class$");
        Matcher matcher = pattern.matcher(javaFileObject.getName());
        if (matcher.matches()) {
            return matcher.group(1).replaceAll("/", ".");
        }
        throw new IllegalStateException("Got invalid name : " + javaFileObject.getName());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        if (!classMap.containsKey(name)) {
            throw new ClassNotFoundException("Couldn't find class : " + name);
        }

        CompileTestFileManager.InMemoryOutputJavaFileObject javaFileObject = classMap.get(name);

        return defineClass(name, javaFileObject.getContent(), 0, javaFileObject.getContent().length);
    }

    @Override
    public Class<?> getClass(String binaryClassName) throws ClassNotFoundException {
        return this.loadClass(binaryClassName);
    }
}
