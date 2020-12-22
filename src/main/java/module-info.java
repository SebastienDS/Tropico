module tropico {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens tropico.controllers to javafx.fxml;
    exports tropico.controllers;
}