package io.toolisticon.cute.impl;

import io.toolisticon.cute.common.SimpleTestProcessor1;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Unit test for {@link CompileTestUtilities}.
 */
public class CompileTestUtilitiesTest {

    @Test
    public void getAnnotationProcessorWasAppliedMessage_concreteProcessor() {
        Processor processor = new SimpleTestProcessor1();
        MatcherAssert.assertThat(
                CompileTestUtilities.getAnnotationProcessorWasAppliedMessage(processor),
                Matchers.equalTo(
                        CompileTestUtilities.TEMPLATE_ANNOTATION_PROCESSOR_WAS_APPLIED
                                .replaceFirst("[%]s", SimpleTestProcessor1.class.getCanonicalName()
                                )
                                .replaceFirst("[%]s", "" + System.identityHashCode(processor))));
    }

    @Test
    public void getAnnotationProcessorWasAppliedMessage_anynomousProcessor_class() {

        Processor processor = new AbstractProcessor() {
            @Override
            public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
                return false;
            }
        };

        MatcherAssert.assertThat(
                CompileTestUtilities.getAnnotationProcessorWasAppliedMessage(processor),
                Matchers.equalTo(
                        CompileTestUtilities.TEMPLATE_ANNOTATION_PROCESSOR_WAS_APPLIED.replaceFirst(
                                "[%]s",
                                CompileTestUtilities.ANONYMOUS_CLASS.replaceFirst("[%]s", AbstractProcessor.class.getCanonicalName())
                        )
                                .replaceFirst("[%]s", "" + System.identityHashCode(processor))
                )
        );
    }

    @Test
    public void getAnnotationProcessorWasAppliedMessage_anynomousProcessor_interface() {

        Processor processor = new Processor() {
            @Override
            public Set<String> getSupportedOptions() {
                return null;
            }

            @Override
            public Set<String> getSupportedAnnotationTypes() {
                return null;
            }

            @Override
            public SourceVersion getSupportedSourceVersion() {
                return null;
            }

            @Override
            public void init(ProcessingEnvironment processingEnv) {

            }

            @Override
            public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
                return false;
            }

            @Override
            public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
                return null;
            }
        };

        MatcherAssert.assertThat(
                CompileTestUtilities.getAnnotationProcessorWasAppliedMessage(processor),
                Matchers.equalTo(

                        CompileTestUtilities.TEMPLATE_ANNOTATION_PROCESSOR_WAS_APPLIED.replaceFirst(
                                "[%]s",
                                CompileTestUtilities.ANONYMOUS_CLASS
                                        .replaceFirst("[%]s", Processor.class.getCanonicalName()
                                        )

                        )
                                .replaceFirst("[%]s", "" + System.identityHashCode(processor)
                                )
                )

        );
    }

    @Test
    public void getAnnotationProcessorWasAppliedMessage_nullValueProcessor() {
        MatcherAssert.assertThat(
                CompileTestUtilities.getAnnotationProcessorWasAppliedMessage(null),
                Matchers.equalTo(CompileTestUtilities.TEMPLATE_ANNOTATION_PROCESSOR_WAS_APPLIED.replaceFirst("[%]s", "").replaceFirst("[#][%]s", "#NULL")));
    }


}
