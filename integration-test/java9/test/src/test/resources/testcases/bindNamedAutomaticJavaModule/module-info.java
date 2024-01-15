module io.toolisticon.cute.integrationtest.javanine {
    requires java.compiler;
    requires java.logging;
    requires transitive integration.test.javanine.namedautomaticmodule;

    exports io.toolisticon.cute.integrationtest.javanine;
}

