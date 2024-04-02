package io.toolisticon.cute;


import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Some static utility functions.
 */
final class CompileTestUtilities {

    static final String ANONYMOUS_CLASS = "ANONYMOUS CLASS<%s>";
    final static String TEMPLATE_ANNOTATION_PROCESSOR_WAS_APPLIED = "!!!--- ANNOTATION PROCESSOR (%s)<#%s> WAS APPPLIED ---!!!";

    /**
     * Hidden constructor.
     */
    private CompileTestUtilities() {

    }

    /**
     * Produces messages string depending on passed processor to check if processor has been applied.
     *
     * @param processor the processor to create the message for
     * @return the message to check if processor has been applied
     */
    static String getAnnotationProcessorWasAppliedMessage(Processor processor) {
        return String.format(TEMPLATE_ANNOTATION_PROCESSOR_WAS_APPLIED,
                processor != null && processor.getClass() != null ? (
                        processor.getClass().getCanonicalName() != null ?
                                processor.getClass().getCanonicalName() :
                                String.format(ANONYMOUS_CLASS,
                                        Object.class.equals(processor.getClass().getSuperclass()) ?
                                                Processor.class.getCanonicalName()
                                                : processor.getClass().getSuperclass().getCanonicalName()
                                )
                ) : "",
                processor != null ? System.identityHashCode(processor) : "NULL");
    }

    /**
     * Gets all messages of a specific kind.
     *
     * @param diagnostics the compilations diagnostics result
     * @param kind        the kind of the messages to return
     * @return a Set containing all messages of passed kind, or an empty Set.
     */
    static Set<String> getMessages(DiagnosticCollector<JavaFileObject> diagnostics, Diagnostic.Kind kind) {

        return getDiagnosticByKind(diagnostics,kind).stream().map(e -> e.getMessage(null)).collect(Collectors.toSet());

    }

    /**
     * Filters Diagnostics by kind.
     *
     * @param diagnostics the compilations diagnostics result
     * @param kind        the kind of the messages to return
     * @return a Set containing all Diagnostic element of passed kind, or an empty Set.
     */
    static Set<Diagnostic<? extends JavaFileObject>> getDiagnosticByKind(DiagnosticCollector<JavaFileObject> diagnostics, Diagnostic.Kind kind) {

        return diagnostics.getDiagnostics().stream().filter(e -> e.getKind().equals(kind)).collect(Collectors.toSet());

    }


}