package it.units.sdm.gomoku.ui.gui;

import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

import static it.units.sdm.gomoku.ui.gui.SceneController.Views.*;

public class Main extends Application {

    public static final MainViewmodel mainViewmodel = new MainViewmodel();
    private static final String FXML_LOCATION_PATH = "views/";
    private final static int initialSceneWidthInPx = 630;
    private final static int initialSceneHeightInPx = 580;
    private final static int stageMinWidth = 550;
    private final static int stageMinHeight = 580;
    private final static String stageTitle = "Gomoku!";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        SceneController.initialize(stage, stageTitle, initialSceneWidthInPx, initialSceneHeightInPx,
                stageMinWidth, stageMinHeight,
                new Pair<>(START_VIEW, FXML_LOCATION_PATH + "start-view.fxml"),
                new Pair<>(MAIN_VIEW, FXML_LOCATION_PATH + "main-view.fxml")
        );
        stage.show();
    }
}