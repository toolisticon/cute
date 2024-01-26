package io.toolisticon.cute;

import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class CuteClassLoaderImpl extends ClassLoader implements CuteClassLoader {

    final CompileTestFileManager compileTestFileManager;

    final Map<String, Class<?>> classMap = new HashMap<>();

    public CuteClassLoaderImpl(CompileTestFileManager compileTestFileManager) {
        this.compileTestFileManager = compileTestFileManager;

        // get classes
        List<JavaFileObject> javaFileObjectList = compileTestFileManager.getGeneratedJavaFileObjects().stream()
                .filter(e -> e.getKind() == JavaFileObject.Kind.CLASS)
                .collect(Collectors.toList());

        for (JavaFileObject javaFileObject : javaFileObjectList) {
            try {
                byte[] byteArray = readAllBytes(javaFileObject.openInputStream());
                String className = convertJavaFileObjectNameToBinaryClassName(javaFileObject);
                classMap.put(className, defineClass(className, byteArray, 0, byteArray.length));
            } catch (IOException e) {
                // Ignore for the moment
            }
        }

    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

    static String convertJavaFileObjectNameToBinaryClassName(JavaFileObject javaFileObject) {
        Pattern pattern = Pattern.compile("^[/](.*)[.]class$");
        Matcher matcher = pattern.matcher(javaFileObject.getName());
        if (matcher.matches()) {
            return matcher.group(1).replaceAll("[/]",".");
        }
        throw new IllegalStateException("Got invalid name : " + javaFileObject.getName());
    }

    @Override
    public <TYPE> Class<TYPE> getClass(String binaryClassName) throws ClassNotFoundException {


        Class<TYPE> clazz =  (Class<TYPE>) this.classMap.get(binaryClassName);

        if(clazz == null){
            throw new ClassNotFoundException(binaryClassName);
        }
        return clazz;
    }
}
