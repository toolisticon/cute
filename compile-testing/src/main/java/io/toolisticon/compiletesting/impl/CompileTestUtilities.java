package io.toolisticon.compiletesting.impl;


import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.HashSet;
import java.util.Set;

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

        Set<String> messages = new HashSet<>();
        Set<Diagnostic> filteredDiagnostic = getDiagnosticByKind(diagnostics, kind);

        for (Diagnostic diagnostic : filteredDiagnostic) {
            messages.add(diagnostic.getMessage(null));
        }

        return messages;

    }

    /**
     * Filters Diagnostics by kind.
     *
     * @param diagnostics the compilations diagnostics result
     * @param kind        the kind of the messages to return
     * @return a Set containing all Diagnostic element of passed kind, or an empty Set.
     */
    static Set<Diagnostic> getDiagnosticByKind(DiagnosticCollector<JavaFileObject> diagnostics, Diagnostic.Kind kind) {

        Set<Diagnostic> filteredDiagnostics = new HashSet<>();

        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
            if (kind == diagnostic.getKind()) {
                filteredDiagnostics.add(diagnostic);
            }
        }

        return filteredDiagnostics;

    }


}