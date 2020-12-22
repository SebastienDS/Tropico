package tropico.controllers;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SceneManagement.createScene("primary", 640, 480);
        stage.setScene(SceneManagement.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}