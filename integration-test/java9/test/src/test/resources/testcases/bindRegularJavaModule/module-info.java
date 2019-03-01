
module io.toolisticon.compiletesting.integrationtest.java9 {
    requires java.compiler;
    requires java.logging;
    requires transitive io.toolisticon.compiletesting.integrationtest.java9.regularmodule;

    exports io.toolisticon.compiletesting.integrationtest.java9;
}

