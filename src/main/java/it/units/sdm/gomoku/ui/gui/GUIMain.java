package it.units.sdm.gomoku.ui.gui;

import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

import static it.units.sdm.gomoku.ui.gui.SceneController.ViewName.*;

public class GUIMain extends Application {

    public static final String START_VIEW_FXML_FILE_NAME = "start-view.fxml";
    public static final String MAIN_VIEW_FXML_FILE_NAME = "main-view.fxml";
    public static final String SUMMARY_VIEW_FXML_FILE_NAME = "summary-view.fxml";

    public static final MainViewmodel guiMainViewmodel = new MainViewmodel();
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
                new Pair<>(START_VIEW, FXML_LOCATION_PATH + START_VIEW_FXML_FILE_NAME),
                new Pair<>(MAIN_VIEW, FXML_LOCATION_PATH + MAIN_VIEW_FXML_FILE_NAME),
                new Pair<>(SUMMARY_VIEW, FXML_LOCATION_PATH + SUMMARY_VIEW_FXML_FILE_NAME)
        );
        stage.show();
    }
}