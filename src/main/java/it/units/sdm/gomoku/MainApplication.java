package it.units.sdm.gomoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("ui/gui/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),630,580);
        stage.setMinHeight(550);
        stage.setMinWidth(580);

        stage.setTitle("Gomoku!");
        stage.setScene(scene);
        stage.show();
    }
}