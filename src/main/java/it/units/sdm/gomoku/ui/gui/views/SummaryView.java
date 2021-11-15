package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.actors.Player;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.gui.viewmodels.GUIMainViewmodel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import static it.units.sdm.gomoku.ui.gui.GUIMain.guiMainViewmodel;

public class SummaryView extends View<GUIMainViewmodel> {

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

    public SummaryView() {
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
        } catch (Game.GameNotEndedException e) {
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
            } catch (Match.MatchNotEndedException e) {
                e.printStackTrace();
            }
            newMatchButton.setVisible(true);
        }
    }

    public void continueButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().startNewGame();
    }

    public void extraGameButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().startExtraGame();
    }

    public void newMatchButtonOnMouseClicked(MouseEvent e) {
        getViewmodelAssociatedWithView().startNewMatch();
    }
}
