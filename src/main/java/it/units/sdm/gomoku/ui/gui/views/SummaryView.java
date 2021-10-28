package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.mvvm_library.views.View;
import it.units.sdm.gomoku.mvvm_library.views.gui_items.CommanderButton;
import it.units.sdm.gomoku.ui.gui.viewmodels.MainViewmodel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static it.units.sdm.gomoku.ui.gui.GUIMain.mainViewmodel;

public class SummaryView extends View {

    public static final String continueAfterSummaryPropertyName = "continue";
    public static final String newMatchAfterSummaryPropertyName = "newMatch";
    public static final String extraGameAfterSummaryPropertyName = "extraGame";

    @FXML
    private HBox buttonHBox;
    @FXML
    private VBox endMatchVBox;

    @FXML
    private Label winnerOfGame;
    @FXML
    private Label winnerOfMatch;
    @FXML
    private Label scoreOfMatch;

    public SummaryView() {
        super(mainViewmodel);
    }

    @FXML
    private void initialize() {
        var vm = (MainViewmodel) getViewmodelAssociatedWithView();

        try {
            Player winnerValue = vm.getWinnerOfTheGame();
            this.winnerOfGame.setText(winnerValue != null
                    ? "WIN OF " + winnerValue
                    : "DRAW");
        } catch (Game.GameNotEndedException e) {
            e.printStackTrace();
        }

        this.scoreOfMatch.setText(vm.getScoreOfMatch().toString()
                .replace(", ", "\n\t")
                .replace("{", "\t")
                .replace("}", "")
                .replace("=", " = "));

        if (!vm.isMatchEnded()) {
            CommanderButton continueButton = getContinueCommanderButton();
            buttonHBox.getChildren().add(continueButton.getGUIItem());
        } else {
            endMatchVBox.setVisible(true);
            try {
                Player winnerValue = vm.getWinnerOfTheMatch();
                this.winnerOfMatch.setText(winnerValue != null
                        ? "WIN OF " + winnerValue
                        : "DRAW");

                if (vm.getWinnerOfTheMatch() == null) {
                    CommanderButton extraGameButton = getExtraGameCommanderButton();
                    buttonHBox.getChildren().add(extraGameButton.getGUIItem());
                }
            } catch (Match.MatchNotEndedException e) {
                e.printStackTrace();
            }


            CommanderButton newMatchButton = getNewMatchCommanderButton();
            buttonHBox.getChildren().add(newMatchButton.getGUIItem());
        }
    }


    private CommanderButton getContinueCommanderButton() {
        return new CommanderButton(
                "Continue",
                this,
                getViewmodelAssociatedWithView(),
                continueAfterSummaryPropertyName,
                () -> true);
    }

    private CommanderButton getNewMatchCommanderButton() {
        return new CommanderButton(
                "New Match",
                this,
                getViewmodelAssociatedWithView(),
                newMatchAfterSummaryPropertyName,
                () -> true);
    }

    private CommanderButton getExtraGameCommanderButton() {
        return new CommanderButton(
                "Extra Game",
                this,
                getViewmodelAssociatedWithView(),
                extraGameAfterSummaryPropertyName,
                () -> true);
    }
}
