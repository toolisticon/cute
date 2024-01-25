module io.toolisticon.cute.integrationtest.javanine {
    requires java.compiler;
    requires java.logging;
    requires transitive io.toolisticon.cute.integrationtest.javanine.regularmodule;

    exports io.toolisticon.cute.integrationtest.javanine;
}

