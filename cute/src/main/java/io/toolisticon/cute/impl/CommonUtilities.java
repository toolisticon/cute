package io.toolisticon.cute.impl;

import java.util.Random;

/**
 * Some common utility functions.
 */
public final class CommonUtilities {

    /**
     * Hidden constructor.
     */
    private CommonUtilities() {

    }

    public static String getRandomString(int length) {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

}
