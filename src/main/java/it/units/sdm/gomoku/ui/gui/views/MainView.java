package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.mvvm_library.views.View;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static it.units.sdm.gomoku.ui.gui.GUIMain.mainViewmodel;

public class MainView extends View {

    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private GridPane centerGridPane;
    @FXML
    private GridPane topGridPane;
    @FXML
    private VBox rightGridPane;

    @FXML
    private Label whitePlayer;
    @FXML
    private Label blackPlayer;
    @FXML
    private Label startTime;
    @FXML
    private Label currentTime;

    public MainView() {
        super(mainViewmodel);
    }

    @FXML
    private void initialize() {

        MainViewmodel vm = (MainViewmodel) getViewmodelAssociatedWithView();

        double discardSafeMisure = 50;
        double discardHeight = topGridPane.getPrefHeight();// 50;
        double discardWidth = rightGridPane.getPrefWidth();// 100;

        GomokuGridManager gomokuGridManager = new GomokuGridManager(vm, mainAnchorPane,
                discardWidth + discardSafeMisure, discardHeight + discardSafeMisure);
        GridPane baseGridPane = gomokuGridManager.getGridPane();

        centerGridPane.getChildren().add(baseGridPane);

        blackPlayer.setText(vm.getCurrentBlackPlayer().toString());
        whitePlayer.setText(vm.getCurrentWhitePlayer().toString());

        ZonedDateTime startZoneDateTime = vm.getGameStartTime();
        startTime.setText("Start game: \n" +
                DateTimeFormatter.ofPattern("HH:mm:ss\ndd/MM/yyyy").format(startZoneDateTime) + "\n");
    }


}