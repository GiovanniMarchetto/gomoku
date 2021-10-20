package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import it.units.sdm.gomoku.ui.gui.SceneController;
import it.units.sdm.gomoku.ui.gui.views.SummaryView;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainViewmodel extends Viewmodel {

    private Match match;

    private Game currentGame;

    private Board currentBoard;

    public MainViewmodel() {
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Board.BoardMatrixPropertyName -> {
                Board.ChangedCell cell = (Board.ChangedCell) evt.getNewValue();
                firePropertyChange(Board.BoardMatrixPropertyName, null, cell);
            }
            case Setup.setupCompletedPropertyName -> {
                Setup setup = (Setup) evt.getNewValue();
                match = new Match(setup.getBoardSizeValue(), setup.getNumberOfGames(), setup.getPlayers());
                startNewGame();
                SceneController.passToNewScene(SceneController.ViewName.MAIN_VIEW);
            }
            case Game.gameEndedPropertyName -> {
                endGame();
                SceneController.passToNewScene(SceneController.ViewName.SUMMARY_VIEW);
            }
            case SummaryView.continueAfterSummaryPropertyName -> {
                startNewGame();
                SceneController.passToNewScene(SceneController.ViewName.MAIN_VIEW);
            }
            case SummaryView.newMatchAfterSummaryPropertyName -> {
                SceneController.passToNewScene(SceneController.ViewName.START_VIEW);
            }
            case SummaryView.extraGameAfterSummaryPropertyName -> {
                match.addAnExtraGame();
                startNewGame();
                SceneController.passToNewScene(SceneController.ViewName.MAIN_VIEW);
            }
            default -> throw new IllegalArgumentException("Property name " + evt.getPropertyName() + " not found!");
        }
    }

    public void startNewGame() {
        try {
            currentGame = match.startNewGame();
            currentBoard = currentGame.getBoard();
            observe(currentGame);
            observe(currentBoard);
        } catch (Match.MatchEndedException e) {
            e.printStackTrace();
        }
    }

    public void endGame() {
        // *Avengers theme plays*
        stopObserving(currentGame);
        stopObserving(currentBoard);
    }

    public void placeStone(Coordinates coordinates) {
        try {
            Player currentPlayer = currentGame.getCurrentPlayer();
            Match.executeMoveOfPlayerInGame(currentGame,
                    currentPlayer instanceof CPUPlayer ?
                            ((CPUPlayer) currentPlayer).chooseRandomEmptyCoordinates(currentBoard) : coordinates);
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            e.printStackTrace();
        }
    }

    public int getBoardSize() {
        try {
            return Objects.requireNonNull(currentBoard).getSize();
        } catch (NullPointerException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .severe("The board is null but should not.\n\t" +
                            Arrays.stream(e.getStackTrace())
                                    .map(StackTraceElement::toString)
                                    .collect(Collectors.joining("\n\t")));
            throw e;
        }
    }

    public boolean isMatchEnded() {
        return match.isEnded();
    }

    @Nullable
    public Player getWinnerOfTheMatch() throws Match.MatchEndedException {
        return match.getWinner();
    }

    @Nullable
    public Player getWinnerOfTheGame() throws Game.NotEndedGameException {
        return currentGame.getWinner();
    }
}