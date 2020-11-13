module tropico {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens tropico to javafx.fxml;
    exports tropico;
}