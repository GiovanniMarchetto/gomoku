package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.exceptions.GameNotEndedException;
import it.units.sdm.gomoku.model.exceptions.MatchNotEndedException;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.gui.viewmodels.GUIMainViewmodel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import static it.units.sdm.gomoku.ui.gui.GUIMain.guiMainViewmodel;

public class GUISummaryView extends View<GUIMainViewmodel> {

    @FXML
    private Button newMatchButton;
    @FXML
    private Button extraGameButton;
    @FXML
    private Button continueButton;
    @FXML
    private VBox endMatchVBox;

    @FXML
    private Label winnerOfGameLabel;
    @FXML
    private Label winnerOfMatchLabel;
    @FXML
    private Label scoreOfMatchLabel;

    public GUISummaryView() {
        super(guiMainViewmodel);
    }

    @FXML
    private void initialize() {
        GUIMainViewmodel vm = getViewmodelAssociatedWithView();

        continueButton.managedProperty().bind(continueButton.visibleProperty());
        extraGameButton.managedProperty().bind(extraGameButton.visibleProperty());
        newMatchButton.managedProperty().bind(newMatchButton.visibleProperty());

        try {
            Player winnerValue = vm.getWinnerOfTheGame();
            this.winnerOfGameLabel.setText(winnerValue != null
                    ? "WIN OF " + winnerValue
                    : "DRAW");
        } catch (GameNotEndedException e) {
            e.printStackTrace();
        }

        this.scoreOfMatchLabel.setText(vm.getScoreOfMatch().toString()
                .replace(", ", "\n\t")
                .replace("{", "\t")
                .replace("}", "")
                .replace("=", " = "));

        if (!vm.isMatchEnded()) {
            continueButton.setVisible(true);
        } else {
            endMatchVBox.setVisible(true);
            try {
                Player winnerValue = vm.getWinnerOfTheMatch();
                this.winnerOfMatchLabel.setText(winnerValue != null
                        ? "WIN OF " + winnerValue
                        : "DRAW");

                if (vm.getWinnerOfTheMatch() == null) {
                    extraGameButton.setVisible(true);
                }
            } catch (MatchNotEndedException e) {
                e.printStackTrace();
            }
            newMatchButton.setVisible(true);
        }
    }

    @SuppressWarnings("unused")//parameter is needed
    public void continueButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().initializeNewGame();
    }

    @SuppressWarnings("unused")//parameter is needed
    public void extraGameButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().startExtraGame();
    }

    @SuppressWarnings("unused")//parameter is needed
    public void newMatchButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().startNewMatch();
    }
}
