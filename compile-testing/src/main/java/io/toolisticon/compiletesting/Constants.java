package io.toolisticon.compiletesting;

import java.lang.annotation.Annotation;

/**
 * Declares constants and Default values used in this project.
 */
public final class Constants {

    public final static Class<? extends Annotation> DEFAULT_ANNOTATION = TestAnnotation.class;
    public final static String DEFAULT_UNIT_TEST_SOURCE_FILE = "/AnnotationProcessorUnitTestClass.java";


    public static final class Messages {

        public static class Message {

            private final String message;

            Message(String message) {
                this.message = message;
            }

            public String produceMessage(Object... token) {
                return String.format(message, token);
            }

            public String getMessagePattern(){
                return message;
            }

        }

        // Assertions related messages
        public final static Message UNIT_TEST_PRECONDITION_INCOMPATIBLE_ELEMENT_TYPE = new Message("PRECONDITION : Processed Element type doesn't match the one expected by your unit test processor");
        public final static Message UNIT_TEST_PRECONDITION_MUST_FIND_EXACTLY_ONE_ELEMENT = new Message("PRECONDITION: Expected to find exactly one element annotated with %s in processed sources files");

        public final static Message ASSERTION_GOT_UNEXPECTED_EXCEPTION_INSTEAD_OF_EXPECTED = new Message("Expected exception of type '%s' but exception of type '%s%s' was thrown instead.");
        public final static Message ASSERTION_GOT_UNEXPECTED_EXCEPTION = new Message("An unexpected exception of type '%s%s' has been thrown.");
        public final static Message ASSERTION_EXPECTED_EXCEPTION_NOT_THROWN = new Message("Expected exception of type '%s' to be thrown, but wasn't.");

        public final static String TOKEN__WITH_MESSAGE = "'  with message '";

        // Compile test messages
        public final static Message MESSAGE_COMPILATION_SHOULD_SUCCEED_AND_ERROR_MESSAGE_EXPECTED = new Message("Test configuration error : Compilation should succeed but error messages is expected too !!!");
        public final static Message MESSAGE_COMPILATION_SHOULD_HAVE_SUCCEEDED_BUT_FAILED = new Message("Compilation should have succeeded but failed");
        public final static Message MESSAGE_COMPILATION_SHOULD_HAVE_FAILED_BUT_SUCCEEDED = new Message("Compilation should have failed but succeeded");

        public final static Message MESSAGE_JFO_DOESNT_EXIST = new Message("Expected generated JavaFileObject (%s) doesn't exist.");
        public final static Message MESSAGE_JFO_EXISTS_BUT_SHOULD_BE_NON_EXISTENT = new Message("Expected JavaFileObject (%s) to be non existent.");

        public final static Message MESSAGE_JFO_EXISTS_BUT_DOESNT_MATCH_MATCHER = new Message("Expected generated JavaFileObject (%s) exists but doesn't match passed GeneratedFileObjectMatcher: %s");

        public final static Message MESSAGE_FO_COMPARISION_FAILED = new Message("Check with GeneratedFileObjectMatcher %s failed.");

        public final static Message MESSAGE_FO_DOESNT_EXIST = new Message("Expected generated FileObject (%s) doesn't exist.");
        public final static Message MESSAGE_FO_EXISTS_BUT_SHOULD_BE_NON_EXISTENT = new Message("Expected FileObject (%s) to be non existent.");

        public final static Message MESSAGE_FO_EXISTS_BUT_DOESNT_MATCH_MATCHER = new Message("Expected generated FileObject (%s) exists but doesn't match passed GeneratedFileObjectMatcher: %s");

        public final static Message MESSAGE_PROCESSOR_HASNT_BEEN_APPLIED = new Message("Annotation processor %s hasn't been called! Please check that there's at least one source file using an annotation supported by the processor: %s");
        public final static Message MESSAGE_HAVENT_FOUND_MESSSAGE = new Message("Haven't found expected message string '%s' of kind %s!");

        public final static Message MESSAGE_TECHNICAL_ERROR = new Message("TECHNICAL ERROR : %s");


        // IllegalArgumentException Messages
        public final static Message IAE_PASSED_PARAMETER_MUST_NOT_BE_NULL = new Message("Passed %s must not be null");

        public final static Message IAE_CANNOT_INSTANTIATE_PROCESSOR = new Message("Cannot instantiate passed processor of type '%s'. Make sure that a NoArg constructor exists and is accessible.");
        public final static Message IAE_CANNOT_FIND_JAVAFILEOBJECT = new Message("Can't find JavaFileObject for uri:%s");
        public final static Message IAE_CANNOT_FIND_FILEOBJECT = new Message("Can't find FileObject for uri:%s");

        // IllegalStateException
        public final static Message ISE_CANNOT_OPEN_INPUTSTREAM_WITH_URI = new Message("Cannot open InputStream for resource with uri '%s' ! ");
        public final static Message ISE_MUST_CONFIGURE_AT_LEAST_ONE_SOURCE_FILE = new Message("At least one source file has to be added to the compiler test configuration");
        public final static Message ISE_MUST_CONFIGURE_AT_LEAST_ONE_PROCESSOR = new Message( "At least one processor has to be added to the compiler test configuration");

        // Generated File Object Matchers
        public final static Message GFOM_FILEOBJECTS_ARENT_EQUAL_BY_BINARY_COMPARISION = new Message("FileObjects aren't equal by using binary comparison");
        public final static Message GFOM_COULDNT_FIND_SUBSTRING = new Message("Couldn't find substring %s in file %s");
        public final static Message GFOM_FILEOBJECTS_ARENT_EQUAL_BY_TEXTUAL_COMPARISION_WITH_IGNORE_LINEENDINGS = new Message("FileObjects aren't equal by doing textual comparision and ignoring line endings");
        public final static Message GFOM_FILEOBJECT_DOESNT_MATCH_PATTERN = new Message("File '%s' doesn't match the following pattern: '%s'!");
        public final static Message GFOM_FILEOBJECT_IS_NOT_WELL_FORMED = new Message("File %s is no well formed xml file!");


        /**
         * Hidden constructor.
         */
        private Messages() {

        }

    }

    /**
     * Hidden constructor.
     */
    private Constants() {

    }

}
