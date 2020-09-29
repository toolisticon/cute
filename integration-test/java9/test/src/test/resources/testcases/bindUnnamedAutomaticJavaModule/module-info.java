
module io.toolisticon.cute.integrationtest.java9 {
    requires java.compiler;
    requires java.logging;
    requires transitive integration.test.java9.unnamedautomaticmodule;

    exports io.toolisticon.cute.integrationtest.java9;
}

