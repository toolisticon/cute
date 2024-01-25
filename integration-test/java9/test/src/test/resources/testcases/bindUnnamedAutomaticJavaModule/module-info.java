
module io.toolisticon.cute.integrationtest.javanine {
    requires java.compiler;
    requires java.logging;
    requires transitive integration.test.javanine.unnamedautomaticmodule;

    exports io.toolisticon.cute.integrationtest.javanine;
}

