package io.toolisticon.cute.extension.api;

import io.toolisticon.spiap.api.Spi;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import java.util.Set;

@Spi
public interface ModuleSupportSpi {

    void applyModulePath(StandardJavaFileManager stdJavaFileManager, JavaCompiler.CompilationTask compilationTask, Set<String> modules);

    void writeModuleDebugOutput(StringBuilder stringBuilder);

}
