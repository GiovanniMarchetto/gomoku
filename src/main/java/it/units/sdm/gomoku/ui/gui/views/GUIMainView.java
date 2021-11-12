package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.mvvm_library.Observer;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.gui.GUIMain;
import it.units.sdm.gomoku.ui.gui.SceneController;
import it.units.sdm.gomoku.ui.gui.viewmodels.GUIMainViewmodel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.beans.PropertyChangeEvent;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GUIMainView extends View<GUIMainViewmodel> implements Observer {

    @FXML
    private Label currentPlayerLabel;
    @FXML
    private Circle currentPlayerCircle;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private GridPane centerGridPane;
    @FXML
    private GridPane topGridPane;
    @FXML
    private VBox rightGridPane;

    @FXML
    private Label whitePlayerLabel;
    @FXML
    private Label blackPlayerLabel;
    @FXML
    private Label startTime;

    public GUIMainView() {
        super(GUIMain.guiMainViewmodel);
        observe(getViewmodelAssociatedWithView());
    }

    @FXML
    private void initialize() {

        GUIMainViewmodel vm = getViewmodelAssociatedWithView();

        addObservedPropertyOfViewmodel(
                vm.getCurrentPlayerProperty(), evt ->
                        SceneController.executeOnJavaFxUiThread(() -> {
                            currentPlayerLabel.setText(evt.getNewValue().toString());
                            currentPlayerCircle.setFill(
                                    getViewmodelAssociatedWithView().getColorOfCurrentPlayer() == Stone.Color.BLACK
                                            ? Color.BLACK : Color.WHITE);
                        }));

        double discardSafeMisure = 50;
        double discardHeight = topGridPane.getPrefHeight();// 50;
        double discardWidth = rightGridPane.getPrefWidth();// 100;

        GomokuGridManager gomokuGridManager = new GomokuGridManager(vm, mainAnchorPane,
                discardWidth + discardSafeMisure, discardHeight + discardSafeMisure);
        GridPane baseGridPane = gomokuGridManager.getGridPane();

        centerGridPane.getChildren().add(baseGridPane);

        blackPlayerLabel.setText(vm.getCurrentBlackPlayer().toString());
        whitePlayerLabel.setText(vm.getCurrentWhitePlayer().toString());

        ZonedDateTime startZoneDateTime = vm.getGameStartTime();
        startTime.setText("Start game: \n" +
                DateTimeFormatter.ofPattern("HH:mm:ss\ndd/MM/yyyy").format(startZoneDateTime) + "\n");
    }

    @Override
    public void onViewInitialized() {
        super.onViewInitialized();
        getViewmodelAssociatedWithView().triggerFirstMove();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}