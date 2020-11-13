module tropico {
    requires javafx.controls;
    requires javafx.fxml;

    opens tropico to javafx.fxml;
    exports tropico;
}