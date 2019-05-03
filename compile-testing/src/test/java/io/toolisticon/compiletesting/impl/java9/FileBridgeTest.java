package io.toolisticon.compiletesting.impl.java9;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;

public class FileBridgeTest {

    @Test
    public void test_toPath() {

        if (!Java9SupportCheck.UNSUPPORTED_JAVA_VERSION) {

            File file = new File("/a/b/TATA.txt");

            PathWrapper result = FileBrigde.toPath(file);
            MatcherAssert.assertThat(result.getWrappedPath(), Matchers.notNullValue());

        } else {
            System.out.println("!!! TEST SKIPPED !!!");
        }

    }

}
