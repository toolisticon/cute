package io.toolisticon.cute.common;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class SimpleTestProcessor1 extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(SimpleTestAnnotation1.class)){

            // generate file
            TypeElement typeElement = (TypeElement) element;
            String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
            String simpleName = typeElement.getSimpleName() + "GeneratedClass";
            String className = typeElement.getQualifiedName() + "GeneratedClass";
            String value = typeElement.getAnnotation(SimpleTestAnnotation1.class).value();

            try {
                JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(className, element);

                BufferedWriter bufferWriter = new BufferedWriter(javaFileObject.openWriter());
                bufferWriter.write("package " + packageName + ";");
                bufferWriter.newLine();
                bufferWriter.write("import " + SimpleTestProcessor1Interface.class.getCanonicalName() + ";");
                bufferWriter.newLine();
                bufferWriter.write("public class " + simpleName + " implements " + SimpleTestProcessor1Interface.class.getSimpleName() + "{");
                bufferWriter.newLine();
                bufferWriter.write("public String getOutput (){return \"" + value + "\";}");
                bufferWriter.newLine();
                bufferWriter.write("}");
                bufferWriter.flush();
                bufferWriter.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        return false;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {

        return new HashSet<String>(Arrays.asList(SimpleTestAnnotation1.class.getCanonicalName()));

    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return this.processingEnv;
    }
}