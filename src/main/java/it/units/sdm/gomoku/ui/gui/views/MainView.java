package it.units.sdm.gomoku.ui.gui.views;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class MainView {

    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private GridPane centerGridPane;
    @FXML
    private GridPane topGridPane;
    @FXML
    private GridPane rightGridPane;

    @FXML
    private void initialize() {

        int boardSize = 19;
        double discardSafeMisure = 50;
        double discardHeight = topGridPane.getPrefHeight();// 50;
        double discardWidth = rightGridPane.getPrefWidth();// 100;

        GomokuGridManager gomokuGridManager = new GomokuGridManager(boardSize, mainAnchorPane,
                discardWidth + discardSafeMisure, discardHeight + discardSafeMisure);
        GridPane baseGridPane = gomokuGridManager.getGridPane();

        centerGridPane.getChildren().add(baseGridPane);

    }

}