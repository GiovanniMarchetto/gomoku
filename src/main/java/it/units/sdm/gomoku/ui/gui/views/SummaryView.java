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

import static it.units.sdm.gomoku.ui.gui.GUIMain.mainViewmodel;

public class SummaryView extends View {

    public static final String continueAfterSummaryPropertyName = "continue";
    public static final String newMatchAfterSummaryPropertyName = "newMatch";
    public static final String extraGameAfterSummaryPropertyName = "extraGame";

    @FXML
    private HBox buttonHBox;

    @FXML
    private Label titleOfSummary;
    @FXML
    private Label winnerOfGame;
    @FXML
    private Label winnerOfMatch;

    public SummaryView() {
        super(mainViewmodel);
    }

    @FXML
    private void initialize() {
        var vm = (MainViewmodel) getViewmodelAssociatedWithView();

        String whatEnded = vm.isMatchEnded() ? "MATCH" : "GAME";
        titleOfSummary.setText(whatEnded + " ENDED");
        titleOfSummary.setStyle("-fx-font-size: 20; -fx-font-weight: bold");


        String winnerOfGame = null;
        try {
            Player winnerValue = vm.getWinnerOfTheGame();
            winnerOfGame = winnerValue != null ? winnerValue + " WIN" : "a draw";
        } catch (Game.GameNotEndedException e) {
            e.printStackTrace();
        }
        this.winnerOfGame.setText("This game is ended with: " + winnerOfGame);


        if (!vm.isMatchEnded()) {
            CommanderButton continueButton = getContinueCommanderButton();
            buttonHBox.getChildren().add(continueButton.getGUIItem());
        } else {
            String winnerOfMatch = null;
            try {
                Player winnerValue = vm.getWinnerOfTheMatch();
                winnerOfMatch = winnerValue != null ? winnerValue + " WIN" : "a draw";

                if (vm.getWinnerOfTheMatch() == null) {
                    System.out.println("generate");
                    CommanderButton extraGameButton = getExtraGameCommanderButton();
                    buttonHBox.getChildren().add(extraGameButton.getGUIItem());
                }
            } catch (Match.MatchEndedException e) {
                e.printStackTrace();
            }

            this.winnerOfGame.setText("The match is ended with: " + winnerOfMatch);

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
                () -> null);
    }

    private CommanderButton getNewMatchCommanderButton() {
        return new CommanderButton(
                "New Match",
                this,
                getViewmodelAssociatedWithView(),
                newMatchAfterSummaryPropertyName,
                () -> null);
    }

    private CommanderButton getExtraGameCommanderButton() {
        return new CommanderButton(
                "Extra Game",
                this,
                getViewmodelAssociatedWithView(),
                extraGameAfterSummaryPropertyName,
                () -> null);
    }
}
