package io.toolisticon.compiletesting.impl;

import javax.annotation.processing.Processor;

/**
 * Some static utility functions.
 */
public class CompileTestUtilities {

    protected static final String ANONYMOUS_CLASS = "ANONYMOUS CLASS<%s>";
    protected final static String TEMPLATE_ANNOTATION_PROCESSOR_WAS_APPLIED = "!!!--- ANNOTATION PROCESSOR (%s)<#%s> WAS APPPLIED ---!!!";

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
    public static String getAnnotationProcessorWasAppliedMessage(Processor processor) {
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
                processor!= null ? System.identityHashCode(processor): "NULL");
    }

}
