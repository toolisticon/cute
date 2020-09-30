
module io.toolisticon.cute.integrationtest.java9 {
    requires java.compiler;
    requires java.logging;
    requires transitive io.toolisticon.cute.integrationtest.java9.regularmodule;

    exports io.toolisticon.cute.integrationtest.java9;
}

