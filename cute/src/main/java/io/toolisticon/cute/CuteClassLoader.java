package io.toolisticon.cute;

public interface CuteClassLoader {

    <TYPE> Class<TYPE> getClass(String binaryClassName) throws ClassNotFoundException;

}
