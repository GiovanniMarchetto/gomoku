package it.units.sdm.gomoku;

import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static final String FXML_LOCATION_PATH = "ui/gui/views/";
    public static final MainViewmodel mainViewmodel = new MainViewmodel();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader startFxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_LOCATION_PATH + "start-view.fxml"));
        Scene startScene = new Scene(startFxmlLoader.load());

        FXMLLoader mainFxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_LOCATION_PATH + "main-view.fxml"));
        //Scene mainScene = new Scene(mainFxmlLoader.load(), 630, 580);


        stage.setMinHeight(550);
        stage.setMinWidth(580);
        stage.setTitle("Gomoku!");

        stage.setScene(startScene);
        stage.show();
    }
}