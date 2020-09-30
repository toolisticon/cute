package io.toolisticon.cute;

import org.hamcrest.MatcherAssert;

public final class TestUtilities {

    private TestUtilities() {

    }

    public static void assertAssertionMessageContainsMessageTokensAssertion(AssertionError assertionError, String message) {

        MatcherAssert.assertThat("AssertionError message '" + assertionError.getMessage() + "' should have matched message string: '" + message + "'", assertAssertionMessageContainsMessageTokens(assertionError, message));

    }

    public static boolean assertAssertionMessageContainsMessageTokens(AssertionError assertionError, String message) {

        String[] messageTokens = message.split("[%]s");

        for (String messageToken : messageTokens) {

            if (!assertionError.getMessage().contains(messageToken)) {
                return false;
            }

        }

        return true;

    }

}
