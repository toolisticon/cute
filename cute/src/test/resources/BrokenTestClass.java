package io.toolisticon.cute;

import io.toolisticon.cute.TestAnnotation;

/**
 * Test class for annotation processor tools.
 */
@TestAnnotation
public class BrokenTestClass {
    // invalid code => leads to compiler error
    sdasdasd
}
