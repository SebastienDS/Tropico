module tropico {
    requires com.google.gson;

    opens tropico to com.google.gson;
    exports tropico;
}