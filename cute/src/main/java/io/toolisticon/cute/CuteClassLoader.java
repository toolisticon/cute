package io.toolisticon.cute;

public interface CuteClassLoader {

    Class<?> getClass(String binaryClassName) throws ClassNotFoundException;

}
